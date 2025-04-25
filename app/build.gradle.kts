plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.meteoapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.meteoapp"
        minSdk = 27
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.appcompat:appcompat:1.3.1") // Ou version compatible
    implementation("com.google.android.material:material:1.4.0") // Ou version compatible
    implementation("androidx.constraintlayout:constraintlayout:2.1.1") // Ou version compatible

    // Networking (Retrofit pour appeler l'API Météo)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Pour parser le JSON
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1") // Optionnel: pour voir les logs réseau

    // ViewModel & LiveData (pour le pattern MVVM) - Utilisez les versions androidx
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.3.1") // Ou version compatible
    implementation("androidx.lifecycle:lifecycle-livedata:2.3.1") // Ou version compatible
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:2.3.1") // Nécessaire pour Java

    // Location Services (pour le GPS)
    implementation("com.google.android.gms:play-services-location:18.0.0") // Ou version compatible

    // RecyclerView (pour la liste de prévisions)
    implementation("androidx.recyclerview:recyclerview:1.2.1") // Ou version compatible

    // Tests (Optionnel mais recommandé)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}