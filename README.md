# SkyNet Reader

<p align="center">
  <img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/app/src/main/res/mipmap-mdpi/ic_launcher.png" height="120" width="120" alt="SkyNet Reader">
</p>

<p align="center">
  <strong>A modern, open-source Android news reader built with Jetpack Compose.</strong><br>
  Categorised feeds &bull; Offline reading &bull; On-device AI chat &bull; Material You theming
</p>

<p align="center">
  <a href="https://github.com/spkdroid/SkyNet-Reader/blob/master/license.md"><img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="License: MIT"></a>
  <img src="https://img.shields.io/badge/min%20SDK-24-brightgreen" alt="Min SDK 24">
  <img src="https://img.shields.io/badge/target%20SDK-35-brightgreen" alt="Target SDK 35">
  <img src="https://img.shields.io/badge/Kotlin-1.9-purple" alt="Kotlin 1.9">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-latest-blue" alt="Jetpack Compose">
</p>

---

## About the Project

SkyNet Reader started in **2016** as a lightweight Java-based news feed app using Volley, ListView, and the Android Support Library. Over the years, the Android ecosystem evolved dramatically — and so did SkyNet Reader.

In early **2026**, the project was rebuilt from the ground up: every line of Java was replaced with idiomatic Kotlin, the UI was migrated from Fragments and XML layouts to a single-Activity Jetpack Compose architecture, and modern libraries (Hilt, Retrofit, Room, Coil, WorkManager) replaced the legacy stack. The result is a clean, testable, and maintainable codebase that follows Clean Architecture principles.

Today, SkyNet Reader serves as both a real-world news reader and a reference project demonstrating current Android best practices.

## Screenshot

<p align="center">
  <img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/screenshot/screen.png" height="700" width="275" alt="SkyNet Reader Screenshot">
</p>

---

## Features

- **Categorised news feeds** — World, Entertainment, Business, Technology, and Politics tabs with swipeable horizontal paging
- **Offline-first architecture** — articles cached in Room; read news without a connection
- **Article detail view** — in-app browser with reader mode, estimated reading time, and font size controls
- **Bookmarks** — save articles locally for later; swipe-to-delete with undo
- **Full-text search** — real-time filtering across cached and remote articles
- **On-device AI chat** — private, offline conversations powered by MediaPipe's Gemma model
- **Dark mode & Material You** — dynamic colour theming on Android 12+, manual toggle in settings
- **Onboarding flow** — first-launch walkthrough built in pure Compose
- **Background sync** — WorkManager refreshes headlines every 6 hours on Wi-Fi
- **Dashboard** — at-a-glance stats with article counts, bookmark totals, and recent headlines

---

## Architecture

The app follows **Clean Architecture** with **MVVM** at the presentation layer.

```
┌───────────────────────────────────────────────────────┐
│              Presentation (Compose + ViewModel)        │
│  Single Activity → NavHost → Composable screens        │
│                 observes StateFlow                      │
└────────────────────┬──────────────────────────────────┘
                     │
┌────────────────────▼──────────────────────────────────┐
│              Domain (Use Cases + Models)                │
│  GetNewsFeedUseCase · SearchNewsUseCase                 │
│  BookmarkArticleUseCase · GetBookmarksUseCase           │
└────────────────────┬──────────────────────────────────┘
                     │
┌────────────────────▼──────────────────────────────────┐
│              Data (Repository + DataSources)            │
│  Retrofit (remote) · Room (local) · DataStore (prefs)  │
└───────────────────────────────────────────────────────┘
```

### Key patterns

| Pattern | Where |
|---------|-------|
| MVVM | Every screen — ViewModel exposes `StateFlow<UiState>` |
| Repository | `NewsRepository`, `BookmarkRepository` — single source of truth |
| Use Cases | One class per business action |
| Dependency Injection | Hilt — all ViewModels, repos, and data sources |
| Single Activity | `MainActivity` hosts all Compose navigation |
| Offline-first | Room is always the source of truth; Retrofit updates it in the background |

---

## Tech Stack

| Layer | Library |
|-------|---------|
| Language | Kotlin 1.9 |
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| DI | Hilt |
| Networking | Retrofit 2 + OkHttp 4 |
| Images | Coil (Compose) |
| Database | Room |
| Preferences | DataStore |
| Background work | WorkManager |
| AI inference | MediaPipe LLM (Gemma 3 1B) |
| Analytics | Firebase Analytics + Crashlytics |
| Testing | JUnit 4, MockK, Turbine, Espresso |

---

## Getting Started

### Prerequisites

- **Android Studio** Iguana (2024.1) or newer
- **JDK 17**
- **Android SDK 35**

### Build & Run

```bash
# Clone the repository
git clone https://github.com/spkdroid/SkyNet-Reader.git
cd SkyNet-Reader

# Build the debug APK
./gradlew assembleDebug

# Install on a connected device
./gradlew installDebug
```

The debug APK is written to `app/build/outputs/apk/debug/`.

### Firebase (optional)

The project has Firebase dependencies for analytics and crashlytics. To enable them:

1. Create a project on the [Firebase Console](https://console.firebase.google.com/)
2. Download `google-services.json` and place it in `app/`
3. Uncomment `apply plugin: 'com.google.gms.google-services'` in `app/build.gradle`

Without this file the app still builds and runs — Firebase features are simply inactive.

### AI Chat (optional)

The chat screen uses an on-device Gemma 3 model. To enable it:

1. Download [`gemma-3-1b-it-int4.task`](https://huggingface.co/google/gemma-3-1b-it) (~600 MB)
2. Push to the device:
   ```bash
   adb push gemma-3-1b-it-int4.task /data/data/com.news.skynet.debug/files/
   ```
3. Open the AI Chat tab in the app

---

## Project Structure

```
app/src/main/java/com/news/skynet/
├── AppController.kt                 # @HiltAndroidApp + WorkManager config
├── MainActivity.kt                  # Single-Activity Compose host
├── di/                              # Hilt modules (App, Network, Database)
├── data/
│   ├── remote/                      # Retrofit service, DTOs, remote data source
│   ├── local/                       # Room database, DAOs, entities
│   └── repository/                  # NewsRepository, BookmarkRepository
├── domain/
│   ├── model/                       # NewsArticle, NewsCategory
│   └── usecase/                     # GetNewsFeed, Search, Bookmark use cases
├── ui/
│   ├── compose/
│   │   ├── navigation/              # NavGraph, Screen routes, shared VM
│   │   ├── screens/                 # All Composable screens
│   │   ├── components/              # Reusable composables (NewsArticleCard)
│   │   └── theme/                   # Color, Typography, Theme
│   ├── feed/                        # NewsFeedViewModel, UiState
│   ├── detail/                      # ArticleDetailViewModel
│   ├── dashboard/                   # DashboardViewModel
│   ├── search/                      # SearchViewModel
│   ├── bookmarks/                   # BookmarksViewModel
│   ├── chat/                        # ChatViewModel, ChatMessage
│   └── settings/                    # SettingsViewModel
├── util/                            # NetworkResult sealed class
└── worker/                          # NewsRefreshWorker (periodic sync)
```

---

## API

SkyNet Reader fetches news from a custom REST API. The backend, written in PHP, aggregates content from various sources and exposes it as JSON.

| Category | Endpoint |
|----------|----------|
| World | `http://www.spkdroid.com/News/canada.php?type=1` |
| Entertainment | `http://www.spkdroid.com/News/canada.php?type=2` |
| Business | `http://www.spkdroid.com/News/canada.php?type=3` |
| Technology | `http://www.spkdroid.com/News/canada.php?type=4` |
| Politics | `http://www.spkdroid.com/News/canada.php?type=5` |

---

## Roadmap

The project is developed in phases. See [roadmap.md](roadmap.md) for the detailed plan.

| Phase | Description | Status |
|-------|-------------|--------|
| **Phase 1** — Foundation | Kotlin migration, MVVM, Hilt, Retrofit, Room, Compose | Done |
| **Phase 2** — Core Features | Offline reading, detail view, bookmarks, search, dark mode, onboarding, background sync | Done |
| **Phase 3** — UX Polish | Paging 3, animations, shimmer loading, transition polish | Planned |
| **Phase 4** — Advanced Features | FCM push notifications, home screen widget, AI summaries, Firebase Analytics | Planned |
| **Phase 5** — Quality & Distribution | CI/CD with GitHub Actions, Baseline Profiles, full test coverage | Planned |

---

## Contributing

Contributions are welcome! Whether it's a bug fix, new feature, or documentation improvement — feel free to open an issue or submit a pull request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please make sure your code compiles and passes existing tests before submitting.

---

## Author

**Ramkumar Velmurugan**
[Portfolio](http://www.spkdroid.com/CV/) · [GitHub](https://github.com/spkdroid)

---

## License

This project is licensed under the MIT License — see the [license.md](license.md) file for details.

Copyright (c) 2016 Ramkumar Velmurugan

---

## 📚 Acknowledgments

- **Apache HTTPComponents**: For HTTP request handling.  
- **CustomWebView**: For enhanced web content rendering.  
- **Volley**: For seamless and efficient API communication.

--- 
