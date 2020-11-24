package com.pangleglobal.panglequickstartdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val list =
            arrayOf(
                "Origin Native",
                "Template Native",
                "Rewarded Video",
                "Full Screen Video",
                "Template Banner"
            )
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
                        val intent =
                            Intent(this@MainActivity, TemplateNativeAdsViewActivity::class.java)
                        startActivity(intent)
                    }
                    2 -> {
                        val intent =
                            Intent(this@MainActivity, RewardedVideoAdsActivity::class.java)
                        startActivity(intent)
                    }
                    3 -> {
                        val intent =
                            Intent(this@MainActivity, FullScreenVideoAdsActivity::class.java)
                        startActivity(intent)
                    }
                    4 -> {
                        val intent =
                            Intent(this@MainActivity, TemplateBannerAdsActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                    }
                }
            }
        })
    }
}