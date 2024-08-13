package com.akshay.harsoda.android.base.helper.ads.facebook

import com.akshay.harsoda.android.base.helper.ads.callback.AdsListener
import com.facebook.ads.InterstitialAd

data class FacebookInterstitialAdModel(
    var interstitialAd: InterstitialAd? = null,
    var adsID: String = "",
    var listener: AdsListener<InterstitialAd>? = null,
    var isAdLoadingRunning: Boolean = false
)
