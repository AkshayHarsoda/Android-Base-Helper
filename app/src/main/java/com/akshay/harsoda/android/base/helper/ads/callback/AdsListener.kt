@file:Suppress("unused")

package com.akshay.harsoda.android.base.helper.ads.callback


/**
 * @author Akshay Harsoda
 * @since 05 Aug 2021
 *
 * AdMobAdsListener.kt - Simple interface which has notified your AD process
 */
interface AdsListener<T> {

    fun onAdFailed() {}

    fun onAdClosed() {}

    fun onAdLoaded(fAd: T) {}

}