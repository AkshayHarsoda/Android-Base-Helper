package com.akshay.harsoda.android.base.helper.ads.facebook

import android.app.Activity
import android.content.Context
import com.akshay.harsoda.android.base.helper.ads.callback.AdsListener
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.akshay.harsoda.android.base.helper.ads.utils.*

/**
 * @author Akshay Harsoda
 * @since 18 Nov 2022
 *
 * InterstitialAdHelper.kt - Simple object which has load and handle your Interstitial AD data
 */
object FaceBookInterstitialAdHelper {

    private val TAG = "AdsHelper_${javaClass.simpleName}"

    private var mListener: AdsListener<InterstitialAd>? = null

    //<editor-fold desc="This Ads Related Flag">
    private var isThisAdShowing: Boolean = false
    private var isAdsShowingFlagForDeveloper: Boolean = false
    private var isAnyIndexLoaded = false
    private var isAnyIndexAlreadyLoaded = false
    //</editor-fold>

    private var mAdIdPosition: Int = -1

    private var mOnAdLoaded: () -> Unit = {}

    private fun getInterstitialAdModel(
        onFindModel: (index: Int, interstitialAdModel: FacebookInterstitialAdModel) -> Unit
    ) {
        mAdIdPosition = if (mAdIdPosition < facebook_interstitial_ad_model_list.size) {
            if (mAdIdPosition == -1) {
                0
            } else {
                (mAdIdPosition + 1)
            }
        } else {
            0
        }

        logE(TAG, "getInterstitialAdModel: AdIdPosition -> $mAdIdPosition")

        if (mAdIdPosition >= 0 && mAdIdPosition < facebook_interstitial_ad_model_list.size) {
            onFindModel.invoke(mAdIdPosition, facebook_interstitial_ad_model_list[mAdIdPosition])
        } else {
            mAdIdPosition = -1
        }
    }

    // TODO: Load Single Ad Using Model Class
    private fun loadNewAd(
        fContext: Context,
        fModel: FacebookInterstitialAdModel,
        fIndex: Int
    ) {

        logI(tag = TAG, message = "loadNewAd: Index -> $fIndex\nAdsID -> ${fModel.adsID}")

        fModel.isAdLoadingRunning = true

        InterstitialAd(fContext, fModel.adsID).apply {
            loadAd(
                this.buildLoadAdConfig()
                    .withAdListener(object : InterstitialAdListener {
                        override fun onError(ad: Ad, adError: AdError) {
                            logE(tag = TAG, message = "loadNewAd: onAdFailedToLoad: Index -> $fIndex\nAd failed to load -> \nErrorCode::${adError.errorCode}\nErrorMessage::${adError.errorMessage}")

                            fModel.isAdLoadingRunning = false
                            fModel.interstitialAd = null
                            fModel.listener?.onAdFailed()
                        }

                        override fun onAdLoaded(ad: Ad) {
                            logI(tag = TAG, message = "loadNewAd: onAdLoaded: Index -> $fIndex")

                            fModel.isAdLoadingRunning = false
                            fModel.interstitialAd = this@apply
                            fModel.listener?.onAdLoaded(this@apply)
                        }

                        override fun onAdClicked(ad: Ad) {

                        }

                        override fun onLoggingImpression(ad: Ad) {

                        }

                        override fun onInterstitialDisplayed(ad: Ad) {
                            logI(tag = TAG, message = "loadNewAd: onAdShowedFullScreenContent: Index -> $fIndex")
//                            isAnyAdShowing = true
                            isAdsShowingFlagForDeveloper = true
                        }

                        override fun onInterstitialDismissed(ad: Ad) {
                            logI(tag = TAG, message = "loadNewAd: onAdDismissedFullScreenContent: Index -> $fIndex")
                            fModel.interstitialAd = null

//                            isAnyAdShowing = false
                            isThisAdShowing = false

                            fModel.listener?.onAdClosed()
                        }

                    })
                    .build()
            )
        }
    }

    private fun requestWithIndex(
        fContext: Context,
        interstitialAdModel: FacebookInterstitialAdModel,
        index: Int,
        onAdLoaded: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        if (isOnline
            && interstitialAdModel.interstitialAd == null
            && !interstitialAdModel.isAdLoadingRunning
        ) {
            loadNewAd(
                fContext = fContext,
                fModel = interstitialAdModel.apply {
                    this.listener = object : AdsListener<InterstitialAd> {

                        override fun onAdLoaded(fAd: InterstitialAd) {
                            super.onAdLoaded(fAd)
                            mAdIdPosition = -1
                            logI(tag = TAG, message = "requestWithIndex: onInterstitialAdLoaded: Index -> $index")
                            if (!isAnyIndexLoaded) {
                                isAnyIndexLoaded = true
                                onAdLoaded.invoke()
                                if (onAdLoaded != mOnAdLoaded) {
                                    mOnAdLoaded.invoke()
                                }
                            }
                        }

                        override fun onAdClosed() {
                            super.onAdClosed()
                            mListener?.onAdClosed()
                        }

                        override fun onAdFailed() {
                            super.onAdFailed()
                            onAdFailed.invoke()
                        }
                    }
                },
                fIndex = index
            )
        } else if (isOnline
            && interstitialAdModel.interstitialAd != null
        ) {
            if (!isAnyIndexAlreadyLoaded) {
                logI(tag = TAG, message = "requestWithIndex: already loaded ad Index -> $index")
                isAnyIndexAlreadyLoaded = true
                onAdLoaded.invoke()
                if (onAdLoaded != mOnAdLoaded) {
                    mOnAdLoaded.invoke()
                }
                mAdIdPosition = -1
            }
        }
    }

    internal fun loadAd(
        fContext: Context,
        onAdLoaded: () -> Unit = {},
        onAdFailed: () -> Unit = {},
    ) {
        if (isEnableFacebookAds) {
            mOnAdLoaded = onAdLoaded
            isAnyIndexLoaded = false
            isAnyIndexAlreadyLoaded = false

            if (isOnline
                && /*(mFirebaseDataModel.fbInterstitialAdShowCounter > 0
                        || mFirebaseDataModel.backFbInterstitialAdShowCounter > 0)
                &&*/ facebook_interstitial_ad_model_list.isNotEmpty()
            ) {
                logI(tag = TAG, message = "loadAd: Request Ad After Failed Previous Index Ad")
                getInterstitialAdModel { index, interstitialAdModel ->
                    logI(tag = TAG, message = "loadAd: getInterstitialAdModel: Index -> $index")
                    requestWithIndex(
                        fContext = fContext,
                        interstitialAdModel = interstitialAdModel,
                        index = index,
                        onAdLoaded = onAdLoaded,
                        onAdFailed = {
                            if ((mAdIdPosition + 1) >= facebook_interstitial_ad_model_list.size) {
                                mAdIdPosition = -1
                                onAdFailed.invoke()
                            } else {
                                loadAd(fContext = fContext, onAdLoaded = mOnAdLoaded)
                            }
                        },
                    )
                }
            } else {
                onAdFailed.invoke()
            }
        } else {
            onAdFailed.invoke()
        }
    }

    internal fun showInterstitialAd(
        fActivity: Activity,
        onAdClosed: (isAdShowing: Boolean) -> Unit
    ) {
        if (isEnableFacebookAds
            && isOnline
            && facebook_interstitial_ad_model_list.isNotEmpty()
        ) {

            mListener = object : AdsListener<InterstitialAd> {
                override fun onAdClosed() {
//                    if (isAppForeground) {
                        onAdClosed.invoke(isAdsShowingFlagForDeveloper)
                        isAdsShowingFlagForDeveloper = false
//                    }

                    logI(tag = TAG, message = "showInterstitialAd: onAdClosed: Load New Ad")
                    loadAd(fContext = fActivity, onAdLoaded = mOnAdLoaded)
                }
            }

            val loadedAdModel: FacebookInterstitialAdModel? = facebook_interstitial_ad_model_list.find { it.interstitialAd != null }

            loadedAdModel?.let {
                val lIndex: Int = facebook_interstitial_ad_model_list.indexOf(it)

                it.interstitialAd?.let { interstitialAd ->
//                    if (isNeedToShowAds && !isThisAdShowing && !isAnyAdShowing) {
                        if (interstitialAd.isAdLoaded && !interstitialAd.isAdInvalidated) {
//                            if (!isAnyAdShowing) {
                                isAdsShowingFlagForDeveloper = false
//                                isAnyAdShowing = true
                                interstitialAd.show()
                                logI(tag = TAG, message = "showInterstitialAd: Show Interstitial Ad Index -> $lIndex")
                                isThisAdShowing = true
//                            }
                        }
//                    }
                }
            }
        }

        if (!isThisAdShowing) {
            mListener?.onAdClosed() ?: kotlin.run { onAdClosed.invoke(false) }
        }
    }

    internal fun destroy() {
        mListener = null
        isThisAdShowing = false
        isAnyIndexLoaded = false
        isAnyIndexAlreadyLoaded = false
        mAdIdPosition = -1

        for (data in facebook_interstitial_ad_model_list) {
            data.interstitialAd?.destroy()
            data.interstitialAd = null
            data.listener = null
            data.isAdLoadingRunning = false
        }
    }
}