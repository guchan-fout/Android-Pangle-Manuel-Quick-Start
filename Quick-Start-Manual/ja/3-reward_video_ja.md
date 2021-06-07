# 3. リワード動画広告


* [リワード動画広告](#start/reward_ad)
  * [リワード動画広告のロード](#start/reward_ad_load)
  * [ロードイベントの受信と広告の表示](#start/reward_ad_loadevent)

この章では、アプリでリワード動画広告を表示する手順について説明します。

広告を利用するには、SDKを有効にする必要があります。詳細は[インストールと初期化](1-integrate_ja.md) をご確認ください。


<a name="start/reward_ad"></a>
## リワード動画広告

<a name="start/reward_ad_load"></a>
### リワード動画広告のロード

Pangle管理画面上にて, 対象アプリに属する **Rewarded Video Ads** 広告を新規してください。 新規したらその広告枠の **placement ID** が生成されます。

アプリに合わせて広告の`Orientation`を設定してください。
`rewards name`と`rewards quantity`は、必要がなければランダムにすることができます。




<img src="../pics/reward_video_add.png" alt="drawing" width="300"/>  <br>

<img src="../pics/reward_video_set.png" alt="drawing" width="300"/>

アプリケーションで、 `TTAdNative`を作成し、` AdSlot`に広告のパラメータを設定し、 `TTAdNative`の` void loadRewardVideoAd（AdSlot var1、@ NonNull TTAdNative.RewardVideoAdListener var2）; `を使用して広告を読み込みます。



```kotlin

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_rewarded_video_ads)

    load_ad.setOnClickListener {
        requestRewardedVideoAd("your placement id")
    }
    ...
}

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
        .setRewardName("your reward's name") //Parameter for rewarded video ad requests, name of the reward
        .setRewardAmount(1) // The number of rewards in rewarded video ad
        .setUserID("your app user id") //User ID, a required parameter for rewarded video ads
        .setMediaExtra("media_extra") //optional parameter
        .build()

    //load ad
    mTTAdNative.loadRewardVideoAd(adSlot, mRewardedAdListener)
}

  ...

```

<a name="start/reward_ad_loadevent"></a>
### ロードイベントの受信と広告の表示

`RewardVideoAdListener`は、広告の読み込みの結果を示します。 広告が読み込まれている場合は、 `TTRewardVideoAd`の` void showRewardVideoAd（Activity var1）; `'を呼び出して広告を表示します。 `onRewardVideoCached（）`で広告を表示することをお勧めします。


**広告が表示された後、次の表示のために広告をリロードしてください。同じロードされた広告の有効なインプレッションは1回だけカウントされます。**

```kotlin

private var mRewardVideoAd: TTRewardVideoAd? = null
private var mIsCached: Boolean = false

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_rewarded_video_ads)

    ...

    show_ad.setOnClickListener {
        if (mIsCached) {
            mRewardVideoAd?.showRewardVideoAd(this)
            resetVideoAd()
        } else {
            Timber.w("Not Cached yet")
        }
    }
}

private val mRewardedAdListener: RewardVideoAdListener = object : RewardVideoAdListener {
    override fun onError(i: Int, msg: String) {
        Timber.d("RewardVideoAdListener loaded fail .code=$i,message=$msg")
    }

    override fun onRewardVideoAdLoad(ttRewardVideoAd: TTRewardVideoAd) {
        mRewardVideoAd = ttRewardVideoAd
        mIsCached = false

        ...

    }

    override fun onRewardVideoCached() {
        mIsCached = true
    }
}

// Only first show is a valid impression, please reload again to get another ad.
private fun resetVideoAd() {
    mRewardVideoAd = null
}
```
