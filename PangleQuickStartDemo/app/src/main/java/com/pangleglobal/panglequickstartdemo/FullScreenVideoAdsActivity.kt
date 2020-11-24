package com.pangleglobal.panglequickstartdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdNative.FullScreenVideoAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd
import kotlinx.android.synthetic.main.activity_full_screen_video_ads.*
import timber.log.Timber

class FullScreenVideoAdsActivity : AppCompatActivity() {

    private lateinit var mFullScreenVideoAd: TTFullScreenVideoAd
    private var mIsCached: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_video_ads)
        load_status.text = "Please click LOAD AD"

        load_ad.setOnClickListener {
            requestFullScreenVideoAd("945277276")
        }

        show_ad.setOnClickListener {
            if(mIsCached) {
                mFullScreenVideoAd.showFullScreenVideoAd(this@FullScreenVideoAdsActivity)
            } else {
                Timber.w("Not Cached yet")
            }
        }
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
                load_status.text = "Loading failed"
            }

            override fun onFullScreenVideoAdLoad(ttFullScreenVideoAd: TTFullScreenVideoAd) {
                mFullScreenVideoAd = ttFullScreenVideoAd
                mFullScreenVideoAd.setFullScreenVideoAdInteractionListener(object : TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
                    override fun onSkippedVideo() {
                        Timber.d("fullscreen video onSkippedVideo")
                    }

                    override fun onAdShow() {
                        Timber.d("fullscreen video onAdShow")
                    }

                    override fun onAdVideoBarClick() {
                        Timber.d("fullscreen video onAdVideoBarClick")
                    }

                    override fun onVideoComplete() {
                        Timber.d("fullscreen video onVideoComplete")
                    }

                    override fun onAdClose() {
                        Timber.d("fullscreen video onAdClose")
                    }
                })
            }

            override fun onFullScreenVideoCached() {
                mIsCached = true
                load_status.text = "Video Cached"
            }
        }
}