package com.pangleglobal.panglequickstartdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdSdk
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        TTAdSdk.init(
            this,
            TTAdConfig.Builder()
                // Please use your own appId, this is for demo
                .appId("5081617")
                .appName(packageName)
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

    override fun onStart() {
        super.onStart()
        val list = arrayOf("Origin Native", "Interstitial","Template Native Feed","Template Banner")
        val adapter =
            RecyclerAdapter(list)
        val layoutManager = LinearLayoutManager(this)

        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter
        recycler_view.setHasFixedSize(true)

        adapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedText: String) {

                when (position) {
                    0 -> {
                        val intent = Intent(this@MainActivity, NativeAdsViewActivity::class.java)
                        startActivity(intent)
                    }
                    1 -> {
                        //val intent = Intent(this@MainActivity, NativeAdActivity::class.java)
                        //startActivity(intent)
                    }
                    2 -> {
                        //val intent = Intent(this@MainActivity, FullScreenVideoActivity::class.java)
                        //startActivity(intent)
                    }
                    3 -> {
                        //val intent = Intent(this@MainActivity, TemplateNativeFeedAdActivity::class.java)
                        //startActivity(intent)
                    }
                    4 -> {
                        //val intent = Intent(this@MainActivity, TemplateBannerAdActivity::class.java)
                        //startActivity(intent)
                    }
                    else -> {
                    }
                }
            }
        })
    }
}