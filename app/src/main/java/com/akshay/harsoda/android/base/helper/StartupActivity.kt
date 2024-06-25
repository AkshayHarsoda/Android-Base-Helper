package com.akshay.harsoda.android.base.helper

import android.view.View
import com.akshay.harsoda.android.base.helper.ads.utils.AppTimer
import com.akshay.harsoda.android.base.helper.base.BaseActivity
import com.akshay.harsoda.android.base.helper.base.BaseBindingActivity
import com.akshay.harsoda.android.base.helper.base.fire.checkForDeveloperOption
import com.akshay.harsoda.android.base.helper.base.fire.setFirebaseData
import com.akshay.harsoda.android.base.helper.databinding.ActivityStartupBinding


class StartupActivity : BaseBindingActivity<ActivityStartupBinding>() {

    private var isLaunchNext: Boolean = false
    private var isNeedToStartTime: Boolean = true

    override fun getActivityContext(): BaseActivity {
        return this@StartupActivity
    }

    override fun setBinding(): ActivityStartupBinding {
        return ActivityStartupBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        if (isOnPause) {
            isOnPause = false

            if (isNeedToStartTime) {
                mTimer?.cancelTimer()
                mTimer = null

                if (!isLaunchNext) {
                    getDataFromFirebase()
                } else {
                    startNextTimer()
                }
            }
        }
        super.onResume()
    }

    override fun onNoInternetDialogShow() {
        super.onNoInternetDialogShow()
        mTimer?.cancelTimer()
        mTimer = null
    }

    override fun onNoInternetDialogDismiss() {
        super.onNoInternetDialogDismiss()
        initView()
    }

    @Suppress("DEPRECATION")
    override fun initView() {
        super.initView()
//        mBinding.appName.text = mActivity.getStringRes(R.string.html_app_name).getFromHtml

        isLaunchNext = false
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        getDataFromFirebase()
    }

    private fun getDataFromFirebase() {
        mActivity.checkForDeveloperOption {
            mActivity.setFirebaseData {
//                SplashAd.loadSplashAd(fContext = mActivity)
                        startTimer(millisInFuture = 2000, countDownInterval = 1000)
            }
        }
    }



    private fun startTimer(millisInFuture: Long, countDownInterval: Long) {
        mTimer?.cancelTimer()
        mTimer = AppTimer(
            millisInFuture = millisInFuture,
            countDownInterval = countDownInterval,
            onTick = {},
            onFinish = {
                if (isLaunchNext) {
                    goToNextScreen()
                } else {
                    afterTimerFinish()
                }
            }
        )

        mTimer?.start()
    }

    private fun afterTimerFinish() {
        mTimer?.cancelTimer()

        if (!isOnPause) {
            mTimer = null

//            mActivity.checkForceUpdate {
                launchScreenWithAd()
//            }
        }
    }

    private fun launchScreenWithAd() {
        isLaunchNext = true
//        mActivity.showSplashAd {
            goToNextScreen()
//        }
    }

    private fun startNextTimer() {
        startTimer(millisInFuture = 500, countDownInterval = 100)
    }

    private fun goToNextScreen() {
        launchActivity(fIntent = getActivityIntent<MainActivity>(), isAdsShowing = false, isNeedToFinish = true)
    }
    //</editor-fold>

    override fun onPause() {
        super.onPause()
        isOnPause = true
        mTimer?.cancelTimer()
    }

    override fun customOnBackPressed() {

    }
}