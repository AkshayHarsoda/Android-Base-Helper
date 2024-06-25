@file:Suppress("unused")

package com.akshay.harsoda.android.base.helper.ads.utils

import android.os.CountDownTimer

class AppTimer(
    millisInFuture: Long,
    countDownInterval: Long,
    private val onTick:(countDownTime: Long) -> Unit = {},
    private val onFinish:() -> Unit = {},
) : CountDownTimer(millisInFuture, countDownInterval) {

    var isRunning: Boolean = false

    override fun onTick(millisUntilFinished: Long) {
        val lCountDownTime = (millisUntilFinished / 1000)
        val formattedSeconds = String.format("%02d", lCountDownTime)
        logE(tag = "AppTimer", message = "onTick: Formatted Time Number is $formattedSeconds")
        isRunning = true
        onTick.invoke(lCountDownTime)
    }

    override fun onFinish() {
        isRunning = false
        onFinish.invoke()
    }

    fun cancelTimer() {
        isRunning = false
        this.cancel()
    }
}