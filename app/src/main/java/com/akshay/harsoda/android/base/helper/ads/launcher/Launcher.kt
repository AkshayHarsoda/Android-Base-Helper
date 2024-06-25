package com.akshay.harsoda.android.base.helper.ads.launcher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.ColorRes
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import com.akshay.harsoda.android.base.helper.R
import com.akshay.harsoda.android.base.helper.ads.launcher.tabs.CustomTabsHelper

object Launcher {
    private fun openUri(context: Context, uri: String): Boolean = runCatching {
//        AdMobAppOpenAdHelper.stopShowingAppOpenAdInternally()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        context.startActivity(intent)
        true
    }.getOrNull() ?: false

    private fun openCustomTabs(context: Context, uri: String, @ColorRes toolbarColor: Int, isNightMode: Boolean): Boolean =
        openCustomTabs(context, Uri.parse(uri), toolbarColor, isNightMode)

    private fun openCustomTabs(context: Context, uri: Uri, @ColorRes toolbarColor: Int, isNightMode: Boolean): Boolean = runCatching {
//        AdMobAppOpenAdHelper.stopShowingAppOpenAdInternally()
        val scheme =
            if (isNightMode) CustomTabsIntent.COLOR_SCHEME_DARK
            else CustomTabsIntent.COLOR_SCHEME_LIGHT
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(context, toolbarColor))
            .build()
        val intent = CustomTabsIntent.Builder(CustomTabsHelper.session)
            .setShowTitle(true)
            .setColorScheme(scheme)
            .setDefaultColorSchemeParams(params)
            .build()
        intent.intent.setPackage(CustomTabsHelper.packageNameToBind)
        intent.launchUrl(context, uri)
        true
    }.getOrNull() ?: false

    @JvmStatic
    fun openGooglePlay(context: Context, packageName: String, @ColorRes toolbarColor: Int, isNightMode: Boolean = false): Boolean =
        openUri(context, "market://details?id=$packageName") ||
                openCustomTabs(context, "https://play.google.com/store/apps/details?id=$packageName", toolbarColor, isNightMode)

    @JvmStatic
    fun openGooglePlay(context: Context, packageName: String, @ColorRes toolbarColor: Int): Boolean =
        openGooglePlay(context = context, packageName = packageName, toolbarColor = toolbarColor, isNightMode = false)

    @JvmStatic
    fun openGooglePlay(context: Context, packageName: String, isNightMode: Boolean): Boolean =
        openGooglePlay(context = context, packageName = packageName, toolbarColor = R.color.color_green_bg, isNightMode = isNightMode)

    @JvmStatic
    fun openGooglePlay(context: Context, packageName: String): Boolean =
        openGooglePlay(context = context, packageName = packageName, toolbarColor = R.color.color_green_bg, isNightMode = false)

    @JvmStatic
    fun openPrivacyPolicy(context: Context, fLink: String, @ColorRes toolbarColor: Int, isNightMode: Boolean = false) =
        openCustomTabs(context, fLink, toolbarColor, isNightMode)

    @JvmStatic
    fun openPrivacyPolicy(context: Context, fLink: String, @ColorRes toolbarColor: Int) =
        openPrivacyPolicy(context = context, fLink = fLink, toolbarColor = toolbarColor, isNightMode = false)

    @JvmStatic
    fun openPrivacyPolicy(context: Context, fLink: String, isNightMode: Boolean) =
        openPrivacyPolicy(context = context, fLink = fLink, toolbarColor = R.color.color_green_bg, isNightMode = isNightMode)

    @JvmStatic
    fun openPrivacyPolicy(context: Context, fLink: String) =
        openPrivacyPolicy(context = context, fLink = fLink, toolbarColor = R.color.color_green_bg, isNightMode = false)

    @JvmStatic
    fun openAnyLink(context: Context, uri: String, @ColorRes toolbarColor: Int, isNightMode: Boolean = false) =
        openCustomTabs(context, uri, toolbarColor, isNightMode)

    @JvmStatic
    fun openAnyLink(context: Context, uri: String, @ColorRes toolbarColor: Int) =
        openAnyLink(context = context, uri = uri, toolbarColor = toolbarColor, isNightMode = false)

    @JvmStatic
    fun openAnyLink(context: Context, uri: String, isNightMode: Boolean) =
        openAnyLink(context = context, uri = uri, toolbarColor = R.color.color_green_bg, isNightMode = isNightMode)

    @JvmStatic
    fun openAnyLink(context: Context, uri: String) =
        openAnyLink(context = context, uri = uri, toolbarColor = R.color.color_green_bg, isNightMode = false)

    @JvmStatic
    fun shareThisApp(context: Activity, packageName: String, appName: String) {
        val lAppMsg = "Download this amazing " + appName + " app from play store, " +
                "Please search in play store or Click on the link given below to " +
                "download. "

        val lFinalMsg = String.format(lAppMsg, appName, appName)

        shareThisApp(
            context = context,
            packageName = packageName,
            appName = appName,
            appMessage = lFinalMsg
        )
    }

    @JvmStatic
    fun shareThisApp(context: Activity, packageName: String, appName: String, appMessage: String) {
//        AdMobAppOpenAdHelper.stopShowingAppOpenAdInternally()
        val lAppLink = "https://play.google.com/store/apps/details?id=${packageName}"

        ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setSubject(appName)
            .setText("\n\n$appMessage\n\n$lAppLink")
            .startChooser()
    }

    @JvmStatic
    fun sharePlainText(context: Activity, appName: String, text: String) {
//        AdMobAppOpenAdHelper.stopShowingAppOpenAdInternally()

        ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setSubject(appName)
            .setText(text)
            .startChooser()
    }
}
