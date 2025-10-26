#!/bin/bash

# Switch to development branch
current_branch=$(git branch --show-current)
if [ "$current_branch" != "development" ]; then
  echo "Switching to development branch..."
  git checkout development
fi

# Pull latest changes
echo "🔄 Pulling latest changes from development..."
git pull origin development

echo "✅ Pull complete!"
