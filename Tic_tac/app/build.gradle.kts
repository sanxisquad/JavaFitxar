plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.tiac_tac"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tiac_tac"
        minSdk = 26
        targetSdk = 34
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
    implementation(libs.volley)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Afegir dependències per a Apache POI
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation ("androidx.cardview:cardview:1.0.0")

}

// Desactivar el seguiment de l'estat de les tasques de mapatge de fonts de depuració
tasks.configureEach {
    if (name.contains("map") && name.contains("SourceSetPaths")) {
        doNotTrackState("Accessing unreadable inputs or outputs is not supported.")
    }
}