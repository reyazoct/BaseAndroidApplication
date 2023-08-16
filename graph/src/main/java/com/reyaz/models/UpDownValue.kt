package com.reyaz.models

import androidx.compose.ui.graphics.Color
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.reyaz.util.toDisplayAmount
import com.reyaz.util.toDisplayPercentage
import kotlin.math.absoluteValue

sealed class UpDownValue(private val value: Double) {
    class Up(value: Double) : UpDownValue(value)
    class Down(value: Double) : UpDownValue(value)
    object Static : UpDownValue(0.0)

    fun getColorCode(): Color {
        return when (this) {
            is Down -> Color(0xFFE96D6D)
            is Up -> Color(0xFF0AC167)
            Static -> Color(0xFFFFFFFF)
        }
    }

    fun toDisplayAmount(sign: Boolean = false): String {
        return if (sign) {
            value.toDisplayAmount(true)
        } else {
            value.absoluteValue.toDisplayAmount(false)
        }
    }

    fun toDisplayPercentage(sign: Boolean = false): String {
        return if (sign) {
            value.toDisplayPercentage(true, 2)
        } else {
            value.absoluteValue.toDisplayPercentage(false, 2)
        }
    }

    val amount: Double
        get() = value

    class JsonAdapter : TypeAdapter<UpDownValue>() {
        override fun write(writer: JsonWriter?, value: UpDownValue?) {}

        override fun read(reader: JsonReader?): UpDownValue? {
            val doubleValue = reader?.nextDouble() ?: return null
            return getUpDownValue(doubleValue)
        }
    }

    companion object {
        fun getUpDownValue(doubleValue: Double): UpDownValue {
            return if (doubleValue > 0) {
                Up(doubleValue)
            } else if (doubleValue < 0) {
                Down(doubleValue)
            } else {
                Static
            }
        }
    }
}
