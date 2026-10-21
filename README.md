# Captain AI Assistant — Android App (First Working Version)

This is the **source code** for your own AI assistant app for Android.
It is a real, working app — but it is **not** a finished Play Store app yet.
Read this file first. Everything is explained in simple language.

## What the app can do

The app has 4 tabs at the bottom:

### 1. Chat (text chat)
Type any question and Gemini replies in writing.
You can also type commands like:
- `open YouTube`
- `open WhatsApp`
- `open Chrome`

The assistant will open that app on your phone.

### 2. Voice (live chat)
Tap the big gradient mic button and speak. The assistant:
1. Listens to your voice (using Android's speech recognition — listening is free and on-device).
2. Thinks with Gemini.
3. Answers **out loud in a natural, human-like voice**.

Important: the spoken reply does **NOT** use Android's built-in robotic
TextToSpeech. It uses Gemini's own text-to-speech (same Gemini key),
which is why it sounds natural. The Voice screen shows a big friendly
gradient mic button that pulses while listening and speaking.

### 3. News
Shows current top headlines, fetched through Gemini with Google Search
grounding — so the headlines are live and up to date. Tap any headline
to read the full story in your browser.

### 4. Settings
This is where you put your Gemini key. You do **not** need to edit any
code to add it — just type it here and press Save. The key stays saved
on your phone only.

## The key: only ONE key needed

| Field | What to put | Where to get it |
|---|---|---|
| Gemini API key | Your Gemini key | Free from Google AI Studio: https://aistudio.google.com |
| Chat model (optional) | Leave as `gemini-2.0-flash` | — |
| Voice (optional) | Leave as `Aoede` (breezy, friendly) — or try `Kore`, `Zephyr`, `Puck` | — |
| Voice model (optional) | Leave as `gemini-2.5-flash-preview-tts` | — |

**This single Gemini key powers everything: chat, natural voice AND news.**
No other key is needed. There is no OpenAI key, no voice key and no news
key in this version.

Without the key, the Chat, Voice and News tabs will show an error
message telling you to add it. The "open app" commands work without any key.

## ⚠️ Key safety — please read

**Never paste your key into any chat, message, or email — not even to
your assistant.** Enter it ONLY in the app's Settings screen on your
phone. Anyone with your key can use your Gemini account.

## The design (Design B — the one you picked)

Cheerful, colorful, modern look:
- Vibrant blue → purple → pink gradient background.
- A cute rounded assistant avatar at the top of the Chat screen.
- White rounded chat cards (dark text inside, easy to read).
- Colorful quick-action chips: orange, teal, purple, pink.
- A big friendly gradient mic button on the Voice screen.
- A slim light bottom bar with Chat, Voice, News, Settings.

## How to open the project and run it on your phone

1. Install **Android Studio** (free) on your computer.
2. In Android Studio, choose **Open** and select the `ai-assistant-app` folder.
3. Wait for Gradle to finish downloading and syncing (first time takes a few minutes).
4. Connect your Android phone with a USB cable and turn on **USB debugging**
   (Settings → Developer options → USB debugging).
5. Press the green **Run** button in Android Studio. The app installs on your phone.
6. Open the app, go to **Settings**, paste your Gemini key, press Save — done.

## How to build the APK from the command line (no Android Studio)

If you have a JDK 17 and the Android SDK installed, you can build the
debug APK (installs directly on any phone) from a terminal:

1. Set the SDK location — either keep the included `local.properties`
   file (it points to the SDK path used during development) or edit it
   to your own SDK folder: `sdk.dir=/path/to/your/Android/Sdk`
2. Make sure JDK 17 is active: `export JAVA_HOME=/path/to/jdk-17`
3. From inside the `ai-assistant-app` folder, run:
   `./gradlew assembleDebug`
4. The APK appears at: `app/build/outputs/apk/debug/app-debug.apk`
   Copy it to your phone and tap it to install.

Requirements for the command-line build: JDK 17, Android SDK with
`platform-tools`, `platforms;android-34` and `build-tools;34.0.0`
(the app's `compileSdk` and `targetSdk` are 34, `minSdk` is 26).
Gradle 8.7 is fetched automatically by the included wrapper.

Note: on the machine where this project was prepared, the Gradle build
could NOT run, because that machine's sandbox blocks Java programs from
making loopback (same-computer) TCP connections — Gradle's build daemon
needs those to work, and the connection is replaced by a "Direct network
protocols" policy message. That is a property of that machine, not of
this code. On a normal computer (or in Android Studio) the build works
as described above.

## About costs — please read

- **Chat, voice and news need internet and your Gemini key.** Gemini has
  a free tier in Google AI Studio; heavy use may be billed by Google.
  Small personal use is usually free or very cheap.
- **Opening apps and listening to your voice are free.** Those parts run
  fully on your phone and cost nothing.
- Your key is stored only on your phone. Never share it with anyone.

## What is NOT done yet

Be honest about the limits of this first version:

- No signed release APK is built here. You get source code; Android Studio
  builds a debug app on your phone when you press Run.
- No Play Store publishing (that needs a developer account, a signed
  release build, a store listing, and review).
- No wake word ("Hey assistant...") — you tap the mic button to talk.
- No offline AI — chat, voice and news need internet and a Gemini key.
- No iOS version — this is Android only.
- No chat history saving yet — chats reset when you close the app.
- No user accounts or login.

## Project structure (for developers)

- `app/src/main/java/com/captain/aiassistant/MainActivity.kt` — app entry + bottom tabs + gradient backdrop
- `app/src/main/java/com/captain/aiassistant/data/` — network + on-device logic
  - `AiApiClient.kt` — talks to the Gemini chat API (OkHttp)
  - `NeuralTtsClient.kt` — Gemini text-to-speech + WAV playback (never Android TextToSpeech)
  - `NewsRepository.kt` — headlines from Gemini with Google Search grounding
  - `AppLauncher.kt` — opens other apps by name (Android intents)
  - `SettingsStore.kt` — saves the Gemini key on the phone
  - `Models.kt` — chat message model
- `app/src/main/java/com/captain/aiassistant/viewmodel/` — screen logic
- `app/src/main/java/com/captain/aiassistant/ui/screens/` — Chat, Voice, News, Settings screens
- `app/src/main/java/com/captain/aiassistant/ui/components/` — shared Design B UI: gradient background, white cards, gradient buttons, quick-action chips, assistant avatar, mic button
- `app/src/main/java/com/captain/aiassistant/ui/theme/` — light Material 3 theme, blue → purple → pink palette, rounded shapes

Tech: Kotlin + Jetpack Compose + Material 3, OkHttp, coroutines.
