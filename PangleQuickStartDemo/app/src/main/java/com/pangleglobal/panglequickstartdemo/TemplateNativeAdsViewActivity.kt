package com.pangleglobal.panglequickstartdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdDislike.DislikeInteractionCallback
import com.bytedance.sdk.openadsdk.TTAdNative.NativeExpressAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.bytedance.sdk.openadsdk.TTNativeExpressAd.ExpressAdInteractionListener
import com.pangleglobal.panglequickstartdemo.adapter.TemplateNativeAdsContentAdapter
import com.pangleglobal.panglequickstartdemo.model.CellContentModel
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class TemplateNativeAdsViewActivity : AppCompatActivity() {

    private lateinit var mAdapter: TemplateNativeAdsContentAdapter
    private lateinit var mViewManager: RecyclerView.LayoutManager

    lateinit var mContentlist: ArrayList<CellContentModel>

    private var mTTNativeExpressAd: TTNativeExpressAd? = null

    val adPosition = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_native_ads_view)

        mContentlist = initList()

        mAdapter =
            TemplateNativeAdsContentAdapter(mContentlist)
        mViewManager = LinearLayoutManager(this)

        recycler_view.layoutManager = mViewManager
        recycler_view.adapter = mAdapter
        recycler_view.setHasFixedSize(true)

        requestTemplateNativeAd("945545833")
    }

    fun requestTemplateNativeAd(mPlacementID: String) {
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
            .setAdCount(1)
            .setExpressViewAcceptedSize(300F, 250F)
            .build()
        mTTAdNative.loadNativeExpressAd(adSlot, mTTNativeExpressAdListener)
    }

    private val mTTNativeExpressAdListener: NativeExpressAdListener =
        object : NativeExpressAdListener {
            override fun onError(code: Int, message: String) {
                Timber.d("NativeExpressAdListener loaded fail .code=$code,message=$message")
            }

            override fun onNativeExpressAdLoad(ads: List<TTNativeExpressAd>) {
                if (ads == null || ads.isEmpty()) {
                    return
                }
                mTTNativeExpressAd = ads[0]
                mTTNativeExpressAd?.let {
                    it.setExpressInteractionListener(mExpressAdInteractionListener)
                    bindDislike(it)
                    it.render()
                }
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
                val content = CellContentModel()
                content.isAd = true
                content.templateAd = view
                mContentlist.add(adPosition, content)
                mAdapter.notifyItemInserted(adPosition)

            }
        }

    private fun bindDislike(ad: TTNativeExpressAd) {
        ad.setDislikeCallback(this, object : DislikeInteractionCallback {
            override fun onSelected(position: Int, value: String) {
                Timber.d("onSelected")
                mContentlist.removeAt(adPosition)
                mAdapter.notifyDataSetChanged()
            }

            override fun onCancel() {}

            override fun onRefuse() {}
        })
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

    override fun onDestroy() {
        super.onDestroy()
        mTTNativeExpressAd?.destroy()
    }
}