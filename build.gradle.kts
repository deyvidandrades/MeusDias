plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.deyvidandrades.meusdias"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.deyvidandrades.meusdias"
        minSdk = 35
        targetSdk = 36
        versionCode = 32
        versionName = "1.4.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildToolsVersion = "35.0.0"
}

dependencies {

    //Core libs
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")

    //Android widgets
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.android.play:review:2.0.2")
    implementation("com.google.android.play:review-ktx:2.0.2")

    //Outras bibliotecas
    implementation("nl.dionsegijn:konfetti-xml:2.0.5")
    implementation("com.google.code.gson:gson:2.13.1")

    //Test libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}