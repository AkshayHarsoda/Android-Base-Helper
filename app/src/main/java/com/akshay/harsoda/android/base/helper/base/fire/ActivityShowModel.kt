package com.akshay.harsoda.android.base.helper.base.fire


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose


var mActivityShowModel: ActivityShowModel = ActivityShowModel()

@Keep
@Parcelize
data class ActivityShowModel(
    @SerializedName("update_message")
    @Expose
    val updateMessage: String = "",

    @SerializedName("version_name")
    @Expose
    val versionName: String = "",
) : Parcelable