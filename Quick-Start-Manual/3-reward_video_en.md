# 3. Rewarded Video Ads


* [Rewarded Video Ads](#start/reward_ad)
  * [Loading Ads](#start/reward_ad_load)
  * [Determining load events](#start/reward_ad_loadevent)


This chapter will explain the procedure for displaying the rewarded video ads in the application.

Please [integrate Pangle SDK](1-integrate_en.md) before load ads.


<a name="start/reward_ad"></a>
## Rewarded Video Ads

<a name="start/reward_ad_load"></a>
### Loading Ads

On Pangle platform, create an **Rewarded Video Ads** ad in the app, you will get a **placement ID** for ad's loading.

Please set the ad's `Orientation` to fit for the app.
`rewards name` and `rewards quantity` can be random if not needed.


<img src="pics/reward_video_add.png" alt="drawing" width="300"/>  <br>

<img src="pics/reward_video_set.png" alt="drawing" width="300"/>


In your application, create a `TTAdNative` and set the ad's parameter in a `AdSlot`, use `TTAdNative`'s `void loadRewardVideoAd(AdSlot var1, @NonNull TTAdNative.RewardVideoAdListener var2);` to load the ad.

```kotlin
fun requestRewardedVideoAd(mPlacementID: String) {
    Timber.d(mPlacementID)
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
        .setImageAcceptedSize(1080, 1920) //Set size to fit your ad slot size
        .setRewardName("your reward's name") //Parameter for rewarded video ad requests, name of the reward
        .setRewardAmount(1) // The number of rewards in rewarded video ad
        .setUserID("your app user id") //User ID, a required parameter for rewarded video ads
        .setMediaExtra("media_extra") //optional parameter
        .setOrientation(TTAdConstant.VERTICAL) //Set how you wish the video ad to be displayed, choose from TTAdConstant.HORIZONTAL or TTAdConstant.VERTICAL
        .build()

    //load ad
    mTTAdNative.loadRewardVideoAd(adSlot, mRewardedAdListener)
}

  ...

```

<a name="start/reward_ad_loadevent"></a>
### Determining load events and displaying

`RewardVideoAdListener` indicates the result of ad's load. If ad is loaded, call `TTRewardVideoAd`'s `void showRewardVideoAd(Activity var1);`' to display the ad.

```kotlin
private val mRewardedAdListener: RewardVideoAdListener = object : RewardVideoAdListener {
    override fun onError(i: Int, msg: String) {
        Timber.d("RewardVideoAdListener loaded fail .code=$msg,message=$i")
    }

    override fun onRewardVideoAdLoad(ttRewardVideoAd: TTRewardVideoAd) {
        ttRewardVideoAd.showRewardVideoAd(this@RewardedVideoAdsActivity)
    }

    override fun onRewardVideoCached() {}
}
```
