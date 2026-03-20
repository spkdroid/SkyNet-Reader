# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Ramkumar Velmurugan\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ── Retrofit ──────────────────────────────────────────────────────────────────
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ── OkHttp ────────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# ── Gson ──────────────────────────────────────────────────────────────────────
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }

# ── App DTO / domain models (required for JSON de-serialisation) ──────────────
-keep class com.news.skynet.data.remote.NewsDto { *; }
-keep class com.news.skynet.domain.model.** { *; }
-keep class com.news.skynet.data.local.** { *; }

# ── Room ──────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Entity class * { *; }

# ── Hilt ──────────────────────────────────────────────────────────────────────
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * extends android.app.Application

# ── Navigation Safe Args ──────────────────────────────────────────────────────
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable

# ── Coil ──────────────────────────────────────────────────────────────────────
-dontwarn coil.**

# ── Lottie ────────────────────────────────────────────────────────────────────
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# ── Firebase ──────────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ── WorkManager ───────────────────────────────────────────────────────────────
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ── WebView JavaScript interface (for article detail) ────────────────────────
-keepclassmembers class com.news.skynet.ui.detail.ArticleDetailFragment {
    @android.webkit.JavascriptInterface <methods>;
}
