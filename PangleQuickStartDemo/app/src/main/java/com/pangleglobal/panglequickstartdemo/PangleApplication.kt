package com.pangleglobal.panglequickstartdemo

import android.app.Application
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdSdk
import timber.log.Timber

class PangleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initSdk()
    }

    private fun initSdk() {
        TTAdSdk.init(this, buildAdConfig(), mInitCallback)
        //TTAdSdk.init(this.buildAdConfig())
    }

    private fun checkInitResult(): Boolean {
       return TTAdSdk.isInitSuccess()
    }

    private val mInitCallback: TTAdSdk.InitCallback = object : TTAdSdk.InitCallback {
        override fun success() {
            Timber.d("init succeeded")
        }

        override fun fail(p0: Int, p1: String?) {
            Timber.d("init failed. reason = $p1")
        }
    }

    private fun buildAdConfig(): TTAdConfig {
        return TTAdConfig.Builder()
            // Please use your own appId, this is for demo
            .appId("5081617")
            // Turn it on during the testing phase, you can troubleshoot with the log, remove it after launching the app
            .debug(BuildConfig.DEBUG)
            // The default setting is SurfaceView. We strongly recommend to set this to true.
            // If using TextureView to play the video, please set this and add "WAKE_LOCK" permission in manifest
            .useTextureView(true)
            // Fields to indicate whether you are a child or an adult ，0:adult ，1:child
            .coppa(0)
            //Fields to indicate whether you are protected by GDPR,  the value of GDPR. 0: close GDRP Privacy protection ，1: open GDRP Privacy protection
            .setGDPR(0)
            .build()
    }
}
