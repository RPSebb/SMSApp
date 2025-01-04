plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.smsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smsapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        compose = true
    }

    buildToolsVersion = "36.0.0 rc1"

    lint {
        disable.addAll(
            listOf(
                "AccidentalOctal",
                "AndroidGradlePluginVersion",
                "AnnotationProcessorOnCompilePath",
                "BomWithoutPlatform",
                "UseOfBundledGooglePlayServices",
                "ChromeOsAbiSupport",
                "GradleCompatible",
                "DataBindingWithoutKapt",
                "GradleDependency",
                "GradleDeprecated",
                "GradleDeprecatedConfiguration",
                "OutdatedLibrary",
                "DevModeObsolete",
                "DuplicatePlatformClasses",
                "EditedTargetSdkVersion",
                "ExpiredTargetSdkVersion",
                "ExpiringTargetSdkVersion",
                "GradleGetter",
                "GradlePluginVersion",
                "HighAppVersionCode",
                "GradleIdeError",
                "JavaPluginLanguageLevel",
                "JcenterRepositoryObsolete",
                "KaptUsageInsteadOfKsp",
                "KtxExtensionAvailable",
                "LifecycleAnnotationProcessorWithJava8",
                "MinSdkTooLow",
                "SimilarGradleDependency",
                "NotInterpolated",
                "GradlePath",
                "PlaySdkIndexNonCompliant",
                "PlaySdkIndexGenericIssues",
                "GradleDynamicVersion",
                "NewerVersionAvailable",
                "RiskyLibrary",
                "StringShouldBeInt",
                "UseTomlInstead",
                "OldTargetApi"
            )
        )
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.animation.android)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.material.icons.core)

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("com.google.dagger:hilt-android:2.54")
    kapt("com.google.dagger:hilt-compiler:2.54")
}

kapt {
    correctErrorTypes = true
}