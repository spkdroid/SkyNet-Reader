# SkyNet Reader — Product Roadmap

> Last updated: March 20, 2026

---

## Vision

**SkyNet Reader** is a multiplatform, AI-powered news reader that delivers a premium reading experience on every device — Android, iOS, Desktop (macOS/Windows/Linux), and Web. The product combines local-first intelligence with a beautiful, performant UI built on Compose Multiplatform.

---

## Current State (v1.0) ✅

The Android app is live with the following stack:

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9.23 |
| UI | Jetpack Compose (BOM 2024.04.01), Material 3 |
| Architecture | Clean Architecture + MVVM, single-Activity |
| DI | Hilt / Dagger 2.50 |
| Networking | Retrofit 2.9 + OkHttp 4.12 |
| Database | Room 2.6.1 (offline-first) |
| Preferences | Jetpack DataStore |
| Images | Coil 2.5 (Compose) |
| AI | MediaPipe LLM (Gemma 3 1B on-device) |
| Background | WorkManager 2.9 (6-hour periodic sync) |
| SDK | minSdk 24, targetSdk 35, compileSdk 35 |

### Completed milestones

- Full Kotlin rewrite (was Java)
- Jetpack Compose migration (was Fragments + XML)
- MVVM + Clean Architecture with Use Cases
- Offline-first caching (Room)
- Categorised news feed with tab pager
- Full-text search
- Bookmark system
- On-device AI chat (Gemma 3 1B via MediaPipe)
- Dark mode / Material You theming
- Onboarding flow
- Settings screen (dark mode, cache management)
- WorkManager background sync

---

## Architecture — Current & Target

### Current (Android-only)

```
┌──────────────────────────────────────────────────┐
│              Android App (Compose)               │
│  MainActivity → NavGraph → Screens → ViewModels  │
├──────────────────────────────────────────────────┤
│   Domain (Use Cases, Models) — pure Kotlin       │
├──────────────────────────────────────────────────┤
│   Data (Retrofit, Room, DataStore)               │
└──────────────────────────────────────────────────┘
```

### Target (Kotlin Multiplatform)

```
┌───────────┬───────────┬─────────────┬────────────┐
│  Android  │    iOS    │   Desktop   │    Web     │
│  Compose  │  SwiftUI  │  Compose    │  Compose   │
│           │  bridge   │  Desktop    │  WASM/JS   │
└─────┬─────┴─────┬─────┴──────┬──────┴─────┬──────┘
      │           │            │            │
┌─────▼───────────▼────────────▼────────────▼──────┐
│            Shared KMP Module (commonMain)         │
│                                                   │
│  ┌─────────────────────────────────────────────┐  │
│  │  Presentation: shared ViewModels (KMP-VM)   │  │
│  ├─────────────────────────────────────────────┤  │
│  │  Domain: Use Cases, Models, Business Logic  │  │
│  ├─────────────────────────────────────────────┤  │
│  │  Data: Ktor client, SQLDelight, DataStore   │  │
│  └─────────────────────────────────────────────┘  │
│                                                   │
│  Platform expect/actual:                          │
│    • androidMain — Hilt, Room bridge, MediaPipe   │
│    • iosMain    — Darwin networking, CoreML       │
│    • desktopMain — JVM SQLite, ONNX Runtime       │
│    • webMain    — Fetch API, IndexedDB             │
└───────────────────────────────────────────────────┘
```

---

## Phase 1 — Android Polish & Hardening (v1.1)

> **Status:** Next up
> **Goal:** Production-quality Android release before going multiplatform.

### 1.1 Dashboard Redesign
- Hero card for breaking/top story with large image + gradient overlay
- "Trending Now" horizontal scroll with category chips
- "For You" section — personalized feed based on reading history
- Quick-action cards (Search, Bookmarks, AI Chat)
- Animated pull-to-refresh with branded indicator
- Shimmer loading skeletons instead of spinner

### 1.2 Article Detail Improvements
- Reader mode with typography controls (font, size, line spacing)
- Estimated reading time in header
- Share sheet with deep link + preview
- Text-to-speech playback
- In-article AI summary button (Gemma 3)

### 1.3 Push Notifications
- Firebase Cloud Messaging integration
- Per-category notification channels
- Breaking news deep-link into article detail
- Notification preferences in Settings

### 1.4 Home Screen Widget
- Glance (Compose) widget showing top 3 headlines
- Tapping a headline deep-links into the app
- Periodic 30-minute refresh via WorkManager

### 1.5 Analytics & Crash Reporting
- Firebase Analytics — custom events: `article_opened`, `category_switched`, `search_performed`, `bookmark_toggled`
- Firebase Crashlytics — automatic crash + non-fatal error reporting
- Privacy-first: analytics opt-out toggle in Settings

### 1.6 Testing & CI
- Unit tests: JUnit 5 + MockK + Turbine (Flow assertions)
- UI tests: Compose testing APIs + Hilt test runner
- GitHub Actions CI: lint → test → assembleRelease on every PR
- Baseline Profiles for 30% faster cold startup

---

## Phase 2 — Kotlin Multiplatform Foundation (v2.0)

> **Goal:** Extract shared business logic into a KMP module. Android continues working as-is; iOS gets a native SwiftUI shell.

### 2.1 Project Restructuring

```
SkyNet-Reader/
├── shared/                          ← NEW: KMP shared module
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/       ← Domain + Data + ViewModels
│       ├── commonTest/kotlin/       ← Shared tests
│       ├── androidMain/kotlin/      ← Android-specific implementations
│       ├── iosMain/kotlin/          ← iOS-specific implementations
│       ├── desktopMain/kotlin/      ← JVM Desktop implementations
│       └── webMain/kotlin/          ← Kotlin/WASM or Kotlin/JS
├── androidApp/                      ← Existing Android app (Compose UI)
│   ├── build.gradle.kts
│   └── src/main/
├── iosApp/                          ← NEW: Xcode project (SwiftUI)
│   └── SkyNetReader/
├── desktopApp/                      ← NEW: Compose Desktop app
│   └── src/main/
├── webApp/                          ← NEW: Compose for Web / WASM
│   └── src/main/
└── build.gradle.kts                 ← Root with KMP plugin
```

### 2.2 Shared Module — Library Migrations

| Android-only | KMP Replacement | Scope |
|---|---|---|
| Retrofit + OkHttp | **Ktor Client** | `commonMain` with engine per platform |
| Room | **SQLDelight** | `commonMain` — generates Kotlin from SQL |
| Jetpack DataStore | **Multiplatform Settings** (or DataStore KMP) | `commonMain` |
| Hilt (Dagger) | **Koin** | `commonMain` — pure Kotlin DI |
| Coil | **Kamel** or Coil 3 (KMP) | `commonMain` |
| Kotlin Serialization | Replace Gson | `commonMain` — multiplatform JSON |
| kotlinx-datetime | Replace java.time | `commonMain` — multiplatform date/time |
| KMP-ViewModel | Shared ViewModels | `commonMain` — lifecycle-aware on all platforms |

### 2.3 Domain Layer (Pure Kotlin — moves unchanged)
- `NewsArticle`, `NewsCategory`, `BookmarkEntity` → `commonMain`
- `GetNewsFeedUseCase`, `SearchNewsUseCase`, `BookmarkArticleUseCase` → `commonMain`
- `NetworkResult` sealed class → `commonMain`

### 2.4 Data Layer (Ktor + SQLDelight)

```kotlin
// commonMain — Ktor replaces Retrofit
class NewsApiClient(private val client: HttpClient) {
    suspend fun getNewsFeed(type: Int): List<NewsDto> =
        client.get("canada.php") { parameter("type", type) }.body()
}

// commonMain — SQLDelight replaces Room
// news.sq
CREATE TABLE NewsArticle (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    summary TEXT NOT NULL,
    url TEXT NOT NULL,
    imageUrl TEXT NOT NULL,
    publishedAt TEXT NOT NULL,
    categoryType INTEGER NOT NULL,
    cachedAt INTEGER NOT NULL
);

selectByCategory:
SELECT * FROM NewsArticle WHERE categoryType = ? ORDER BY cachedAt DESC;
```

### 2.5 Platform Engines

```kotlin
// androidMain
actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) { json() }
    install(Logging) { level = LogLevel.BODY }
}

// iosMain
actual fun createHttpClient(): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) { json() }
}

// desktopMain
actual fun createHttpClient(): HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) { json() }
}
```

---

## Phase 3 — iOS App (v2.1)

> **Goal:** Native iOS app with SwiftUI shell consuming the shared KMP module.

### 3.1 SwiftUI Integration
- Xcode project under `iosApp/`
- KMP shared module published as an XCFramework via `embedAndSignAppleFrameworkForXcode`
- SwiftUI views call shared Kotlin ViewModels via SKIE (Swift-Kotlin Interface Enhancer)

### 3.2 iOS Feature Parity

| Feature | Implementation |
|---|---|
| News feed with categories | SwiftUI `TabView` + `LazyVStack`, shared ViewModel |
| Article detail | `SFSafariViewController` or custom SwiftUI WebView |
| Bookmarks | Shared SQLDelight storage |
| Search | Shared Use Case, SwiftUI `searchable()` modifier |
| Dark mode | Automatic via SwiftUI `@Environment(\.colorScheme)` |
| AI chat | Core ML with Gemma 3 (or shared ONNX model) |
| Push notifications | APNs via shared notification handler |
| Offline reading | SQLDelight cache (same as Android) |

### 3.3 iOS-Specific Enhancements
- Live Activities for breaking news (iOS 16.1+)
- Dynamic Island support
- Widget with WidgetKit (SwiftUI)
- Haptic feedback on interactions
- Share Extension for saving articles from Safari
- Spotlight search indexing for bookmarked articles

---

## Phase 4 — Desktop App (v2.2)

> **Goal:** Compose Desktop app for macOS, Windows, and Linux.

### 4.1 Compose Desktop Shell
- Single `desktopApp/` module using `org.jetbrains.compose` plugin
- Window management: resizable, multi-window for article detail
- Keyboard shortcuts: `⌘/Ctrl+F` search, `⌘/Ctrl+D` bookmark, `←/→` navigate categories
- System tray icon with breaking news notifications
- Native file menu (File, Edit, View, Help)

### 4.2 Desktop-Specific Features

| Feature | Implementation |
|---|---|
| Multi-column layout | Responsive: list + detail side-by-side on wide screens |
| Native notifications | JVM system tray `TrayIcon.displayMessage()` |
| Offline database | SQLDelight with JVM SQLite driver |
| AI chat | ONNX Runtime for Gemma 3 on JVM |
| Drag & drop | Drag articles to bookmarks panel |
| Export | Export bookmarks as PDF/HTML/Markdown |
| Auto-update | GitHub Releases + Sparkle (macOS) / WinSparkle (Windows) |

### 4.3 Distribution
- macOS: `.dmg` notarized with Apple Developer ID
- Windows: `.msi` installer via WiX or NSIS
- Linux: `.deb` / `.rpm` / Flatpak / AppImage
- All via GitHub Actions CI/CD

---

## Phase 5 — Web App (v2.3)

> **Goal:** Compose for Web (Kotlin/WASM) for browser access.

### 5.1 Compose for Web Shell
- `webApp/` module targeting Kotlin/WASM (preferred) or Kotlin/JS
- Progressive Web App (PWA) with service worker for offline reading
- Responsive design: mobile-first, adapts to tablet/desktop widths

### 5.2 Web-Specific Features

| Feature | Implementation |
|---|---|
| SEO | Server-side rendering for article pages (optional Ktor backend) |
| Offline | Service Worker + IndexedDB (via SQLDelight JS driver) |
| Push | Web Push API for breaking news |
| AI chat | WebAssembly ONNX or API fallback |
| Sharing | Web Share API on mobile browsers |
| Install | PWA "Add to Home Screen" prompt |
| URL routing | Browser history integration with Compose Navigation |

---

## Phase 6 — Shared Backend & Sync (v3.0)

> **Goal:** Cloud sync layer so users can access bookmarks, preferences, and reading history across all devices.

### 6.1 Backend (Ktor Server)
- Ktor server deployed on Railway / Fly.io / Cloud Run
- REST API + optional WebSocket for real-time breaking news push
- PostgreSQL for user data, Redis for session cache
- Authentication: Firebase Auth (Google, Apple, Email) or Supabase

### 6.2 Cross-Device Sync
- Bookmarks, reading history, preferences synced via backend
- Conflict resolution: last-write-wins with timestamps
- Offline queue: changes stored locally, synced when online
- E2E encryption option for bookmarks (privacy-first)

### 6.3 Personalization Engine
- Server-side article ranking based on reading patterns
- "For You" feed generated per user
- Category interest scores derived from dwell time + bookmarks
- Opt-in: users can disable personalization entirely

### 6.4 Multi-Source RSS
- Users add custom RSS/Atom feeds from any URL
- Server-side feed parser with deduplication
- OPML import/export for feed list portability
- Per-source branding on article cards

---

## Phase 7 — Premium & Monetization (v3.1)

> **Goal:** Sustainable revenue model without compromising user experience.

### 7.1 SkyNet Reader Pro (In-App Purchase / Subscription)
- **Free tier:** 5 categories, 50 bookmarks, basic AI chat
- **Pro tier:** Unlimited categories, unlimited bookmarks, advanced AI (longer context, multi-turn), custom RSS feeds, cross-device sync, no ads
- Platform-specific: Google Play Billing, StoreKit 2, Stripe (web/desktop)

### 7.2 Tasteful Monetization
- Non-intrusive native ads in feed (clearly labeled)
- No interstitials, no pop-ups, no tracking ads
- Pro users see zero ads

---

## Release Timeline

| Version | Milestone | Platforms |
|---|---|---|
| **v1.0** ✅ | Android app — Compose, Clean Architecture, AI chat | Android |
| **v1.1** | Polish — dashboard redesign, widgets, notifications, CI | Android |
| **v2.0** | KMP shared module — extract domain + data layer | Android (refactored) |
| **v2.1** | iOS app — SwiftUI shell + shared KMP module | Android, **iOS** |
| **v2.2** | Desktop app — Compose Desktop + shared module | Android, iOS, **Desktop** |
| **v2.3** | Web app — Compose WASM/JS + PWA | Android, iOS, Desktop, **Web** |
| **v3.0** | Cloud sync, backend, personalization, multi-source RSS | All platforms |
| **v3.1** | Pro tier, monetization | All platforms |

---

## Tech Stack Evolution

| Component | v1.0 (Current) | v2.0+ (KMP) |
|---|---|---|
| Language | Kotlin (JVM) | Kotlin Multiplatform |
| UI — Android | Jetpack Compose | Jetpack Compose (unchanged) |
| UI — iOS | — | SwiftUI + SKIE |
| UI — Desktop | — | Compose Desktop |
| UI — Web | — | Compose for Web (WASM) |
| Networking | Retrofit + OkHttp | Ktor Client (multiplatform) |
| Database | Room | SQLDelight (multiplatform) |
| Preferences | Jetpack DataStore | Multiplatform Settings |
| DI | Hilt (Dagger) | Koin (multiplatform) |
| JSON | Gson | kotlinx.serialization |
| Images | Coil 2 | Coil 3 KMP / Kamel |
| AI | MediaPipe (Android) | Platform expect/actual (MediaPipe / CoreML / ONNX) |
| Testing | JUnit 5 + MockK | kotlin.test + shared test suite |
| CI/CD | GitHub Actions | GitHub Actions (matrix: Android, iOS, Desktop, Web) |

---

## Principles

1. **Offline-first** — Every platform caches data locally. The app works without internet.
2. **Privacy-first** — No telemetry without consent. Personalization data stays on-device unless the user opts into cloud sync.
3. **Platform-native feel** — Shared logic, native UI. Android feels like Android, iOS feels like iOS, Desktop feels desktop-native.
4. **Incremental adoption** — KMP is introduced gradually. Android continues working throughout the migration. No big-bang rewrites.
5. **Open source** — Core app stays MIT-licensed. Pro features are additive, not restrictive.

---

*This roadmap is a living document. Update phase statuses as development progresses.*
