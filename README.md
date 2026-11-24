# Big Clock: iPhone-style real-time clock widget for Samsung Galaxy

A big, transparent home-screen clock in the style of the iPhone lock screen.
In the app you choose:

- **Style presets**: Tall, Tall bold, Classic, Heavy, Stacked, Light, Thin
- **Font**: Inter (closest free match to Apple's SF Pro), Inter Tight, Manrope, Roboto Condensed, or your phone's font
- **Weight**: Thin to Heavy
- **Colour**: Glass white, White, Ice blue, Neon cyan, Pink, Gold, Dark
- **Width** (digit size) and **Height** (vertical stretch 100–250%)
- Seconds, 24-hour time, date above or below, stacked hours/minutes

Tap the widget to open Samsung Clock. Works on Android 8.0+; stretching needs Android 12+.

## Get the APK (pick one)

### Easiest: download from Releases
Open the repo's **Releases** → **latest** → tap **BigClock.apk**.

### A. Build it yourself on GitHub
1. Create a free GitHub account and a new repository.
2. Upload all the files in this folder (keep the folder structure, including `.github`).
3. Open the **Actions** tab → wait ~3 min for "Build APK" to turn green.
4. Open the run → download **BigClock-apk** → unzip → `app-debug.apk`.

### B. Android Studio
1. Unzip, then **File → Open** this folder.
2. Let Gradle sync, plug in your Galaxy (USB debugging on) and press **Run ▶**,
   or **Build → Build APK(s)**.

## Install on your Galaxy
1. Copy `app-debug.apk` to the phone and tap it.
2. Allow "Install unknown apps" for My Files / Chrome when asked.
3. Open **Big Clock**, pick a style, tap **Add widget to home screen**.
   (Or long-press home screen → Widgets → Big Clock.) Resize it by long-pressing.

## Why it's battery friendly
The time is drawn by Android's `TextClock`, which the launcher updates itself every second —
no background service, no alarms, no polling.

## How the options work
Widgets can't change font, weight or stretch while running, so `tools/gen_layouts.py`
generates one layout per combination (150 layouts) and the app picks the right one.
Size, colour and the other switches are applied directly.

Fonts are from Google Fonts under the SIL Open Font License (see `tools/OFL-Inter.txt`).
