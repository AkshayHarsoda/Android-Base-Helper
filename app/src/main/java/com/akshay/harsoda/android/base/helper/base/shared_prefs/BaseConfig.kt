package com.akshay.harsoda.android.base.helper.base.shared_prefs

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


//const val KEY_USER_COIN: String = "key_user_coin"
//const val KEY_USER_ENERGY: String = "key_user_energy"
const val KEY_LAST_INCOMPLETE_LEVEL: String = "KEY_LAST_INCOMPLETE_LEVEL"
const val KEY_EXTRA_WORDS = "KEY_EXTRA_WORDS"
const val KEY_EXTRA_WORDS_COUNT = "KEY_EXTRA_WORDS_COUNT"

open class BaseConfig(val context: Context) {

//    var `PARAMETER_NAME`: Boolean
//        get() = context.getBoolean("YOUR_PREF_KEY")
//        set(fValue) = context.save("YOUR_PREF_KEY", fValue)

//    var isOpenFirstTime: Boolean
//        get() = context.getBoolean("open_first_time", false)
//        set(fValue) = context.save("open_first_time", fValue)

//    var userCoin: Int
//        get() = context.getInt(fKey = KEY_USER_COIN, fDefaultValue = 0)
//        set(value) = context.save(fKey = KEY_USER_COIN, fValue = value)
//
//    var userEnergy: Int
//        get() = context.getInt(fKey = KEY_USER_ENERGY, fDefaultValue = 6)
//        set(value) = context.save(fKey = KEY_USER_ENERGY, fValue = value)
//
    var lastIncompleteLevel: Int
        get() = context.getInt(fKey = KEY_LAST_INCOMPLETE_LEVEL, fDefaultValue = 0)
        set(value) = context.save(fKey = KEY_LAST_INCOMPLETE_LEVEL, fValue = value)

    var extraWordsCount: Int
        get() = context.getInt(fKey = KEY_EXTRA_WORDS_COUNT, fDefaultValue = 0)
        set(value) = context.save(fKey = KEY_EXTRA_WORDS_COUNT, fValue = value)

    var listOfExtraWord: ArrayList<Int>
        get() {
            val lJson: String = context.getString(fKey = KEY_EXTRA_WORDS)
            val lType = object : TypeToken<ArrayList<Int>>() {}.type
            return Gson().fromJson(lJson, lType) ?: ArrayList()
        }
        set(fValue) = context.save(fKey = KEY_EXTRA_WORDS, fValue = Gson().toJson(fValue))
}
