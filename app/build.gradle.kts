plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.tehgan.rssreader"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tehgan.rssreader"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "RSS_FALLBACK_URL", "\"https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml\"")

        // Build-level fragment labels ensure one source of truth
        resValue("string", "FL_FEED", "fragment_feed")
        resValue("string", "FL_FAVOURITES", "fragment_favourites")
        resValue("string", "FL_DETAIL", "fragment_detail")
        resValue("string", "FL_SETTINGS", "fragment_settings")
    }

    buildFeatures {
        viewBinding = true
        // Allows for build-level const variables
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    val roomVersion = "2.8.4"
    val navVersion = "2.9.6"
    val swipeRefreshVersion = "1.1.0"

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Navigation components for Fragments
    // https://developer.android.com/guide/navigation
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")

    // Room (database) components
    // https://developer.android.com/training/data-storage/room#java
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // Swipe (pull) to refresh library, for easy feed refreshing
    // https://developer.android.com/jetpack/androidx/releases/swiperefreshlayout#kts
    // https://developer.android.com/reference/androidx/swiperefreshlayout/widget/SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:$swipeRefreshVersion")
}