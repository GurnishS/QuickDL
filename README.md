# 📅 QuickDL

**QuickDL** is a fast, powerful, and offline-first video downloader and player Android app built using **Kotlin + Jetpack Compose**, integrating **Chaquopy** to run `yt-dlp` completely offline. It supports parallel downloads, offline video playback via **ExoPlayer**, and uses **Room** for local video management.

> ⚠️ **Note**: *QuickDL is a **demo version** of an ongoing, more advanced project named **Downsy*** — a full-featured video utility app currently in development with broader capabilities, UI enhancements, and improved parrallel download support.

---

## 🚀 Features

* 🔗 **Supports Many Sites**
  Download videos from YouTube, Instagram, Facebook, Twitter, and more using `yt-dlp`.

* 🔌 **Completely Offline**
  `yt-dlp` runs natively inside the app using [Chaquopy](https://chaquo.com/chaquopy/), without requiring internet access for video extraction.

* 🔄 **Parallel Downloads**
  Download multiple videos at once with safe thread management.

* 🎞 **Built-in Video Player**
  Uses **ExoPlayer** to play downloaded videos directly within the app.

* 🧠 **Smart Download Management**
  Pause, resume, cancel, and track progress with notifications and persistent state(Will be implemented in Downsy).

* 📓 **Room Database**
  All downloaded video metadata is stored locally using Jetpack Room for instant access and persistent storage.

* ✨ **Modern UI**
  Built with Jetpack Compose for a sleek, responsive, and beautiful UI.

---

## 🛠 Tech Stack

| Layer         | Technology        |
| ------------- | ----------------- |
| Language      | Kotlin            |
| UI Framework  | Jetpack Compose   |
| Python Bridge | Chaquopy          |
| Downloader    | yt-dlp (embedded) |
| Video Player  | ExoPlayer         |
| Local DB      | Room              |
| Concurrency   | Kotlin Coroutines |
| Architecture  | MVVM              |

---

## 🧪 How It Works

1. **yt-dlp via Chaquopy**
   Chaquopy embeds a Python environment inside your Android app, so `yt-dlp` is invoked directly from Kotlin using `Python.getInstance()`.

2. **Parallel Downloads**
   Each download task is handled in a coroutine, running in a foreground service. You can manage individual downloads through notifications.

3. **Room Integration**
   Video metadata (title, URL, duration, thumbnail, file path) is stored using Room for retrieval even after app restarts.

4. **ExoPlayer Integration**
   Downloaded videos are played using ExoPlayer with support for full-screen, playback speed control, and gesture seeking.

---

## 🚳 Requirements

* Android Studio Giraffe or higher
* Android SDK 24+
* Chaquopy plugin
* Internet access for initial build to include yt-dlp (afterwards works offline)

---

## 🛫 Building the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/GurnishS/QuickDL.git
   cd QuickDL
   ```

2. Make sure you have `yt-dlp` installed in the `python` folder:

   ```
   app/src/main/python/yt_dlp/
   ```

3. Sync project with Gradle files.

4. Run on a real device (recommended for testing Chaquopy and downloads).

---

## 📸 Screenshots
![image](https://github.com/user-attachments/assets/c2585540-94b4-4134-aedc-4ec1a7ff77ab)
![image](https://github.com/user-attachments/assets/b7c21bb3-d64f-48f4-b405-4365f1396bab)
![image](https://github.com/user-attachments/assets/4c0515d8-32ba-414e-b383-77e3e981f69a)
![image](https://github.com/user-attachments/assets/32e13422-fa0d-49d3-9d5d-e548724c10a7)

---

## 🚧 About Downsy (Coming Soon)

**Downsy** is the upcoming full release based on QuickDL. It will include:

* Enhanced UI/UX
* Scheduled downloads
* Stream while downloading
* External Storage(allowing file sharing)
* FFMPEG video and audio muxing
* Playlist & channel support
* Foreground Service
* Notifications
* Custom Commands
* Preferences and Customisation

> Stay tuned for more updates on **Downsy** — the next-gen video companion app.
[Downsy](https://github.com/GurnishS/Downsy)

---

## ⚠️ Disclaimer

This app is intended for **personal use only**. Ensure you comply with the Terms of Service of content providers before downloading content.

---

## 📝 License

MIT License

---

## 💬 Acknowledgements

* [yt-dlp](https://github.com/yt-dlp/yt-dlp)
* [Chaquopy](https://chaquo.com/chaquopy/)
* [ExoPlayer](https://github.com/google/ExoPlayer)
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
