package com.pangleglobal.panglequickstartdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdNative.RewardVideoAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import kotlinx.android.synthetic.main.activity_full_screen_video_ads.*
import timber.log.Timber

class RewardedVideoAdsActivity : AppCompatActivity() {
    private var mRewardVideoAd: TTRewardVideoAd? = null
    private var mIsCached: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewarded_video_ads)

        load_status.text = "Please click LOAD AD"

        load_ad.setOnClickListener {
            requestRewardedVideoAd("945273302")
        }

        show_ad.setOnClickListener {
            if (mIsCached) {
                mRewardVideoAd?.showRewardVideoAd(this)
            } else {
                Timber.w("Not Cached yet")
            }
        }
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

    private val mRewardedAdListener: RewardVideoAdListener = object : RewardVideoAdListener {
        override fun onError(i: Int, msg: String) {
            Timber.d("RewardVideoAdListener loaded fail .code=$i,message=$msg")
            load_status.text = "Loading failed"
        }

        override fun onRewardVideoAdLoad(ttRewardVideoAd: TTRewardVideoAd) {
            mRewardVideoAd = ttRewardVideoAd
            mRewardVideoAd?.setRewardAdInteractionListener(object :
                TTRewardVideoAd.RewardAdInteractionListener {
                override fun onRewardVerify(p0: Boolean, p1: Int, p2: String?) {
                    Timber.d("reward video onRewardVerify")
                }

                override fun onSkippedVideo() {
                    Timber.d("reward video onSkippedVideo")
                }

                override fun onAdShow() {
                    Timber.d("reward video onAdShow")
                    // Only first show is a valid impression, please reload again to get another ad.
                    mRewardVideoAd = null
                    load_status.text = "Please reload"
                }

                override fun onAdVideoBarClick() {
                    Timber.d("reward video onAdVideoBarClick")
                }

                override fun onVideoComplete() {
                    Timber.d("reward video onVideoComplete")
                }

                override fun onAdClose() {
                    Timber.d("reward video onAdClose")
                }

                override fun onVideoError() {
                    Timber.d("reward video onVideoError")
                }
            })
        }

        override fun onRewardVideoCached() {
            mIsCached = true
            load_status.text = "Video Cached"
        }
    }
}