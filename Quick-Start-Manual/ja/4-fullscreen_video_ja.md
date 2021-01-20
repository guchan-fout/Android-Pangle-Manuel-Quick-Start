# 4. フルスクリーン動画広告


* [フルスクリーン動画広告](#start/fullscreen_ad)
  * [フルスクリーン動画広告のロード](#start/fullscreen_load)
  * [ロードイベントの受信と広告の表示](#start/fullscreen_loadevent)


この章では、アプリでフルスクリーン動画広告を表示する手順について説明します。

広告を利用するには、SDKを有効にする必要があります。詳細は[インストールと初期化](1-integrate_ja.md) をご確認ください。



<a name="start/fullscreen_ad"></a>
## フルスクリーン動画広告

<a name="start/fullscreen_load"></a>
### フルスクリーン動画広告のロード

Pangle管理画面上にて, 対象アプリに属する **Interstitial Video Ads** 広告を新規してください。 新規したらその広告枠の **placement ID** が生成されます。

アプリに合わせて広告の`Orientation`を設定してください。


<img src="../pics/fullscreen_add.png" alt="drawing" width="300"/>  <br>

<img src="../pics/fullscreen_set.png" alt="drawing" width="300"/>

アプリケーションで、 `TTAdNative`を作成し、` AdSlot`に広告のパラメータを設定し、 `TTAdNative`の` void loadFullScreenVideoAd（AdSlot var1、@ NonNull TTAdNative.FullScreenVideoAdListener var2）; `を使用して広告を読み込みます。


```kotlin
class FullScreenVideoAdsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...

        requestFullScreenVideoAd("your placement id")
    }

    private fun requestFullScreenVideoAd(mPlacementID: String) {
        if (mPlacementID.isEmpty()) {
            Timber.e("PlacementId is null")
            return
        }

        //init Pangle ad manager
        val mTTAdManager = TTAdSdk.getAdManager()
        val mTTAdNative = mTTAdManager.createAdNative(this)
        val adSlot = AdSlot.Builder()
            .setCodeId(mPlacementID)
            .setSupportDeepLink(true)
            .setImageAcceptedSize(1080, 1920)
            .setOrientation(TTAdConstant.VERTICAL) //required parameter ，Set how you wish the video ad to be displayed ,choose from TTAdConstant.HORIZONTAL or TTAdConstant.VERTICAL
            .build()
        mTTAdNative.loadFullScreenVideoAd(adSlot, mTTFullScreenAdListener)
    }

    ...
}

```

<a name="start/fullscreen_loadevent"></a>
### ロードイベントの受信と広告の表示

`FullScreenVideoAdListener`は、広告の読み込みの結果を示します。 広告が読み込まれている場合は、 `TTFullScreenVideoAd`の` void showFullScreenVideoAd（Activity var1）; `'を呼び出して広告を表示します。 `onFullScreenVideoCached（）`に広告を表示することをお勧めします。

`FullScreenVideoAdListener` indicates the result of ad's load. If ad is loaded, call `TTFullScreenVideoAd`'s `void showFullScreenVideoAd(Activity var1);`' to display the ad. We recommend to show the ad in `onFullScreenVideoCached()`.

**広告が表示された後、次の表示のために広告をリロードしてください。同じロードされた広告の有効なインプレッションは1回だけカウントされます。**

```kotlin

private var mFullScreenVideoAd: TTFullScreenVideoAd? = null
private var mIsCached: Boolean = false

override fun onCreate(savedInstanceState: Bundle?) {
    ...

    show_ad.setOnClickListener {
        if (mIsCached) {
            mFullScreenVideoAd?.showFullScreenVideoAd(this@FullScreenVideoAdsActivity)
            resetVideoAd()
        } else {
            Timber.w("Not Cached yet")
        }
    }
}

private val mTTFullScreenAdListener: FullScreenVideoAdListener =
    object : FullScreenVideoAdListener {
        override fun onError(i: Int, s: String) {
            Timber.d("FullScreenVideoAdListener loaded fail .code=$i,message=$s")
        }

        override fun onFullScreenVideoAdLoad(ttFullScreenVideoAd: TTFullScreenVideoAd) {
            mFullScreenVideoAd = ttFullScreenVideoAd
            mIsCached = false

            ...

        }

        override fun onFullScreenVideoCached() {
            mIsCached = true
        }
    }

// Only first show is a valid impression, please reload again to get another ad.
private fun resetVideoAd() {
    mFullScreenVideoAd = null
}
```
