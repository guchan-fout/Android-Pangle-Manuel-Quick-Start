package com.pangleglobal.panglequickstartdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytedance.sdk.openadsdk.TTFeedAd
import com.bytedance.sdk.openadsdk.TTNativeAd
import com.pangleglobal.panglequickstartdemo.R
import com.pangleglobal.panglequickstartdemo.RecyclerAdapter
import com.pangleglobal.panglequickstartdemo.model.CellContentModel
import kotlinx.android.synthetic.main.native_ad_cell.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import timber.log.Timber


class CellAdapter(private val contentList: ArrayList<CellContentModel>) :
    RecyclerView.Adapter<RecyclerAdapter.RecyclerAdapterViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerAdapter.RecyclerAdapterViewHolder {
        val view: View
        return if (viewType == TYPE_NORMAL) { // for call layout
            view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recyclerview_item, viewGroup, false)
            RecyclerAdapter.RecyclerAdapterViewHolder(view)
        } else {
            view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.native_ad_cell, viewGroup, false)
            RecyclerAdapter.RecyclerAdapterViewHolder(view)
        }

    }

    override fun onBindViewHolder(
        holder: RecyclerAdapter.RecyclerAdapterViewHolder,
        position: Int
    ) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            holder.view.item_name.text = contentList[position].content
        } else {
            var ad: TTFeedAd = contentList[position].feedAd
            holder.view.titleText.text = ad.title
            holder.view.descText.text = ad.description
            holder.view.adButton.text = ad.buttonText
            Glide.with(holder.view).asBitmap().load(ad.icon.imageUrl).into(holder.view.logoView)
            var videoAd = ad.adView
            if (videoAd != null) {
                Timber.d("video ad")
                holder.view.containerFrame.addView(ad.adView)
            } else {
                Timber.d("image ad")
                val imageView = ImageView(holder.view.context)

                Glide.with(holder.view).asBitmap().load(ad.imageList[0].imageUrl).into(imageView)
                holder.view.containerFrame.addView(imageView)
            }

            holder.view.adLogoView.setImageBitmap(ad.adLogo)

            ad.registerViewForInteraction(holder.view as ViewGroup,holder.view.adButton, mTTNativeAdListener)
        }
    }

    private val mTTNativeAdListener: TTNativeAd.AdInteractionListener =
        object : TTNativeAd.AdInteractionListener {
            override fun onAdClicked(p0: View?, p1: TTNativeAd?) {
                Timber.d("onAdClicked")
            }

            override fun onAdCreativeClick(p0: View?, p1: TTNativeAd?) {
                Timber.d("onAdCreativeClick")
            }

            override fun onAdShow(p0: TTNativeAd?) {
                Timber.d("onAdShow")
            }

        }

    override fun getItemCount(): Int {
        return contentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (!contentList[position].isAd) {
            TYPE_NORMAL
        } else {
            Timber.d("isAD!!")
            TYPE_AD
        }
    }

    companion object {
        private const val TYPE_NORMAL = 1
        private const val TYPE_AD = 2
    }
}