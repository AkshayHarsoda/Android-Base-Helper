@file:Suppress("unused")

package com.akshay.harsoda.android.base.helper.base.utils


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class BackgroundTask<Params, Result>(
    private val onPreExecute: () -> Unit = {},
    private val doInBackground: (fParams : Array<out Params>) -> Result,
    private val onPostExecute: (fResult: Result) -> Unit,
    private val onCancelled: () -> Unit = {}
) : CoroutineScope {

    private val mTAG = javaClass.simpleName

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    fun cancel(isNeedToCallBack: Boolean = true) {
        job.cancel()
        if (isNeedToCallBack) {
            onCancelled.invoke()
        }
    }

    fun execute(vararg fParams: Params) = launch {
        job = Job()
        onPreExecute()
        val lResult: Result = doInBackground(fParams = fParams)
        onPostExecute(fResult = lResult)
    }


    private suspend fun onPreExecute() {
        withContext(Dispatchers.Main) {
            onPreExecute.invoke()
        }
    }

    private suspend fun doInBackground(vararg fParams: Params): Result = withContext(Dispatchers.IO) {
        return@withContext doInBackground.invoke(fParams)
    }

    private suspend fun onPostExecute(fResult: Result) {
        withContext(Dispatchers.Main) {
            onPostExecute.invoke(fResult)
        }
    }
}