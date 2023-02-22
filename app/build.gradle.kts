plugins {
    id (Plugins.ANDROID_APPLICATION)
    id (Plugins.KOTLIN_ANDROID)
    id (Plugins.SECRETS_GRADLE_PLUGIN)
    id (Plugins.SAFEARGS)
    id (Plugins.PARCELIZE)
    id (Plugins.HILT_PLUGIN)
    id (Plugins.KAPT)
}

android {
    namespace = "com.nohjunh.booksearchapp"
    compileSdk = DefaultConfig.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = "com.nohjunh.booksearchapp"
        minSdk = DefaultConfig.MIN_SDK_VERSION
        targetSdk = DefaultConfig.TARGET_SDK_VERSION
        versionCode = DefaultConfig.VERSION_CODE
        versionName = DefaultConfig.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
/*    task wrapper(type: Wrapper) {
        gradleVersion = "7.0"
    }*/
    kapt {
        correctErrorTypes = true
    }
}


dependencies {

    implementation (Dependencies.CORE_KTX)
    implementation (Dependencies.APP_COMPAT)
    implementation (Dependencies.MATERIAL)
    implementation (Dependencies.CONSTRAINT_LAYOUT)
    testImplementation (Testing.JUNIT4)
    androidTestImplementation (Testing.ANDROID_JUNIT)
    androidTestImplementation (Testing.ESPRESSO_CORE)

    // Timber setting
    implementation (Dependencies.TIMBER)

    // Splash Screens
    implementation (Dependencies.SPLASHSCREENS)

    // navigation
    implementation (Dependencies.NAVIGATION_FRAGMENT_KTX)
    implementation (Dependencies.NAVIGATION_UI_KTX)

    // RecyclerView
    implementation (Dependencies.RECYCLERVIEW)

    // coil
    implementation(Dependencies.COIL)
    implementation(Dependencies.COIL_GIF)
    implementation(Dependencies.COIL_COMPOSE)

    // Coroutine
    implementation (Dependencies.COROUTINE_CORE)
    implementation (Dependencies.COROUTINE_ANDROID)

    // Lifecycle viewModelScope
    implementation (Dependencies.LIFECYCLE_VIEWMODEL_KTX)
    implementation (Dependencies.LIFECYCLE_RUNTIME_KTX)
    implementation (Dependencies.LIFECYCLE_SAVEDSTATE)

    // LiveData
    implementation (Dependencies.LIVEDATA_KTX)

    // Room
    implementation (Dependencies.ROOM_RUNTIME)
    // To use Kotlin annotation processing tool (kapt)
    kapt (Dependencies.ROOM_KAPT)
    implementation (Dependencies.ROOM_KTX)
    implementation (Dependencies.ROOM_PAGING)

    // Retrofit
    implementation (Dependencies.RETROFIT)
    implementation (Dependencies.RETROFIT_CONVERTER_GSON)
    implementation (Dependencies.RETROFIT_CONVERTER_MOSHI)

    //moshi
    implementation (Dependencies.MOSHI_KOTLIN)
    implementation (Dependencies.MOSHI)
    kapt (Dependencies.MOSHI_KAPT)

    // OkHttp
    implementation (Dependencies.OKHTTP)
    implementation (Dependencies.OKHTTP_LOGGING_INTERCEPTOR)

    /*
     Activity와 Fragment에 viewModel 의존성을 주입하는 과정에서
     by 위임자를 통한 delegate 패턴으로 viewModel을 초기화할 수 있음.
     delegate 패턴을 사용하면 Factory를 사욯하지 않고도 ViewModel을 생성할 수 있음.
     */
    implementation (Dependencies.ACTIVITY_KTX)
    implementation (Dependencies.FRAGMENT_KTX)

    // kotlinx serialization
    implementation (Dependencies.KOTLIN_SERIALIZATION)

    // DataStore
    implementation (Dependencies.PREFERENCES_DATASTORE)

    // Paging
    implementation (Dependencies.PAGING)

    // WorkManager
    implementation (Dependencies.WORKMANAGER)

    // Hilt
    implementation (Dependencies.DAGGER_HILT)
    kapt (Dependencies.DAGGER_HILT_KAPT)

    // Hilt Extension (workmanager에 의존성을 주입하기 위함)
    implementation (Dependencies.HILT_EXTENSION_WORK)
    kapt (Dependencies.HILT_EXTENSION_KAPT)

}

tasks.register("prepareKotlinBuildScriptModel"){}

