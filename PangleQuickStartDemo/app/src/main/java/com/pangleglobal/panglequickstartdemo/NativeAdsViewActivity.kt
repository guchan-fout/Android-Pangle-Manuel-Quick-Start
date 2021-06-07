package com.pangleglobal.panglequickstartdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdNative.FeedAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTFeedAd
import com.pangleglobal.panglequickstartdemo.adapter.OriginNativeAdsAdapter
import com.pangleglobal.panglequickstartdemo.model.CellContentModel
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class NativeAdsViewActivity : AppCompatActivity() {
    private lateinit var mTTAdNative: TTAdNative
    private lateinit var mContentlist: ArrayList<CellContentModel>
    private lateinit var mAdapter: OriginNativeAdsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    val adPosition = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContentlist = initList()
        mAdapter =
            OriginNativeAdsAdapter(mContentlist)
        viewManager = LinearLayoutManager(this)

        recycler_view.layoutManager = viewManager
        recycler_view.adapter = mAdapter
        recycler_view.setHasFixedSize(true)

        requestOriginNativeAd("945538916")
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

    private fun initList(): ArrayList<CellContentModel> {
        val list =
            arrayOf("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")
        val contentList: ArrayList<CellContentModel> = ArrayList()

        list.forEach { element ->
            val content = CellContentModel()
            content.content = element
            content.isAd = false
            contentList.add(content)
        }
        return contentList
    }

}