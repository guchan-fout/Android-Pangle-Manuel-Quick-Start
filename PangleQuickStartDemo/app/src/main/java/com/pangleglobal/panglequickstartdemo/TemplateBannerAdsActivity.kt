package com.pangleglobal.panglequickstartdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.bytedance.sdk.openadsdk.TTAdNative.NativeExpressAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.bytedance.sdk.openadsdk.TTNativeExpressAd.ExpressAdInteractionListener
import kotlinx.android.synthetic.main.activity_template_banner_ads.*
import timber.log.Timber

class TemplateBannerAdsActivity : AppCompatActivity() {

    private var mTTTemplateBannerAd: TTNativeExpressAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_banner_ads)

        load_rectangle_ad.setOnClickListener { requestTemplateRectanglerAd("945557236") }
        load_banner_button.setOnClickListener { requestTemplateBannerAd("945669957") }
    }

    fun requestTemplateRectanglerAd(mPlacementID: String) {
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
        mTTAdNative.loadBannerExpressAd(adSlot, mTTRectangleExpressAdListener)
    }

    fun requestTemplateBannerAd(mPlacementID: String) {
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
            .setAdCount(1)
            .setExpressViewAcceptedSize(320F, 50F)
            .build()
        mTTAdNative.loadBannerExpressAd(adSlot, mTTBannerExpressAdListener)
    }

    private val mTTBannerExpressAdListener: NativeExpressAdListener =
        object : NativeExpressAdListener {
            override fun onError(code: Int, message: String) {
                Timber.d("NativeExpressAdListener loaded fail .code=$code,message=$message")
            }

            override fun onNativeExpressAdLoad(ads: List<TTNativeExpressAd>) {
                if (ads == null || ads.isEmpty()) {
                    return
                }
                mTTTemplateBannerAd = ads[0]

                mTTTemplateBannerAd?.let {
                    it.setExpressInteractionListener(mExpressBannerAdInteractionListener)
                    bindDislike(it)
                    it.render()
                }
            }
        }

    private val mExpressBannerAdInteractionListener: ExpressAdInteractionListener =
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


    private val mTTRectangleExpressAdListener: NativeExpressAdListener =
        object : NativeExpressAdListener {
            override fun onError(code: Int, message: String) {
                Timber.d("NativeExpressAdListener loaded fail .code=$code,message=$message")
            }

            override fun onNativeExpressAdLoad(ads: List<TTNativeExpressAd>) {
                if (ads == null || ads.isEmpty()) {
                    return
                }
                mTTTemplateBannerAd = ads[0]

                mTTTemplateBannerAd?.let {
                    it.setExpressInteractionListener(mExpressRectangleAdInteractionListener)
                    bindDislike(it)
                    it.render()
                }
            }
        }

    private val mExpressRectangleAdInteractionListener: ExpressAdInteractionListener =
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
                rectangleFrame.addView(view)
            }
        }

    private fun bindDislike(ad: TTNativeExpressAd) {
        ad.setDislikeCallback(this, object : TTAdDislike.DislikeInteractionCallback {
            override fun onSelected(position: Int, value: String) {
                Timber.d("onSelected")
                rectangleFrame.removeAllViews()
                bannerFrame.removeAllViews()
            }

            override fun onCancel() {}

            override fun onRefuse() {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mTTTemplateBannerAd?.destroy()
    }
}