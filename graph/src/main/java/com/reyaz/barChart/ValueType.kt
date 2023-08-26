package com.reyaz.barChart

sealed class ValueType {
    object None : ValueType()
    object Percentage : ValueType()
    sealed class Currency : ValueType() {
        object Rupees : Currency()
        object Dollar : Currency()
    }

    fun getDisplayTitle(title: String): String {
        return when (this) {
            None -> title
            Percentage -> "$title (%)"
            Currency.Rupees -> "$title (₹)"
            Currency.Dollar -> "$title ($)"
        }
    }

    fun getDisplayValue(value: String): String {
        return when (this) {
            None -> value
            Percentage -> "$value%"
            Currency.Rupees -> "₹$value"
            Currency.Dollar -> "$$value"
        }
    }
}