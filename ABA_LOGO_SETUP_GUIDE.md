# ABA PayWay Image Setup Guide

## 📁 Where to Put the Image

You need to place your **`aba-logo.png`** image in the Android drawable resources folder:

### **Correct Path:**
```
smart-parking-mobile/
└── app/
    └── src/
        └── main/
            └── res/
                └── drawable/    ← PUT IMAGE HERE
                    └── aba_logo.png
```

### **Full Absolute Path:**
```
D:\RUPP WORK\CS Y4\smart-parking-mobile\app\src\main\res\drawable\aba_logo.png
```

---

## 📝 Important Notes

### **File Naming Rules:**
- ✅ Use lowercase letters only: `aba_logo.png`
- ✅ Use underscores instead of hyphens: `aba_logo.png`
- ❌ Don't use: `aba-logo.png` (hyphens not allowed)
- ❌ Don't use: `AbaLogo.png` (uppercase not allowed)

### **Image Recommendations:**
- **Format:** PNG with transparent background (recommended)
- **Size:** 200x200px to 512x512px works well
- **Aspect Ratio:** Square or rectangular logo
- **File Size:** Keep under 100KB for faster loading

---

## 🎨 What I Changed

### **Updated Component:**
`DigitalWalletOptions.kt` now displays:

#### **Before:**
- Multiple payment options (ABA, Wing, Pi Pay, True Money)
- Emoji icons
- Button style

#### **After:**
- **Single ABA PayWay option only**
- **ABA logo image** (48x48dp)
- **Modern card design** with:
  - Badge with "ABA Payment" title
  - Subtitle: "Pay with ABA PayWay"
  - Radio button selection
  - Blue highlight when selected
  - Rounded corners (16dp)

---

## 🔧 How It Works

The component automatically:
1. **Selects ABA PayWay by default** (pre-selected)
2. **Displays your logo** from `R.drawable.aba_logo`
3. **Shows title and subtitle**
4. **Highlights when clicked** (blue background)
5. **Calls payment handler** when selected

---

## 📸 Expected Visual Result

```
┌─────────────────────────────────────────────────┐
│  [ABA Logo]  ABA Payment                    (•) │
│              Pay with ABA PayWay                │
└─────────────────────────────────────────────────┘
```

- Logo appears on the left (48x48dp)
- Text in the middle
- Radio button on the right
- Entire card is clickable

---

## 🚀 Next Steps

1. **Get your ABA logo image** (PNG format preferred)
2. **Rename it to:** `aba_logo.png` (lowercase, underscore)
3. **Copy to:** `app/src/main/res/drawable/aba_logo.png`
4. **Rebuild the app** (the image will be auto-detected)

---

## ❓ If Image Doesn't Exist Yet

If you don't have the image right now, you can:

### **Option 1: Use a placeholder**
I can create a simple colored rectangle placeholder until you get the real logo.

### **Option 2: Download ABA logo**
Search for "ABA Bank Cambodia logo PNG" online and download it.

### **Option 3: Create fallback**
Use a text-based fallback instead of an image (let me know if you want this).

---

## 🐛 Troubleshooting

### **Error: "Cannot resolve symbol 'aba_logo'"**
- ✅ Make sure the file is named exactly `aba_logo.png`
- ✅ Check it's in `app/src/main/res/drawable/` 
- ✅ Clean and rebuild: Build → Clean Project → Rebuild Project

### **Image looks pixelated**
- Use a higher resolution image (at least 200x200px)
- Use vector drawable format (SVG/XML) instead

### **Image is too large**
- Compress the PNG file (remove metadata)
- Use online tools like TinyPNG
- Or convert to WebP format

---

## 📂 Alternative Drawable Folders

You can also use resolution-specific folders:

```
drawable/         ← Default (put your image here)
drawable-hdpi/    ← High density screens
drawable-xhdpi/   ← Extra high density
drawable-xxhdpi/  ← Extra extra high density
drawable-xxxhdpi/ ← Extra extra extra high density
```

**Recommendation:** Just use the main `drawable/` folder for simplicity.

---

Let me know if you need help getting or creating the ABA logo image!
