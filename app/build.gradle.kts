plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.mukeatlist"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mukeatlist"
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

    // ✅ Compose 켜기 (필수)
    buildFeatures {
        compose = true
    }

    // ✅ Compose 컴파일러 확장 버전 (필수)
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // 기존(XML/앱 기본) 의존성 유지
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.navigation:navigation-compose:2.7.7")


    // ✅ Compose BOM (버전 관리)
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))

    // ✅ Compose 기본
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // ✅ Material3
    implementation("androidx.compose.material3:material3")

    // ✅ Activity Compose (setContent)
    implementation("androidx.activity:activity-compose:1.9.3")

    // ✅ Paging + Compose
    implementation("androidx.paging:paging-runtime:3.3.2")
    implementation("androidx.paging:paging-compose:3.3.2")

    // ✅ Coil (Compose 이미지)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ✅ 디버그용(프리뷰/툴링)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
}
