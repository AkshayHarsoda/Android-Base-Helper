@file:Suppress("unused")

package com.akshay.harsoda.android.base.helper.ads.nativead

import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.akshay.harsoda.android.base.helper.R
import com.akshay.harsoda.android.base.helper.ads.callback.AdsListener
import com.akshay.harsoda.android.base.helper.ads.utils.getCamelCaseString
import com.akshay.harsoda.android.base.helper.ads.utils.isOnline
import com.akshay.harsoda.android.base.helper.ads.utils.logE
import com.akshay.harsoda.android.base.helper.ads.utils.logI
import com.akshay.harsoda.android.base.helper.base.utils.gone
import com.akshay.harsoda.android.base.helper.base.utils.visible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * @author Akshay Harsoda
 * @since 20 Dec 2022
 *
 * NativeAdHelper.kt - Simple object which has load Native Advanced AD data
 */
internal class AdMobNativeAdHelper(private val mContext: Context, private val nativeAdModelList: ArrayList<AdMobNativeAdModel>) {

    private val TAG = "AdsHelper_${javaClass.simpleName}"

    //<editor-fold desc="This Ads Related Flag">
    private var isThisAdShowing: Boolean = false
    private var isAnyIndexLoaded = false
    private var isAnyIndexAlreadyLoaded = false
    //</editor-fold>

    private var showingAdIndex: Int = -1
    private var mAdIdPosition: Int = -1

    private var mOnAdLoaded: (index: Int, nativeAd: NativeAd) -> Unit = { _, _ -> }

    private fun getNativeAdModel(
        onFindModel: (index: Int, nativeAdModel: AdMobNativeAdModel) -> Unit
    ) {
        mAdIdPosition = if (mAdIdPosition < nativeAdModelList.size) {
            if (mAdIdPosition == -1) {
                0
            } else {
                (mAdIdPosition + 1)
            }
        } else {
            0
        }

        logE(TAG, "getNativeAdModel: AdIdPosition -> $mAdIdPosition")

        if (mAdIdPosition >= 0 && mAdIdPosition < nativeAdModelList.size) {
            onFindModel.invoke(mAdIdPosition, nativeAdModelList[mAdIdPosition])
        } else {
            mAdIdPosition = -1
        }
    }


    private fun loadNewAd(
        fModel: AdMobNativeAdModel,
        fIndex: Int,
        isAddVideoOptions: Boolean,
        @NativeAdOptions.AdChoicesPlacement adChoicesPlacement: Int,
    ) {

        logI(tag = TAG, message = "loadNewAd: Index -> $fIndex\nAdsID -> ${fModel.adsID}")

        fModel.isAdLoadingRunning = true

        AdLoader.Builder(mContext, fModel.adsID)
            .withNativeAdOptions(
                NativeAdOptions.Builder().apply {
                    this.setAdChoicesPlacement(adChoicesPlacement)
                    this.setMediaAspectRatio(MediaAspectRatio.LANDSCAPE)
                    if (isAddVideoOptions) {
                        this.setVideoOptions(
                            VideoOptions.Builder()
                                .setStartMuted(true)
                                .build()
                        )
                    }
                }.build()
            )
            .forNativeAd { nativeAd ->
                fModel.isAdLoadingRunning = false
                logI(tag = TAG, message = "loadNewAd: onAdLoaded: Index -> $fIndex")
                fModel.nativeAd = nativeAd
                fModel.listener?.onAdLoaded(fAd = nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    fModel.isAdLoadingRunning = false
                    logE(tag = TAG, message = "loadNewAd: onAdFailedToLoad: Index -> $fIndex\nAd failed to load -> \nresponseInfo::${adError.responseInfo}\nErrorCode::${adError.code}\nErrorMessage::${adError.message}")
                    fModel.nativeAd = null
                    fModel.listener?.onAdFailed()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    logI(tag = TAG, message = "loadNewAd: onAdClicked: Index -> $fIndex")
//                    isAnyAdShowing = true
//                    AdMobAppOpenAdHelper.stopShowingAppOpenAdInternally()
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    logI(tag = TAG, message = "loadNewAd: onAdOpened: Index -> $fIndex")
//                    isAnyAdShowing = true
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    logI(tag = TAG, message = "loadNewAd: onAdClosed: Index -> $fIndex")
                    fModel.nativeAd = null

//                    isAnyAdShowing = false
                    isThisAdShowing = false

                    fModel.listener?.onAdClosed()
                }
            })
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    private fun requestWithIndex(
        nativeAdModel: AdMobNativeAdModel,
        isAddVideoOptions: Boolean,
        adChoicesPlacement: Int,
        index: Int,
        onAdLoaded: (index: Int, nativeAd: NativeAd) -> Unit,
        onAdClosed: (index: Int) -> Unit,
        onAdFailed: (index: Int) -> Unit,
    ) {
        if (isOnline
            && !isNativeAdAvailable()
            && nativeAdModel.nativeAd == null
            && !nativeAdModel.isAdLoadingRunning
        ) {
            logE(TAG, "New Ad Request")
            loadNewAd(
                fModel = nativeAdModel.apply {
                    this.listener = object : AdsListener<NativeAd> {

                        override fun onAdLoaded(fAd: NativeAd) {
                            super.onAdLoaded(fAd)
                            mAdIdPosition = -1
                            logI(tag = TAG, message = "requestWithIndex: onNativeAdLoaded: Index -> $index")
                            if (!isAnyIndexAlreadyLoaded) {
                                if (!isAnyIndexLoaded) {
                                    isAnyIndexLoaded = true
                                    showingAdIndex = index
                                    onAdLoaded.invoke(index, fAd)
                                }
                            }
                        }

                        override fun onAdClosed() {
                            super.onAdClosed()
                            showingAdIndex = -1
                            onAdClosed.invoke(index)
                        }

                        override fun onAdFailed() {
                            super.onAdFailed()
                            if (!isAnyIndexLoaded && !isAnyIndexAlreadyLoaded) {
                                onAdFailed.invoke(index)
                            }
                        }
                    }
                },
                fIndex = index,
                isAddVideoOptions = isAddVideoOptions,
                adChoicesPlacement = adChoicesPlacement
            )
        } else if (isOnline
            && nativeAdModel.nativeAd != null
        ) {
            nativeAdModel.nativeAd?.let { nativeAd ->
                if (!isAnyIndexAlreadyLoaded) {
                    logI(tag = TAG, message = "requestWithIndex: Index -> $index")
                    isAnyIndexAlreadyLoaded = true
                    onAdLoaded.invoke(index, nativeAd)
                    mAdIdPosition = -1
                }
            }
        }
    }

    internal fun loadAd(
        isAddVideoOptions: Boolean = true,
        @NativeAdOptions.AdChoicesPlacement adChoicesPlacement: Int,
        onAdLoaded: (index: Int, nativeAd: NativeAd) -> Unit = { _, _ -> },
        onAdClosed: (index: Int) -> Unit = { },
        onAdFailed: (index: Int) -> Unit = { },
    ) {
//        if (mFirebaseDataModel.isEnableAdmobAds) {
            mOnAdLoaded = onAdLoaded
            isAnyIndexLoaded = false
            isAnyIndexAlreadyLoaded = false

            if (nativeAdModelList.isNotEmpty()) {
                logI(tag = TAG, message = "loadAd: Request Ad After Failed Previous Index Ad")
                getNativeAdModel { index, nativeAdModel ->
                    logI(tag = TAG, message = "loadAd: getNativeAdModel: Index -> $index")
                    requestWithIndex(
                        nativeAdModel = nativeAdModel,
                        isAddVideoOptions = isAddVideoOptions,
                        adChoicesPlacement = adChoicesPlacement,
                        index = index,
                        onAdLoaded = { indexLoaded, nativeAd ->
                            mAdIdPosition = -1
                            onAdLoaded.invoke(indexLoaded, nativeAd)
                        },
                        onAdClosed = {
                            onAdClosed.invoke(index)
                        },
                        onAdFailed = {
                            if ((mAdIdPosition + 1) >= nativeAdModelList.size) {
                                mAdIdPosition = -1
                                onAdFailed.invoke(index)
                            } else {
                                loadAd(
                                    isAddVideoOptions = isAddVideoOptions,
                                    adChoicesPlacement = adChoicesPlacement,
                                    onAdLoaded = onAdLoaded,
                                    onAdClosed = onAdClosed,
                                    onAdFailed = onAdFailed
                                )
                            }
                        },
                    )
                }
            } else {
                onAdFailed.invoke(-1)
            }
//        } else {
//            onAdFailed.invoke(-1)
//        }
    }

    private fun isNativeAdAvailable(): Boolean {
        return nativeAdModelList.find { it.nativeAd != null }?.nativeAd != null
    }

    internal fun populateNativeAdView(fNativeAd: NativeAd, nativeAdView: NativeAdView, onAdSet: () -> Unit) {
        with(nativeAdView) {
            this.iconView = this.findViewById(R.id.native_ad_app_icon)
            this.headlineView = this.findViewById(R.id.native_ad_headline)
            this.advertiserView = this.findViewById(R.id.native_ad_advertiser)
            this.starRatingView = this.findViewById(R.id.native_ad_stars)
            this.bodyView = this.findViewById(R.id.native_ad_body)
            this.mediaView = this.findViewById(R.id.native_ad_media)
            this.callToActionView = this.findViewById(R.id.native_ad_call_to_action)

            this.mediaView?.let { fView ->
                fView.gone
                if (fNativeAd.mediaContent != null) {
                    fNativeAd.mediaContent?.let { fData ->
                        logI(tag = TAG, message = "populateNativeAdView: Set Media View")
                        fView.mediaContent = fData
                        fView.setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                        fView.visible
                    }
                } else {
                    populateNativeAdView(fNativeAd = fNativeAd, nativeAdView = nativeAdView, onAdSet = onAdSet)
                }
            }

            this.advertiserView?.let { fView ->
                fView.gone
                fNativeAd.advertiser?.let { fData ->
                    (fView as TextView).text = fData
                    fView.visible
                }
            }

            this.bodyView?.let { fView ->
                fView.gone
                fNativeAd.body?.let { fData ->
                    (fView as TextView).text = fData
                    fView.visible
                }
            }

            this.headlineView?.let { fView ->
                fView.gone
                fNativeAd.headline?.let { fData ->
                    (fView as TextView).text = fData
                    fView.visible
                }
            }

            this.priceView?.let { fView ->
                fView.gone
                fNativeAd.price?.let { fData ->
                    (fView as TextView).text = fData
                    fView.visible
                }
            }

            this.storeView?.let { fView ->
                with(fView as TextView) {
                    this.gone
                    fNativeAd.store?.let { fData ->
                        this.text = fData
                        this.isSelected = true
                        this.visible
                    }
                }
            }

            this.starRatingView?.let { fView ->
                fView.gone

                fNativeAd.starRating?.let { fData ->
                    (fView as RatingBar).rating = fData.toFloat()
                    fView.visible
                }
            }

            this.iconView?.let { fView ->
                fView.gone

                when {
                    fNativeAd.icon != null -> {
                        fNativeAd.icon?.drawable?.let { fData ->
                            (fView as ImageView).setImageDrawable(fData)
                            fView.visible
                        }
                    }
                    fNativeAd.images.size > 0 -> {
                        fNativeAd.images[0]?.drawable?.let { fData ->
                            (fView as ImageView).setImageDrawable(fData)
                            fView.visible
                        }
                    }
                    else -> {
                        fView.gone
                    }
                }
            }

            this.callToActionView?.let { fView ->
                fView.gone
                fNativeAd.callToAction?.let { fData ->
                    when (fView) {
                        is Button -> {
                            fView.text = getCamelCaseString(fData)
                        }
                        is androidx.appcompat.widget.AppCompatTextView -> {
                            fView.text = getCamelCaseString(fData)
                        }
                        is TextView -> {
                            fView.text = getCamelCaseString(fData)
                        }
                    }

                    fView.isSelected = true
                    fView.visible
                }
            }

            this.setNativeAd(fNativeAd)
            onAdSet.invoke()
        }
    }

    internal fun destroy() {
        for (data in nativeAdModelList) {
            data.nativeAd?.destroy()
            data.nativeAd = null
            data.listener = null
            data.isAdLoadingRunning = false
        }
    }

    internal fun updateType() {
        destroy()
        isThisAdShowing = false
        isAnyIndexLoaded = false
        isAnyIndexAlreadyLoaded = false
        showingAdIndex = -1
        mAdIdPosition = -1
    }

}