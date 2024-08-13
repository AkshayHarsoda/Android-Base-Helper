package com.akshay.harsoda.android.base.helper.ads.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import com.akshay.harsoda.android.base.helper.ads.facebook.FacebookInterstitialAdModel

internal var isEnableFacebookAds: Boolean = false
internal var facebook_interstitial_ad_model_list: ArrayList<FacebookInterstitialAdModel> = ArrayList()

private var mAlertDialog: AlertDialog? = null

private var mOkayClick: () -> Unit = {}
private var mTurnOffClick: () -> Unit = {}

fun checkDeveloperOption(
    fActivity: Activity,
    isTesting: Boolean,
    @ColorInt fButtonColor: Int? = null,
    onOkayClick: () -> Unit,
    onTurnOffClick: () -> Unit,
    onNext: () -> Unit
) {

    mOkayClick = onOkayClick
    mTurnOffClick = onTurnOffClick

    if (!isTesting && isDevMode(fActivity = fActivity)) {
        mAlertDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        } ?: run {
            val builder: AlertDialog.Builder = AlertDialog.Builder(fActivity)
            builder.setMessage(
                "Please Turn Off Developer Option\n" +
                        "\n" +
                        "Go to Settings > Search developer options and toggle them off."
            )
            builder.setCancelable(false)
            var negativeButtonText = SpannableStringBuilder(" Ok ")
            fButtonColor?.let {
                negativeButtonText = getColorSpannableString(negativeButtonText, it)
            }
            builder.setNegativeButton(negativeButtonText) { dialog, _ ->
                dialog.dismiss()
                mOkayClick.invoke()
            }
            var positiveButtonText = SpannableStringBuilder(" Turn Off ")
            fButtonColor?.let {
                positiveButtonText = getColorSpannableString(positiveButtonText, it)
            }
            builder.setPositiveButton(positiveButtonText) { dialog, _ ->
                dialog.dismiss()
                mTurnOffClick.invoke()
            }
            mAlertDialog = builder.create()
            mAlertDialog?.setCanceledOnTouchOutside(false)
            mAlertDialog?.show()
        }

    } else {
        onNext.invoke()
    }
}

private fun getColorSpannableString(
    fSource: SpannableStringBuilder,
    @ColorInt fColor: Int
): SpannableStringBuilder {
    val foregroundColorSpan = ForegroundColorSpan(fColor)
    fSource.setSpan(
        foregroundColorSpan,
        0,
        fSource.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return fSource
}

private fun isDevMode(fActivity: Activity): Boolean {
    return Settings.Secure.getInt(fActivity.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0
}

val getDeveloperSettingIntent: Intent get() = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)