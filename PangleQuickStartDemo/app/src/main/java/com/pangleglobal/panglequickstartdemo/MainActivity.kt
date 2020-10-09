package com.pangleglobal.panglequickstartdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdSdk

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TTAdSdk.init(
            this,
            TTAdConfig.Builder()
                // Please use your own appId, this is for demo
                .appId("5081617")
                // The default setting is SurfaceView.
                // If using TextureView to play the video, please set this and add "WAKE_LOCK" permission in manifest
                //.useTextureView(true)
                // Whether to support multi-process, true indicates support
                .supportMultiProcess(false)
                // Fields to indicate whether you are a child or an adult ，0:adult ，1:child
                .coppa(0)
                //Fields to indicate whether you are protected by GDPR,  the value of GDPR : 0 close GDRP Privacy protection ，1: open GDRP Privacy protection
                .setGDPR(0)
                .build()
        )
        if (BuildConfig.DEBUG) {
            // Turn it on during the testing phase, you can troubleshoot with the log, remove it after launching the app
            TTAdConfig.Builder().debug(true)
        }
    }
}