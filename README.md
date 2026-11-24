# Neon Clock — real-time clock widget for Samsung Galaxy

A glowing home-screen (and lock-screen) clock widget with live ticking seconds,
date, and 6 styles: Neon, Sunset, Aurora, AMOLED, Glass, and fully Transparent (digits float on your wallpaper). Tap the widget to open Samsung Clock.
Works on Android 8.0+ (all recent Galaxy phones, One UI 1–8).

## Get the APK (pick one)

### A. No computer setup — GitHub builds it for you
1. Create a free GitHub account and a new repository.
2. Upload all the files in this folder (keep the folder structure, including `.github`).
3. Open the **Actions** tab → wait ~3 min for "Build APK" to turn green.
4. Open the run → download **NeonClock-apk** → unzip → `app-debug.apk`.

### B. Android Studio
1. Unzip, then **File → Open** this folder.
2. Let Gradle sync, plug in your Galaxy (USB debugging on) and press **Run ▶**,
   or **Build → Build APK(s)**.

## Install on your Galaxy
1. Copy `app-debug.apk` to the phone and tap it.
2. Allow "Install unknown apps" for My Files / Chrome when asked.
3. Open **Neon Clock**, pick a style, tap **Add widget to home screen**.
   (Or long-press home screen → Widgets → Neon Clock.) Resize it by long-pressing.

## Why it's battery friendly
The time is drawn by Android's `TextClock`, which the launcher updates itself every second —
no background service, no alarms, no polling.
