# 1. Integrate and Initialize

This chapter will explain the procedure for integrating and Initializing the Pangle SDK.

* [Prerequisites](#start/env)
* [Integrate](#start/integrate)
  * [Import the Pangle SDK](#start/import)
  * [Update the Android Manifest](#start/manifest)
* [Initializing the SDK](#start/init)


<a name="start/env"></a>
## Prerequisites

* minSdkVersion 14 or higher
* targetSdkVersion 29 or higher
* Create a Pangle account [here](https://www.pangleglobal.com/)(If you do not have one), and add your app and placements.
* From v3.4.0.0, we added a `<queries>` element to support Android 11(API level 30), please upgrade Android Gradle plugin to 4.1+ or following versions.

  |  Support `<queries>` Gradle Versions  |
  | ---- |
  |  4.0.1 |
  |  3.6.4 |
  |  3.5.4 |
  |  3.4.3 |
  |  3.3.3 |


<a name="start/integrate"></a>
## Integrate

<a name="start/import"></a>
### Import the Pangle SDK
Add `url 'https://artifact.bytedance.com/repository/pangle'` maven in the `allprojects` section of your project-level `build.gradle`.

```gradle
allprojects {
    repositories {
      maven {
        url 'https://artifact.bytedance.com/repository/pangle'
      }
    }
}
```

Next, open the app-level `build.gradle` file for your app, add `implementation 'com.pangle.global:ads-sdk:x.x.x.x'` in "dependencies" section.
In order to use the Android Advertising ID, we also recommend add  `com.google.android.gms:play-services-ads-identifier`.

```gradle
dependencies {
    ...
    implementation 'com.pangle.global:ads-sdk:3.6.0.4'
    implementation 'com.google.android.gms:play-services-ads-identifier:17.0.0'
    ...

}
```

<a name="start/manifest"></a>
### Update the Android Manifest
Add following permissions and **provider** to your app's `AndroidManifest.xml`.

> :warning: Please make sure to add **provider** or ad's loading will not work properly.


```xml

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="xxxxxxxx">

    <!--Required  permissions-->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <!-- If there is a video ad and it is played with textureView, please be sure to add this, otherwise a black screen will appear -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <application
        ...
        <!--Required->
        <provider
            android:name="com.bytedance.sdk.openadsdk.multipro.TTMultiProvider"
            android:authorities="${applicationId}.TTMultiProvider"
            android:exported="false" />

        ...

    </application>

</manifest>

```

<a name="start/init"></a>
## Initialize the SDK
Initialize Pangle SDK asynchronously is supported since the v3.5.0.0 SDK, please call `TTAdSdk.init(final Context var0, final TTAdConfig var1, final TTAdSdk.InitCallback var2)` to initializes the SDK before you send any ad requests. `init` only need to be called once per app’s lifecycle, we **strongly recommend** to do this on app launch.

`TTAdSdk.InitCallback` will be informed about the result of the initialize.

If you use TextureView for video ads, please set `useTextureView(true)` in the Builder and add add  `WAKE_LOCK`  permission in the manifest.

> Before v3.5.0.0, please use synchronous method `TTAdSdk.init(Context var0, TTAdConfig var1)` to initialize.

```kotlin
class PangleApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initSdk()
    }

    private fun initSdk() {
        TTAdSdk.init(this, buildAdConfig(), mInitCallback)
    }

    private val mInitCallback: TTAdSdk.InitCallback = object : TTAdSdk.InitCallback {
        override fun success() {
            Timber.d("init succeeded")
        }

        override fun fail(p0: Int, p1: String?) {
            Timber.d("init failed. reason = $p1")
        }
    }

    private fun buildAdConfig(): TTAdConfig {
        return TTAdConfig.Builder()
            // Please use your own appId,
            .appId("your_app_id")
            // Turn it on during the testing phase, you can troubleshoot with the log, remove it after launching the app
            .debug(BuildConfig.DEBUG)
            // The default setting is SurfaceView. We strongly recommend to set this to true.
            // If using TextureView to play the video, please set this and add "WAKE_LOCK" permission in manifest
            .useTextureView(true)
            // Fields to indicate whether you are a child or an adult ，0:adult ，1:child
            .coppa(0)
            .build()
    }
```


You also could check the initialization status with the method `TTAdSdk.isInitSuccess()`

```kotlin
private fun checkInitResult(): Boolean {
   return TTAdSdk.isInitSuccess()
}

```

> :warning: Ads may be preloaded by the Pangle Ads SDK or mediation partner SDKs after initial. If you need to obtain consent from users under age 13, please ensure you do so before initializing the Pangle Ads SDK.

If you wanna change the value of COPPA after initializing Pangle SDK, you can call following method, as shown below:

```kotlin
TTAdSdk.setCoppa(int CoppaValue);

```
