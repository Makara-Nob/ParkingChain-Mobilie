#!/bin/bash

# Check OPENAI_API_KEY
if [ -z "$OPENAI_API_KEY" ]; then
  echo "❌ OPENAI_API_KEY is not set."
  exit 1
fi

# Stage all changes if none are staged
if git diff --cached --quiet; then
  echo "🛈 No staged changes detected, staging all..."
  git add .
fi

diff_content=$(git diff --cached)
if [ -z "$diff_content" ]; then
  echo "❌ No changes detected to commit."
  exit 1
fi

echo "🧠 Generating commit message with AI..."

# Save diff to temp file
tmp_diff=$(mktemp)
echo "$diff_content" > "$tmp_diff"

# Call OpenAI API
commit_message_raw=$(curl -s https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -d @- <<EOF
{
  "model": "gpt-5",
  "messages": [
    {"role": "system", "content": "You are an AI that writes concise single-line conventional git commit messages (feat, fix, refactor, chore, docs, style). Output only the commit text without quotes."},
    {"role": "user", "content": "$(cat "$tmp_diff")"}
  ],
  "max_tokens": 50,
  "temperature": 0.2
}
EOF
)

rm "$tmp_diff"

# Parse commit message using sed (Windows/Git Bash safe)
commit_message=$(echo "$commit_message_raw" | sed -n 's/.*"content": "\(.*\)".*/\1/p' | head -n1)

if [ -z "$commit_message" ]; then
  echo "❌ AI failed to generate commit message. Falling back to timestamp..."
  commit_message="chore: commit on $(date +"%Y-%m-%d %H:%M:%S")"
fi

# Switch to development branch
current_branch=$(git branch --show-current)
if [ "$current_branch" != "development" ]; then
  git checkout development
fi

# Commit and push
git commit -m "$commit_message"
git push origin development

echo "✅ Commit created and pushed:"
echo "   $commit_message"
