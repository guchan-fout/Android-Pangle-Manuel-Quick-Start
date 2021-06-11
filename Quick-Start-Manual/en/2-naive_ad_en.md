# 2. Native Ads


* [Origin Native Ads](#start/native_ad_origin)
  * [Loading Ads](#start/native_ad_origin_load)
  * [Determining load events](#start/native_ad_origin_loadevent)
  * [Displaying Ads and Registering Ads](#start/native_ad_origin_display)



This chapter will explain the procedure for displaying the native ads in the application.

Please [integrate Pangle SDK](1-integrate_en.md) before load ads.




<a name="start/native_ad_origin"></a>
## Origin Native Ads

<a name="start/native_ad_origin_load"></a>
### Loading Ads

On Pangle platform, create an **Origin** ad in the app, you will get a **placement ID** for ad's loading.

<img src="../pics/native_origin.png" alt="drawing" width="200"/>


In your application, create a `slot` for setting size and type for the ad and  use `TTAdNative` to load ads.

```kotlin
class NativeAdsViewActivity : AppCompatActivity() {

    private lateinit var mTTAdNative: TTAdNative

    ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ...

        requestOriginNativeAd("your placement id")
    }


    private fun requestOriginNativeAd(mPlacementID: String) {
        Timber.d(mPlacementID)
        if (mPlacementID.isEmpty()) {
            Timber.e("PlacementId is null")
            return
        }

        //init Pangle ad manager
        val mTTAdManager = TTAdSdk.getAdManager()
        mTTAdNative = mTTAdManager.createAdNative(this)
        val adSlot = AdSlot.Builder()
            .setCodeId(mPlacementID)
            .setAdCount(1)
            .build()
        mTTAdNative.loadFeedAd(adSlot, mFeedAdListener)
    }


    ...

```

<a name="start/native_ad_origin_loadevent"></a>
### Determining load events

`FeedAdListener` indicates the result of ad's load. If ad is loaded, `TTFeedAd` which include the data of the ad will be passed from `onFeedAdLoad`.


```kotlin
private val mFeedAdListener: FeedAdListener = object : FeedAdListener {
    override fun onError(code: Int, message: String) {
        Timber.d("feedAdListener loaded fail .code=$code,message=$message")
    }

    override fun onFeedAdLoad(ads: List<TTFeedAd>) {
        if (ads.isEmpty()) {
            Timber.e("feedAdListener loaded success .but ad no fill ")
            return
        }
        for (nativeAd in ads) {
            val content = CellContentModel()
            content.isAd = true
            content.feedAd = nativeAd
            mContentlist.add(adPosition, content)
            mAdapter.notifyItemInserted(adPosition)
        }
    }
}
```

<a name="start/native_ad_origin_display"></a>
### Displaying Ads and Registering Ads

`nativeAd`'s parameters have parts like ad's title, description, images for displaying.

if the parameter`imageMode` in the `nativeAd` is **TTAdConstant.IMAGE_MODE_VIDEO** or **TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL** or
**TTAdConstant.IMAGE_MODE_VIDEO_SQUARE**, please set `adView` to display video for the ad.

`TTAdDislike` is for getting user's feedback, please add a button on the ad's view to show `TTNativeAd.getDislikeDialog` to get the feedback.

Please add `TTNativeAd`'s `adLogoView` to the ad's view, this view will show the privacy information when been clicked.

**Must** register clickable view in the ad via `TTFeedAd.registerViewForInteraction()`. You can set clickable area of the ad(etc. button/image/video) and this method will response to user clicks to redirected to the landing page, and **also watching the ad to trigger the impression event**.

```kotlin
override fun onBindViewHolder(
    holder: RecyclerAdapter.RecyclerAdapterViewHolder,
    position: Int
) {
    if (getItemViewType(position) == TYPE_NORMAL) {
        holder.view.item_name.text = contentList[position].content
    } else {

        /**
         *  here is to set native ad's date to the view
         */
        var ad: TTFeedAd = contentList[position].feedAd
        holder.view.titleText.text = ad.title
        holder.view.descText.text = ad.description
        holder.view.adButton.text = ad.buttonText
        Glide.with(holder.view).asBitmap().load(ad.icon.imageUrl).into(holder.view.logoView)

        // this is video ad
        if (ad.imageMode == TTAdConstant.IMAGE_MODE_VIDEO || ad.imageMode == TTAdConstant.IMAGE_MODE_VIDEO_SQUARE || ad.imageMode == TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {
            var videoAd = ad.adView
            if (videoAd != null) {
                Timber.d("video exist")
                holder.view.containerFrame.addView(ad.adView)
            } else {
                Timber.d("video not exist")
                val imageView = ImageView(holder.view.context)
                Glide.with(holder.view).asBitmap().load(ad.imageList[0].imageUrl)
                    .into(imageView)
                holder.view.containerFrame.addView(imageView)
            }
        } else {
            // this is an image ad
            val imageView = ImageView(holder.view.context)
            Glide.with(holder.view).asBitmap().load(ad.imageList[0].imageUrl).into(imageView)
            holder.view.containerFrame.addView(imageView)
        }

        // Pangle logo view, will show privacy information if clicked
        holder.view.adLogoView.addView(ad.adLogoView)
        holder.view.adLogoView.bringToFront()

        // set dislike button on top
        holder.view.dislikeButton.bringToFront()
        bindDislikeAction(ad, holder.view.dislikeButton, holder)

        // register the view for click
        ad.registerViewForInteraction(
            holder.view as ViewGroup,
            holder.view.adButton,
            mTTNativeAdListener
        )
    }
}

private val mTTNativeAdListener: TTNativeAd.AdInteractionListener =
    object : TTNativeAd.AdInteractionListener {
        override fun onAdClicked(p0: View?, p1: TTNativeAd?) {
            Timber.d("onAdClicked")
        }

        override fun onAdCreativeClick(p0: View?, p1: TTNativeAd?) {
            Timber.d("onAdCreativeClick")
        }

        override fun onAdShow(p0: TTNativeAd?) {
            Timber.d("onAdShow")
        }

    }

override fun getItemCount(): Int {
    return contentList.size
}

override fun getItemViewType(position: Int): Int {
    return if (!contentList[position].isAd) {
        TYPE_NORMAL
    } else {
        Timber.d("isAD!!")
        TYPE_AD
    }
}

companion object {
    private const val TYPE_NORMAL = 1
    private const val TYPE_AD = 2
}

private fun bindDislikeAction(
    ad: TTNativeAd,
    dislikeView: View,
    holder: RecyclerAdapter.RecyclerAdapterViewHolder
) {
    val ttAdDislike: TTAdDislike = ad.getDislikeDialog(dislikeView.context as Activity)
    ttAdDislike?.setDislikeInteractionCallback(object : DislikeInteractionCallback {
        override fun onSelected(position: Int, value: String) {
            // here to notify the recycleview to close the adapter
            contentList.removeAt(holder.layoutPosition)
            notifyDataSetChanged()
        }

        override fun onCancel() {
            Timber.d("onCancel")
        }
    })
    dislikeView.setOnClickListener { ttAdDislike?.showDislikeDialog() }
}
```
