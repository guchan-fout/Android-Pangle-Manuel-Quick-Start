# 1. インストールと初期化

本章ではPangle Android SDKのインストールと初期化手順について記述します。

* [前提条件](#start/env)
* [導入](#start/integrate)
  * [Pangle SDKの導入](#start/import)
  * [Android Manifestの更新](#start/manifest)
* [Panlge SDKの初期化](#start/init)


<a name="start/env"></a>
## 前提条件

* minSdkVersion 14 以上
* targetSdkVersion 29 以上
* アカウントをお持ちでない場合,[管理画面](https://www.pangleglobal.com/)からPangleアカウントを作成し、アプリとプレースメントを新規してください。
* v3.4.0.0から、Android 11（APIレベル30）をサポートするために `<queries>`要素を追加しました。Android Gradle プラグインを `4.1.x` 以降または以下のバージョンにアップグレードしてください。


  |  Support `<queries>` Gradle Versions  |
  | ---- |
  |  4.0.1 |
  |  3.6.4 |
  |  3.5.4 |
  |  3.4.3 |
  |  3.3.3 |


<a name="start/integrate"></a>
## 導入

<a name="start/import"></a>
### Pangle SDK 導入

プロジェクトレベルの`build.gradle`の`allprojects`セクションのmavenに`url 'https://artifact.bytedance.com/repository/pangle'`を追加します。

```gradle
allprojects {
    repositories {
      maven {
        url 'https://artifact.bytedance.com/repository/pangle'
      }
    }
}
```

次に、アプリレベルの`build.gradle`を開き、`dependencies` セクションに`implementation 'com.pangle.global:ads-sdk:x.x.x.x'` を追加します。
 Android Advertising IDを使用するには、`com.google.android.gms：play-services-ads-identifier` を追加することもお勧めします。


```gradle
dependencies {
    ...
    implementation 'com.pangle.global:ads-sdk:3.6.0.4'
    implementation 'com.google.android.gms:play-services-ads-identifier:17.0.0'
    ...

}
```

<a name="start/manifest"></a>
### Android Manifest 更新
アプリの `AndroidManifest.xml`に次の権限と**provider**を追加します。
> :warning: 必ず**provider**を追加してください。そうしないと、広告の読み込みが正しく機能しません。


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
## Panlge SDKの初期化
v3.5.0.0 から非同期初期化メソッドが利用可能になり、
広告リクエストを送信する前に、`TTAdSdk.init(final Context var0, final TTAdConfig var1, final TTAdSdk.InitCallback var2)`を呼び出してSDKを初期化してください。 `init'は、アプリのライフサイクルごとに1回だけ呼び出す必要があります。これは、アプリの起動時に行うことを**強くお勧め**します。

初期化の結果は`TTAdSdk.InitCallback`から通知されます。


動画広告に TextureView を使用する場合は、ビルダーで `useTextureView（true）`を設定し、マニフェストに `WAKE_LOCK` 権限を追加してください。


> v3.5.0.0より以前のバージョンは同期メソッドの`TTAdSdk.init(Context var0, TTAdConfig var1)`を利用ください。

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


初期化結果は`TTAdSdk.isInitSuccess()`から確認できます。

```kotlin
private fun checkInitResult(): Boolean {
   return TTAdSdk.isInitSuccess()
}

```

> :warning: `TTAdSdk.init（）`を呼び出すと、Pangle SDKまたはメディエーションパートナーSDKによって広告がプリロードされる場合があります。１３歳以下未成年のユーザーから同意を得る必要がある場合は、Pangle SDKを初期化する前に必ず同意を得るようにしてください。

初期化後に COPPA の設定を変えたい場合は下記のメソッドを利用してください。

```kotlin
TTAdSdk.setCoppa(int CoppaValue);

```
