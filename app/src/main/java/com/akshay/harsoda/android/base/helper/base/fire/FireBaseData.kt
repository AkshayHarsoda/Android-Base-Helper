package com.akshay.harsoda.android.base.helper.base.fire

import android.app.Activity
import android.content.Context
import com.akshay.harsoda.android.base.helper.ads.utils.checkDeveloperOption
import com.akshay.harsoda.android.base.helper.ads.utils.getDeveloperSettingIntent
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.akshay.harsoda.android.base.helper.base.BaseActivity
import com.akshay.harsoda.android.base.helper.base.shared_prefs.getString
import com.akshay.harsoda.android.base.helper.base.shared_prefs.save

private const val ADS_KEY: String = "_ads_data"
private const val SCREEN_KEY: String = "_screen_data"
private const val IS_APP_AVAILABLE_ON_PLAY_STORE: String = "is_app_available_on_play_store"

const val isForTesting: Boolean = true
private const val isForDeveloperOptionTesting: Boolean = true
private const val isForForceUpdateTesting: Boolean = false

private fun Context.getTestAdsJson(): String {
    val lModel = Pair<String, String>("", "")

    return Gson().toJson(lModel)
}
private fun getTestScreenJson(): String {
    val lModel = ActivityShowModel(
        updateMessage = "Testing",
        versionName = "1.0",
    )

    return Gson().toJson(lModel)
}

/**
 * Call With your LauncherActivity
 */
fun Activity.setFirebaseData(onActionDone: () -> Unit) {

//    val mActivity: Activity = this@setFirebaseData

//    if (!isOnline) {
//        initSplashFirebaseData(
//            jsonString = mActivity.getString(fKey = SCREEN_KEY),
//            onActionDone = onActionDone
//        )
//    } else {
        setScreenData(onActionDone)
//    }
}

private fun Activity.setScreenData(onActionDone: () -> Unit) {
    val mActivity: Activity = this@setScreenData
    if (isForTesting) {
        initSplashFirebaseData(
            jsonString = getTestScreenJson(),
            onActionDone = onActionDone
        )
    } else {
        Firebase.remoteConfig.let {
            val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
            it.setConfigSettingsAsync(configSettings)
            it.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val jsonString: String = it.getString(SCREEN_KEY)
                        if (jsonString.isNotEmpty()) {
                            mActivity.save(SCREEN_KEY, jsonString)
                            initSplashFirebaseData(
                                jsonString = jsonString,
                                onActionDone = onActionDone
                            )
                        } else {
                            initSplashFirebaseData(
                                jsonString = mActivity.getString(fKey = SCREEN_KEY),
                                onActionDone = onActionDone
                            )
                        }
                    }
                }
        }
    }
}

private fun initSplashFirebaseData(jsonString: String, onActionDone: () -> Unit) {
    val activityShowModelType = object : TypeToken<ActivityShowModel>() {}.type
    mActivityShowModel = Gson().fromJson<ActivityShowModel>(jsonString, activityShowModelType) ?: ActivityShowModel()

    onActionDone.invoke()
}

fun BaseActivity.checkForDeveloperOption(onNext: () -> Unit) {
    checkDeveloperOption(
        fActivity = this,
        isTesting = isForDeveloperOptionTesting,
//        fButtonColor = this.getColorRes(R.color.color_accent),
        onOkayClick = { this.exitApplication() },
        onTurnOffClick = { this.launchActivity(fIntent = getDeveloperSettingIntent, isAdsShowing = false) },
        onNext = { onNext.invoke() },
    )
}