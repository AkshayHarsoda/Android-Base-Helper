package com.akshay.harsoda.android.base.helper.ads.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData


/**
 * Return true if internet or wi-fi connection is working fine
 * <p>
 * Required permission
 * <uses-permission android:name="android.permission.INTERNET"/>
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
 *
 * @return true if you have the internet connection, or false if not.
 */
inline val isOnline: Boolean
    get() {
        return isInternetAvailable.value == true
    }
val isInternetAvailable: MutableLiveData<Boolean> = MutableLiveData()

private fun getUIThread(runOnUIThread: () -> Unit) {
    Handler(Looper.getMainLooper()).post {
        runOnUIThread.invoke()
    }
}

//<editor-fold desc="Network Related">
private val networkRequest = NetworkRequest.Builder()
    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    .build()

private fun networkCallback(fContext: Context) = object : ConnectivityManager.NetworkCallback() {
    // network is available for use
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        getUIThread {
            isInternetAvailable.value = true
        }
    }

    // lost network connection
    override fun onLost(network: Network) {
        super.onLost(network)
        getUIThread {
            isInternetAvailable.value = false
        }
    }

    override fun onUnavailable() {
        super.onUnavailable()
        getUIThread {
            isInternetAvailable.value = false
        }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        val isValidated: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            fContext.isOnlineApp
        }

        getUIThread {
            isInternetAvailable.value = isValidated
        }
    }
}

@Suppress("DEPRECATION")
private val Context.isOnlineApp: Boolean
    get() {
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).let { connectivityManager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.let {
                    return it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
            } else {
                try {
                    connectivityManager.activeNetworkInfo?.let {
                        if (it.isConnected && it.isAvailable) {
                            return true
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
        return false
    }
//</editor-fold>

fun initNetwork(fContext: Application) {
    isInternetAvailable.value = fContext.isOnlineApp

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val connectivityManager = fContext.getSystemService(ConnectivityManager::class.java)
        connectivityManager.requestNetwork(networkRequest, networkCallback(fContext))
    }
}