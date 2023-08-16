package com.reyaz.util

import android.icu.text.CompactDecimalFormat
import android.os.Build
import androidx.annotation.IntRange
import java.text.DecimalFormat
import java.util.Locale

fun Double?.toDisplayShortValue(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val formatter = CompactDecimalFormat.getInstance(Locale("en", "IN"), CompactDecimalFormat.CompactStyle.SHORT)
        formatter.format(this ?: 0)
    } else {
        toDisplayValue()
    }
}

fun Double?.toDisplayValue(@IntRange(from = 0, to = 6) maxDecimalPoints: Int = 0, @IntRange(from = 0, to = 6) minDecimalPoints: Int = 0): String {
    val double = this ?: 0.0
    val numberFormat = DecimalFormat.getInstance()
    numberFormat.maximumFractionDigits = maxDecimalPoints
    numberFormat.minimumFractionDigits = minDecimalPoints
    return numberFormat.format(double)
}
