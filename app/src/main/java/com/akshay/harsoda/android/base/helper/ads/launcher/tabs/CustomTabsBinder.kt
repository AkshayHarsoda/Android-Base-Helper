package com.akshay.harsoda.android.base.helper.ads.launcher.tabs

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class CustomTabsBinder : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        CustomTabsHelper.bind()
    }

    override fun onStop(owner: LifecycleOwner) {
        CustomTabsHelper.unbind()
    }
}
