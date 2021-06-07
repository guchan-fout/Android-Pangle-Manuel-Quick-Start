# 2. ネイティブ広告


* [オリジンネイティブ広告](#start/native_ad_origin)
  * [広告のロード](#start/native_ad_origin_load)
  * [ロードイベントを受信する](#start/native_ad_origin_loadevent)
  * [広告の表示とインプレッション登録](#start/native_ad_origin_display)


この章では、アプリでネイティブ広告を表示する手順について説明します。

広告を利用するには、SDKを有効にする必要があります。詳細は[インストールと初期化](1-integrate_ja.md) をご確認ください。




<a name="start/native_ad_origin"></a>
## オリジンネイティブ広告

<a name="start/native_ad_origin_load"></a>
### 広告のロード

Pangle管理画面上にて, 対象アプリに属する **Origin** 広告を新規してください。 新規したらその広告枠の **placement ID** が生成されます。


<img src="../pics/native_origin.png" alt="drawing" width="200"/>

広告タイプとサイズを指定する `slot` を新規し、`TTAdNative` で広告をロードしてください。


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
### ロードイベントを受信する

`FeedAdListener`は、広告の読み込みの結果を示します。 広告が読み込まれると、広告のデータを含む `TTFeedAd`が` onFeedAdLoad`から渡されます。


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
### 広告の表示とインプレッション登録

`nativeAd`のパラメータには、広告のタイトル、説明、表示する画像などのパーツがあります。

`nativeAd`のパラメータ`imageMode`が**TTAdConstant.IMAGE_MODE_VIDEO**、　**TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL**　または
**TTAdConstant.IMAGE_MODE_VIDEO_SQUARE**　の時に広告タイプは動画です。
動画素材を表示するには、 `adView`を設定してください。

`TTAdDislike`はユーザーのフィードバックを取得するためのものです。フィードバックを取得するには、広告のビューに` TTNativeAd.getDislikeDialog`を表示するボタンを追加してください。

広告のビューに `TTNativeAd`の` adLogoView`を追加してください。このビューをクリックするとプライバシー情報が表示されます。

**必ず**`TTFeedAd.registerViewForInteraction（）`を介してクリック可能なビューを広告に登録する必要があります。 広告のクリック可能な領域（ボタン/画像/動画など）を設定できます。このメソッドは、ユーザーのクリックに応答してランディングページにリダイレクトされ、**広告のインプレッションイベントをトリガーします**。


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
