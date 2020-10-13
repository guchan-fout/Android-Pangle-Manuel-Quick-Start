package com.pangleglobal.panglequickstartdemo.model

import com.bytedance.sdk.openadsdk.TTFeedAd

class CellContentModel {
    var content: String = ""
    var isAd: Boolean = false
    lateinit var feedAd: TTFeedAd
}