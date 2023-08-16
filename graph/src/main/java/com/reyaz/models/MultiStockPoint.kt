package com.reyaz.models

import androidx.compose.ui.graphics.Color

data class MultiStockPoint(
    val stockPointList: List<StockPoint>,
    val positiveGraphColor: Color,
    val negativeGraphColor: Color,
    val tag: String?,
)