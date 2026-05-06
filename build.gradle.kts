// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

// For Navigation's safe args, see https://developer.android.com/guide/navigation/use-graph/safe-args#kts
buildscript {
    repositories {
        google()
    }
    dependencies {
        val navVersion = "2.9.6"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}