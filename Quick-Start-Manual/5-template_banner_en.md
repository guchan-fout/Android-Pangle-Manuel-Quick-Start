# 5. Template Banner Ads


* [Template Banner Ads](#start/template_banner_ad)
  * [Loading Ads](#start/template_banner_ad_load)
  * [Determining load events and Displaying](#start/template_banner_ad_loadevent)


This chapter will explain the procedure for displaying the template banner in the application.

Please [integrate Pangle SDK](1-integrate_en.md) before load ads.


<a name="start/template_banner_ad"></a>
## Template Banner Ads

<a name="start/template_banner_ad_load"></a>
### Loading Ads

On Pangle platform, create an **Template Banner** ad in the app, you will get a **placement ID** for ad's loading.

**Please select 600*500 at Ad placement size, for now we only opened this size's traffic.**

<img src="pics/template_banner_add.png" alt="drawing" width="200"/>

<img src="pics/template_banner_set.png" alt="drawing" width="200"/>


In your application, create a `slot` and use `setExpressViewAcceptedSize` for setting size and use `TTAdNative`'s `void loadBannerExpressAd(AdSlot var1, @NonNull TTAdNative.NativeExpressAdListener var2);`' to load ads.

**For now pangle only support size 300*250. Please set this size.**


```kotlin
class FullScreenVideoAdsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...

        requestFullScreenVideoAd("945277276")
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
### Determining load events and Displaying

`NativeExpressAdListener` indicates the result of ad's load. If ad is loaded, please bind dislike to the `TTNativeExpressAd` for user's feedback to the ad, and call 'render()' to render the ad.

 `ExpressAdInteractionListener` will return the ad's render result. If render succeed, you will get a ad view to display.

```kotlin
private val mTTBannerNativeExpressAdListener: NativeExpressAdListener =
    object : NativeExpressAdListener {
        override fun onError(code: Int, message: String) {
            Timber.d("NativeExpressAdListener loaded fail .code=$code,message=$message")
        }

        override fun onNativeExpressAdLoad(ads: List<TTNativeExpressAd>) {
            if (ads == null || ads.isEmpty()) {
                return
            }
            mTTTemplateBannerAd = ads[0]
            mTTTemplateBannerAd.setExpressInteractionListener(mExpressAdInteractionListener)
            bindDislike(mTTTemplateBannerAd)
            mTTTemplateBannerAd.render()
        }
    }

private val mExpressAdInteractionListener: ExpressAdInteractionListener =
    object : ExpressAdInteractionListener {
        override fun onAdClicked(view: View, type: Int) {
            Timber.d("onAdClicked")
        }

        override fun onAdShow(view: View, type: Int) {
            Timber.d("onAdShow")
        }

        override fun onRenderFail(view: View, msg: String, code: Int) {
            Timber.d("onRenderFail .code=$code,message=$msg")
        }

        override fun onRenderSuccess(view: View, width: Float, height: Float) {
            Timber.d("onRenderSuccess")
            bannerFrame.addView(view)
        }
    }

private fun bindDislike(ad: TTNativeExpressAd) {
    ad.setDislikeCallback(this, object : TTAdDislike.DislikeInteractionCallback {
        override fun onSelected(position: Int, value: String) {
            Timber.d("onSelected")
            bannerFrame.removeAllViews()
        }

        override fun onCancel() {}
    })
}
```
