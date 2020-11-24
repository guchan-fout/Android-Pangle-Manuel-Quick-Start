package com.pangleglobal.panglequickstartdemo.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.bytedance.sdk.openadsdk.TTAdDislike.DislikeInteractionCallback
import com.bytedance.sdk.openadsdk.TTFeedAd
import com.bytedance.sdk.openadsdk.TTNativeAd
import com.pangleglobal.panglequickstartdemo.R
import com.pangleglobal.panglequickstartdemo.RecyclerAdapter
import com.pangleglobal.panglequickstartdemo.model.CellContentModel
import kotlinx.android.synthetic.main.native_ad_cell.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import timber.log.Timber


class OriginNativeAdsAdapter(private val contentList: ArrayList<CellContentModel>) :
    RecyclerView.Adapter<RecyclerAdapter.RecyclerAdapterViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): RecyclerAdapter.RecyclerAdapterViewHolder {
        val view: View
        return if (viewType == TYPE_NORMAL) { // for call layout
            view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recyclerview_feed_item, viewGroup, false)
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

            /**
             *  here is to set native ad's date to the view
             */
            val ad: TTFeedAd = contentList[position].feedAd
            holder.view.titleText.text = ad.title
            holder.view.descText.text = ad.description
            holder.view.adButton.text = ad.buttonText
            Glide.with(holder.view).asBitmap().load(ad.icon.imageUrl).into(holder.view.logoView)

            // this is video ad
            if (ad.imageMode == TTAdConstant.IMAGE_MODE_VIDEO || ad.imageMode == TTAdConstant.IMAGE_MODE_VIDEO_SQUARE || ad.imageMode == TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {
                val videoAd = ad.adView
                if (videoAd != null) {
                    Timber.d("video exist")
                    holder.view.containerFrame.addView(ad.adView)
                } else {
                    Timber.d("video not exist")
                    val imageView = ImageView(holder.view.context)
                    Glide.with(holder.view).asBitmap().load(ad.imageList[0].imageUrl)
                        .into(imageView)
                    holder.view.containerFrame.addView(imageView)
                }
            } else {
                // this is an image ad
                val imageView = ImageView(holder.view.context)
                Glide.with(holder.view).asBitmap().load(ad.imageList[0].imageUrl).into(imageView)
                holder.view.containerFrame.addView(imageView)
            }

            // Pangle logo view, will show privacy information if clicked
            holder.view.adLogoView.addView(ad.adLogoView)
            holder.view.adLogoView.bringToFront()

            // set dislike button on top
            holder.view.dislikeButton.bringToFront()
            bindDislikeAction(ad, holder.view.dislikeButton, holder)

            // register the view for click
            ad.registerViewForInteraction(
                holder.view as ViewGroup,
                holder.view.adButton,
                mTTNativeAdListener
            )
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

    private fun bindDislikeAction(
        ad: TTNativeAd,
        dislikeView: View,
        holder: RecyclerAdapter.RecyclerAdapterViewHolder
    ) {
        val ttAdDislike: TTAdDislike = ad.getDislikeDialog(dislikeView.context as Activity)
        ttAdDislike?.setDislikeInteractionCallback(object : DislikeInteractionCallback {
            override fun onSelected(position: Int, value: String) {
                // here to notify the recycleview to close the adapter
                contentList.removeAt(holder.layoutPosition)
                notifyDataSetChanged()
            }

            override fun onCancel() {
                Timber.d("onCancel")
            }
        })
        dislikeView.setOnClickListener { ttAdDislike?.showDislikeDialog() }
    }
}