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
* From V3.4.0.0, we added a `<queries>` element to support Android 11(API level 30), please upgrade Android Gradle plugin to 4.1+ or following versions.

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
Add `jcenter()` to the `allprojects` section of your project-level `build.gradle`.

```gradle
allprojects {
    repositories {
        jcenter()
    }
}
```

Next, open the app-level `build.gradle` file for your app, add `implementation 'com.bytedance.sdk:pangle-sdk:x.x.x.x'` in "dependencies" section.
In order to use the Android Advertising ID, we also recommend add  `com.google.android.gms:play-services-ads-identifier`.

```gradle
dependencies {
    ...
    implementation 'com.bytedance.sdk:pangle-sdk:3.1.5.3'
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


## Initialize the SDK
Please call `TTAdSdk.init()` to initializes the SDK before you send any ad requests. `TTAdSdk.init()` only need to be called once per app’s lifecycle, we **strongly recommend** to do this on app launch.

> :warning: Ads may be preloaded by the Pangle Ads SDK or mediation partner SDKs upon calling TTAdSdk.init(). If you need to obtain consent from users in the European Economic Area (EEA) or users under age, please ensure you do so before initializing the Pangle Ads SDK.

If you use TextureView for video ads, please set `useTextureView(true)` in the Builder and add add "WAKE_LOCK" permission in the manifest.


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
        TTAdSdk.init(this, buildAdConfig())
    }

    private fun buildAdConfig() : TTAdConfig {
        return TTAdConfig.Builder()
            // Please use your own appId, this is for demo
            .appId("5081617")
            .appName(packageName)
            // Turn it on during the testing phase, you can troubleshoot with the log, remove it after launching the app
            .debug(BuildConfig.DEBUG)
            // The default setting is SurfaceView.
            // If using TextureView to play the video, please set this and add "WAKE_LOCK" permission in manifest
            .useTextureView(true)
            // Allow show Notification
            .allowShowNotify(true)
            // Whether to support multi-process, true indicates support
            .supportMultiProcess(false)
            // Fields to indicate whether you are a child or an adult ，0:adult ，1:child
            .coppa(0)
            //Fields to indicate whether you are protected by GDPR,  the value of GDPR : 0 close GDRP Privacy protection ，1: open GDRP Privacy protection
            .setGDPR(0)
            .build()
    }
}
```
