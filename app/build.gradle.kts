plugins {
    id("com.android.application")
}

android {
    namespace = "com.chinatsp.demo1"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.chinatsp.demo1"
        minSdk = 28
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("io.github.wnjustdoit:pinyin4j:2.6.0")
    implementation("com.github.promeg:tinypinyin:2.0.3")
    implementation("com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3")
//    implementation("com.belerweb:pinyin4j:2.5.0")
    //gson
    implementation("com.google.code.gson:gson:2.8.9")
    implementation(project(":shapebutton"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}