package com.akshay.harsoda.android.base.helper.ads.nativead

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.multidex.BuildConfig
import com.akshay.harsoda.android.base.helper.R
import com.akshay.harsoda.android.base.helper.ads.utils.isInternetAvailable
import com.akshay.harsoda.android.base.helper.ads.utils.isOnline
import com.akshay.harsoda.android.base.helper.ads.utils.logE
import com.akshay.harsoda.android.base.helper.base.utils.gone
import com.akshay.harsoda.android.base.helper.base.utils.inflater
import com.akshay.harsoda.android.base.helper.base.utils.visible
import com.akshay.harsoda.android.base.helper.databinding.LayoutNativeAdMainBinding
import com.google.android.gms.ads.nativead.NativeAdOptions

internal var admob_native_ad_model_list: ArrayList<AdMobNativeAdModel> = ArrayList()

class NativeAdHelper : FrameLayout {

    private val TAG: String = "AdsHelper_${javaClass.simpleName}"

    private var customLayout = 0

    interface OnNativeAdListener {
        fun onAdLoaded()
        fun onAdFailed()
    }

    private var mListener: OnNativeAdListener? = null

    private var isAdLoaded: Boolean = false

    enum class NativeAdType(var id: Int) {
        BANNER(0), BIG(1), CUSTOM(2);

        companion object {
            @JvmStatic
            fun fromId(id: Int): NativeAdType {
                for (t in values()) {
                    if (t.id == id) return t
                }
                throw IllegalArgumentException()
            }
        }
    }

    private fun getInflatedLayout(@LayoutRes id: Int) = mThisView.context.inflater.inflate(id, mThisView, false)

    private var mNativeAdType: NativeAdType = NativeAdType.BANNER

    private val mThisView: NativeAdHelper get() = this@NativeAdHelper

    private var mBinding: LayoutNativeAdMainBinding = LayoutNativeAdMainBinding.inflate(LayoutInflater.from(context), mThisView, true)

    //<editor-fold desc="Get Ad ID From List">
    private val adMobNativeAd: AdMobNativeAdHelper by lazy {
        val adIDList: ArrayList<AdMobNativeAdModel> = ArrayList()

        admob_native_ad_model_list.forEach {
            adIDList.add(
                AdMobNativeAdModel(
                    adsID = it.adsID
                )
            )
        }

        AdMobNativeAdHelper(mThisView.context, adIDList)
    }
    //</editor-fold>

    //<editor-fold desc="Constructor">
    constructor(context: Context) : super(context) {
        setUpLayout(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setUpLayout(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setUpLayout(context, attrs)
    }

    private fun refreshView() {
        invalidate()
    }
    //</editor-fold>

    private fun setUpLayout(context: Context, attrs: AttributeSet?) {

        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.NativeAdHelper, 0, 0)
            mNativeAdType = NativeAdType.fromId(a.getInt(R.styleable.NativeAdHelper_native_ad_type, 0))

            if (a.hasValue(R.styleable.NativeAdHelper_customLayout)) {
                customLayout = a.getResourceId(R.styleable.NativeAdHelper_customLayout, 0)
            }

            if (a.hasValue(R.styleable.NativeAdHelper_cardBackgroundColor)) {
                val backgroundColor = a.getColorStateList(R.styleable.NativeAdHelper_cardBackgroundColor)
                mBinding.cvAdContainer.setCardBackgroundColor(backgroundColor)
            }

            if (a.hasValue(R.styleable.NativeAdHelper_cardCornerRadius)) {
                val radius = a.getDimension(R.styleable.NativeAdHelper_cardCornerRadius, 0f)
                mBinding.cvAdContainer.radius = radius
            }

            if (a.hasValue(R.styleable.NativeAdHelper_cardElevation)) {
                val cardElevation = a.getDimension(R.styleable.NativeAdHelper_cardElevation, 0f)
                mBinding.cvAdContainer.cardElevation = cardElevation
            }

            if (a.hasValue(R.styleable.NativeAdHelper_cardMaxElevation)) {
                val maxElevation = a.getDimension(R.styleable.NativeAdHelper_cardMaxElevation, 0f)
                mBinding.cvAdContainer.cardElevation = maxElevation
            }

            if (a.hasValue(R.styleable.NativeAdHelper_cardUseCompatPadding)) {
                val useCompatPadding = a.getBoolean(R.styleable.NativeAdHelper_cardUseCompatPadding, false)
                mBinding.cvAdContainer.useCompatPadding = useCompatPadding
            }

            if (a.hasValue(R.styleable.NativeAdHelper_cardPreventCornerOverlap)) {
                val preventCornerOverlap = a.getBoolean(R.styleable.NativeAdHelper_cardPreventCornerOverlap, false)
                mBinding.cvAdContainer.preventCornerOverlap = preventCornerOverlap
            }

            if (a.hasValue(R.styleable.NativeAdHelper_contentPadding)
                || a.hasValue(R.styleable.NativeAdHelper_contentPaddingLeft)
                || a.hasValue(R.styleable.NativeAdHelper_contentPaddingRight)
                || a.hasValue(R.styleable.NativeAdHelper_contentPaddingTop)
                || a.hasValue(R.styleable.NativeAdHelper_contentPaddingBottom)
            ) {
                val defaultPadding = a.getDimensionPixelSize(R.styleable.NativeAdHelper_contentPadding, 0)
                val contentPaddingLeft = a.getDimensionPixelSize(R.styleable.NativeAdHelper_contentPaddingLeft, defaultPadding)
                val contentPaddingRight = a.getDimensionPixelSize(R.styleable.NativeAdHelper_contentPaddingRight, defaultPadding)
                val contentPaddingTop = a.getDimensionPixelSize(R.styleable.NativeAdHelper_contentPaddingTop, defaultPadding)
                val contentPaddingBottom = a.getDimensionPixelSize(R.styleable.NativeAdHelper_contentPaddingBottom, defaultPadding)
                mBinding.cvAdContainer.setContentPadding(contentPaddingLeft, contentPaddingTop, contentPaddingRight, contentPaddingBottom)
            }

            a.recycle()
        }

        refreshView()

        hideShowParent()

        refreshView()

        /*mThisView.addOnAttachStateChangeListener(
            object : OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {

                }

                override fun onViewDetachedFromWindow(v: View) {
                    logE(TAG, "Destroy Ad View::-> ${mThisView.tag}")
                    isAdLoaded = false
                    faceBookNativeAd.destroy()
                    faceBookNativeBannerAd.destroy()
                    adMobNativeAd.destroy()
                    applovinNativeAd.destroy()
                }
            }
        )*/

        isInternetAvailable.observeForever {
            if (it) {
                if (!mThisView.isInEditMode) {
                    logE(TAG, "Load New View::-> ${mThisView.tag}")
                    loadAd()
                }
            }
        }
    }

    fun updateAdType(fNativeAdType: NativeAdType, action: () -> Unit = {}) {
        if (mNativeAdType != fNativeAdType) {
            mNativeAdType = fNativeAdType
            refreshView()

            isAdLoaded = false
            adMobNativeAd.updateType()

            hideShowParent()

            if (!mThisView.isInEditMode) {
                logE(TAG, "Load New View::updateAdType -> ${mThisView.tag}")
                loadAd()
                action.invoke()
            }
            refreshView()
        }
    }

    fun setOnNativeAdListener(fListener: OnNativeAdListener) {
        mListener = fListener
    }

    private inline val View.hideParentView: View
        get() {
            this.visible

            return this
        }

    private inline val View.hideOtherView: View
        get() {
            with(mBinding) {
                facebookNativeAdContainer.gone
                lyAdLoading.root.gone
            }

            if (BuildConfig.DEBUG) {
                this.visible
//            } else if (!mFirebaseDataModel.isEnableFacebookAds
//                && !mFirebaseDataModel.isEnableAdmobAds
//                && !mFirebaseDataModel.isEnableApplovinAds
//            ) {
//                this.gone
//                mThisView.gone
            } else {
                this.visible
            }

            return this
        }

    private fun View.attachToLoadingLayout() {
        mBinding.lyAdLoading.loadingNativeAdContainer.let { container ->
            container.removeAllViews()
            container.addView(this, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            mBinding.lyAdLoading.root.hideOtherView
        }
    }

    private fun hideShowParent() {
        when (mNativeAdType) {
            NativeAdType.BANNER, NativeAdType.BIG -> {
                mBinding.root.hideParentView

                if (mNativeAdType == NativeAdType.BANNER) {
                    getInflatedLayout(R.layout.native_ad_banner_type).attachToLoadingLayout()
                } else {
                    getInflatedLayout(R.layout.native_ad_big_type).attachToLoadingLayout()
                }
            }

            NativeAdType.CUSTOM -> {
                mBinding.root.hideParentView
                getInflatedLayout(customLayout).attachToLoadingLayout()
            }
        }
    }


    fun loadAd() {
        if (!isAdLoaded) {
            hideShowParent()

//            mThisView.beVisibleIf(mFirebaseDataModel.isNativeAdShowed)

//            if (mFirebaseDataModel.isNativeAdShowed) {
                if (isOnline
                    && !isAdLoaded
                ) {
                    loadAdWithCloseListener(
                        onAdLoaded = {
                            logE(TAG, "Ad Loaded Now Show View")
                            isAdLoaded = true
                            refreshView()
                            callLoaded()
                        },
                        onAdFailed = {
                            isAdLoaded = false
                            callFailed()
                        }
                    )
                }
//            }
        }
    }

    private fun callLoaded() {
        Handler(Looper.getMainLooper()).postDelayed({
            mListener?.onAdLoaded() ?: kotlin.run { callLoaded() }
        }, 100)
    }

    private fun callFailed() {
        Handler(Looper.getMainLooper()).postDelayed({
            mListener?.onAdFailed() ?: kotlin.run { callFailed() }
        }, 100)
    }

    private fun loadAdWithCloseListener(
        onAdLoaded: () -> Unit = {},
        onAdFailed: () -> Unit
    ) {
//        when (mFirebaseDataModel.allAdHostType) {
//            AdHostType.NONE -> {}
//            AdHostType.FACE_BOOK -> startLoadingFacebook(onAdLoaded = onAdLoaded, onAdFailed = onAdFailed)
            /*AdHostType.AD_MOB ->*/ startLoadingAdMob(onAdLoaded = onAdLoaded, onAdFailed = onAdFailed)
//            AdHostType.IRON_SOURCE -> {}
//            AdHostType.APPLOVIN -> startLoadingApplovin(onAdLoaded = onAdLoaded, onAdFailed = onAdFailed)
//        }
    }



    private fun startLoadingAdMob(
        onAdLoaded: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        loadAdMobAd(
            onAdLoaded = onAdLoaded,
            onAdFailed = onAdFailed,
        )
    }

    private fun loadAdMobAd(onAdLoaded: () -> Unit, onAdFailed: () -> Unit) {
//        if (mFirebaseDataModel.isEnableAdmobAds) {
            when (mNativeAdType) {
                NativeAdType.BANNER -> {
                    loadAdMobNativeAd(
                        layoutId = R.layout.native_ad_banner_type,
                        adContainerView = mBinding.facebookNativeAdContainer,
                        onAdLoaded = onAdLoaded,
                        onAdFailed = onAdFailed
                    )
                }

                NativeAdType.BIG -> {
                    loadAdMobNativeAd(
                        layoutId = R.layout.native_ad_big_type,
                        adContainerView = mBinding.facebookNativeAdContainer,
                        onAdLoaded = onAdLoaded,
                        onAdFailed = onAdFailed
                    )
                }

                NativeAdType.CUSTOM -> {
                    loadAdMobNativeAd(
                        layoutId = customLayout,
                        adContainerView = mBinding.facebookNativeAdContainer,
                        onAdLoaded = onAdLoaded,
                        onAdFailed = onAdFailed
                    )
                }
            }
//        } else {
//            onAdFailed.invoke()
//        }
    }



    //<editor-fold desc="Load Admob Native Ad">
    private fun View.attachToAdMobNativeAdLayout(): com.google.android.gms.ads.nativead.NativeAdView {

        this.findViewById<FrameLayout>(R.id.native_ad_media_container)?.let { ly ->
            val lMediaView: com.google.android.gms.ads.nativead.MediaView = com.google.android.gms.ads.nativead.MediaView(mThisView.context)
            lMediaView.id = R.id.native_ad_media
            ly.addView(lMediaView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

        val lNativeAdLayout: com.google.android.gms.ads.nativead.NativeAdView = com.google.android.gms.ads.nativead.NativeAdView(mThisView.context)
        lNativeAdLayout.addView(this)

        return lNativeAdLayout
    }

    private fun loadAdMobNativeAd(
        @LayoutRes layoutId: Int,
        adContainerView: FrameLayout,
        onAdLoaded: () -> Unit,
        onAdFailed: () -> Unit
    ) {

        adContainerView.removeAllViews()
        val lNativeAdLayout: com.google.android.gms.ads.nativead.NativeAdView = getInflatedLayout(layoutId).attachToAdMobNativeAdLayout()
        adContainerView.addView(lNativeAdLayout)

        adMobNativeAd.let { adHelper ->
            adHelper.loadAd(
                isAddVideoOptions = true,
                adChoicesPlacement = NativeAdOptions.ADCHOICES_TOP_RIGHT,
                onAdLoaded = { _, nativeAd ->
                    adHelper.populateNativeAdView(
                        fNativeAd = nativeAd,
                        nativeAdView = lNativeAdLayout,
                        onAdSet = {
                            adContainerView.hideOtherView
                            onAdLoaded.invoke()
                        }
                    )
                },
                onAdClosed = {
                    isAdLoaded = false
                    requestLayout()
                    invalidate()
                    loadAd()
                },
                onAdFailed = {
                    onAdFailed.invoke()
                }
            )
        }
    }
    //</editor-fold>

}