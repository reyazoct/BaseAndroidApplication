package com.reyaz.util

import android.icu.text.CompactDecimalFormat
import android.os.Build
import androidx.annotation.IntRange
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun Double?.toDisplayShortValue(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val formatter = CompactDecimalFormat.getInstance(Locale("en", "IN"), CompactDecimalFormat.CompactStyle.SHORT)
        formatter.format(this ?: 0)
    } else {
        toDisplayValue()
    }
}

fun Double?.toDisplayAmount(sign: Boolean = false): String {
    val value = this ?: 0.0
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    numberFormat.maximumFractionDigits = if (value % 1 == 0.0) 0 else 2
    val amountInString = numberFormat.format(value)
    return if (value > 0 && sign) {
        "+".plus(amountInString)
    } else {
        amountInString
    }
}


fun Double?.toDisplayValue(@IntRange(from = 0, to = 6) maxDecimalPoints: Int = 0, @IntRange(from = 0, to = 6) minDecimalPoints: Int = 0): String {
    val double = this ?: 0.0
    val numberFormat = DecimalFormat.getInstance()
    numberFormat.maximumFractionDigits = maxDecimalPoints
    numberFormat.minimumFractionDigits = minDecimalPoints
    return numberFormat.format(double)
}

fun Double?.toDisplayShortPercentage(sign: Boolean = false, @IntRange(from = 0, to = 2) decimalPoints: Int = 0): String {
    return toDisplayShortValue().plus("%")
}

fun Double?.toDisplayPercentage(sign: Boolean = false, @IntRange(from = 0, to = 2) decimalPoints: Int = 0): String {
    val double = this ?: 0.0
    val numberFormat = DecimalFormat.getInstance()
    numberFormat.maximumFractionDigits = decimalPoints
    val percentageInString = toDisplayValue()
    return if (double > 0 && sign) {
        "+".plus(percentageInString)
    } else {
        percentageInString
    }
}

