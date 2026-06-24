# ── Lingji v2.0 ProGuard ──

# Keep JNI native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep LlamaEngine native methods
-keep class com.myagent.app.model.LlamaEngine {
    native <methods>;
}

# Keep Room entities
-keep class com.myagent.app.memory.** { *; }

# General
-dontwarn javax.naming.**
-dontwarn lombok.Generated
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Remove verbose logging in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}