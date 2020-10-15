package com.pangleglobal.panglequickstartdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdNative.RewardVideoAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import timber.log.Timber

class RewardedVideoAdsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewarded_video_ads)

        requestRewardedVideoAd("945273302")
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
            Timber.d("RewardVideoAdListener loaded fail .code=$msg,message=$i")
        }

        override fun onRewardVideoAdLoad(ttRewardVideoAd: TTRewardVideoAd) {
            ttRewardVideoAd.showRewardVideoAd(this@RewardedVideoAdsActivity)
        }

        override fun onRewardVideoCached() {}
    }
}