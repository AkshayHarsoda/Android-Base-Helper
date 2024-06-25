package com.akshay.harsoda.android.base.helper.ads.nativead

import com.akshay.harsoda.android.base.helper.ads.callback.AdsListener
import com.google.android.gms.ads.nativead.NativeAd


data class AdMobNativeAdModel(
    var nativeAd: NativeAd? = null,
    var adsID: String = "",
    var listener: AdsListener<NativeAd>? = null,
    var isAdLoadingRunning: Boolean = false
)
