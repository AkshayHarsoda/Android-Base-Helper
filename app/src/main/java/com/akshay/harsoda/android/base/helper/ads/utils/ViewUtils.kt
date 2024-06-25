package com.akshay.harsoda.android.base.helper.ads.utils

internal fun getCamelCaseString(text: String): String {

    val words: Array<String> = text.split(" ").toTypedArray()

    val builder = StringBuilder()
    for (i in words.indices) {
        var word: String = words[i]
        word = if (word.isEmpty()) word else Character.toUpperCase(word[0])
            .toString() + word.substring(1).lowercase()
        builder.append(word)
        if (i != (words.size - 1)) {
            builder.append(" ")
        }
    }
    return builder.toString()
}