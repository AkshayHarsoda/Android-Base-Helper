package com.akshay.harsoda.android.base.helper.ads.launcher.tabs

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

object OpenUriUtils {
    private var defaultBrowserPackage: String? = null
    private var browserPackages: Set<String>? = null

    fun getBrowserPackages(context: Context): Set<String> {
        browserPackages?.let {
            return it
        }
        return getBrowserPackagesInner(context).also {
            browserPackages = it
        }
    }

    @Suppress("DEPRECATION")
    private fun getBrowserPackagesInner(context: Context): Set<String> {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PackageManager.MATCH_ALL else 0
        return context.packageManager
            .queryIntentActivities(makeBrowserTestIntent(), flags)
            .mapNotNull { it.activityInfo?.packageName }
            .toSet()
    }

    fun getDefaultBrowserPackage(context: Context): String? {
        defaultBrowserPackage?.let {
            return it
        }
        return getDefaultBrowserPackageInner(context)?.also {
            defaultBrowserPackage = it
        }
    }

    @Suppress("DEPRECATION")
    private fun getDefaultBrowserPackageInner(context: Context): String? {
        val packageName = context.packageManager
            .resolveActivity(makeBrowserTestIntent(), 0)
            ?.activityInfo
            ?.packageName
            ?: return null
        return if (getBrowserPackages(context).contains(packageName)) {
            packageName
        } else null
    }

    private fun makeBrowseIntent(uri: String): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse(uri)).also {
            it.addCategory(Intent.CATEGORY_BROWSABLE)
        }

    private fun makeBrowserTestIntent(): Intent =
        makeBrowseIntent("http://www.example.com/")
}
