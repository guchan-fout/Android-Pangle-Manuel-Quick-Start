package com.pangleglobal.panglequickstartdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdNative.FullScreenVideoAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd
import timber.log.Timber

class FullScreenVideoAdsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_video_ads)
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


    private val mTTFullScreenAdListener: FullScreenVideoAdListener =
        object : FullScreenVideoAdListener {
            override fun onError(i: Int, s: String) {
                Timber.d("FullScreenVideoAdListener loaded fail .code=$s,message=$i")
            }

            override fun onFullScreenVideoAdLoad(ttFullScreenVideoAd: TTFullScreenVideoAd) {
                ttFullScreenVideoAd.showFullScreenVideoAd(this@FullScreenVideoAdsActivity)
            }

            override fun onFullScreenVideoCached() {}
        }
}