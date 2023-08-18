package com.reyaz.barChart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.reyaz.models.MultiStockPoint
import com.reyaz.models.StockPoint
import com.reyaz.util.toDisplayShortValue
import org.joda.time.DateTime
import kotlin.math.absoluteValue

@Composable
fun YearBarChart(
    modifier: Modifier = Modifier,
    multiStockPointList: List<MultiStockPoint>,
    title: String? = null,
    barWidthDp: Dp = 32.dp,
    titleStyle: TextStyle = TextStyle.Default,
    yearStyle: TextStyle = TextStyle.Default,
    valueStyle: TextStyle = TextStyle.Default,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (multiStockPointList.isEmpty()) return
        val flattenMap = multiStockPointList.asSequence().map { it.stockPointList }.flatten()

        if (flattenMap.toList().size <= 1) return

        val yearsEpoch = flattenMap.map { it.epochTimestamp }.distinct().sorted().toList()
        val multiStockValues = flattenMap.map { it.stockValue }

        val maxHeight = multiStockValues.max()
        val minHeight = multiStockValues.min()

        val textMeasurer = rememberTextMeasurer()
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
        ) {
            val (maxValue, minValue) = if (maxHeight >= 0.0 && minHeight >= 0.0) {
                Pair(maxHeight, 0.0)
            } else if (maxHeight <= 0.0 && minHeight <= 0) {
                Pair(0.0, minHeight)
            } else {
                if (maxHeight.absoluteValue > minHeight.absoluteValue) {
                    Pair(maxHeight, -maxHeight)
                } else {
                    Pair(-minHeight, minHeight)
                }
            }

            val widthTaken = if (title != null) {
                val year = DateTime(yearsEpoch.first()).year().get().toString()
                val dummyYearMeasuredText = textMeasurer.measure(
                    text = AnnotatedString(year),
                    maxLines = 1,
                    style = yearStyle,
                    softWrap = false,
                )

                val titleHeight = size.height - dummyYearMeasuredText.size.height
                val titleMeasuredText = textMeasurer.measure(
                    text = AnnotatedString(title),
                    maxLines = 1,
                    style = titleStyle.copy(textAlign = TextAlign.Center),
                    constraints = Constraints.fixedWidth(titleHeight.toInt())
                )
                rotate(
                    degrees = 270F,
                    pivot = Offset(
                        0F,
                        titleHeight,
                    )
                ) {
                    drawText(
                        textLayoutResult = titleMeasuredText,
                        topLeft = Offset(
                            0F,
                            titleHeight,
                        ),
                    )
                }
                val counts = 6
                val step = (maxValue - minValue) / (counts - 1)
                var numberMaxWidth = Int.MIN_VALUE

                repeat(counts) { index ->
                    val numberMeasuredText = textMeasurer.measure(
                        text = AnnotatedString((minValue + (step * index)).toDisplayShortValue()),
                        maxLines = 1,
                        style = valueStyle,
                        softWrap = false,
                    )
                    drawText(
                        textLayoutResult = numberMeasuredText,
                        topLeft = Offset(
                            titleMeasuredText.size.height.toFloat() + 4.dp.value,
                            (((size.height - dummyYearMeasuredText.size.height - numberMeasuredText.size.height) / (counts - 1)) * (counts - index - 1))
                        ),
                    )
                    if (numberMaxWidth < numberMeasuredText.size.width) {
                        numberMaxWidth = numberMeasuredText.size.width
                    }
                }

                titleMeasuredText.size.height + numberMaxWidth + 12.dp.value
            } else {
                0F
            }
            var heightTaken = 50F
            yearsEpoch.forEachIndexed { index, epoch ->
                val year = DateTime(epoch).year().get().toString()
                val yearMeasuredText = textMeasurer.measure(
                    text = AnnotatedString(year),
                    maxLines = 1,
                    style = yearStyle,
                    softWrap = false,
                )

                val x = (((size.width - widthTaken + yearMeasuredText.size.width) / (yearsEpoch.size - 1)) * index) + widthTaken - ((yearMeasuredText.size.width / (yearsEpoch.size - 1)) * index) - yearMeasuredText.size.width / 2
                drawText(
                    textLayoutResult = yearMeasuredText,
                    topLeft = Offset(
                        x,
                        size.height - yearMeasuredText.size.height
                    ),
                )
                heightTaken = yearMeasuredText.size.height + 8.dp.value
            }
            val division = (size.height - heightTaken) / (maxValue - minValue).absoluteValue

            val zeroPosition = if (maxValue.absoluteValue == minValue.absoluteValue) (size.height - heightTaken) / 2
            else if (minValue <= 0 && maxValue <= 0) 0F
            else size.height - heightTaken

            val barWidth = barWidthDp.value
            multiStockPointList.forEach { multiStockPoint ->
                val stockPointListSize = multiStockPoint.stockPointList.size
                val gapWidth = (size.width - widthTaken - (barWidth * stockPointListSize)) / (stockPointListSize)
                multiStockPoint.stockPointList.fold(gapWidth / 2) { totalGapTaken, stockPoint ->
                    val barHeight = division * stockPoint.stockValue
                    val color = if (stockPoint.stockValue >= 0) multiStockPoint.positiveGraphColor else multiStockPoint.negativeGraphColor
                    drawRect(
                        color = color,
                        topLeft = Offset(
                            widthTaken + totalGapTaken,
                            (zeroPosition - barHeight).toFloat(),
                        ),
                        size = Size(
                            barWidth,
                            barHeight.toFloat(),
                        )
                    )
                    totalGapTaken + gapWidth
                }
            }
            drawLine(
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
                color = Color(0x33F2F2F2),
                start = Offset(widthTaken, zeroPosition),
                end = Offset(size.width, zeroPosition),
            )
            val width = size.width - widthTaken
            val height = size.height - heightTaken
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10F, 10F), 0F)
            repeat(4) { index ->
                drawLine(
                    color = Color(0x33F2F2F2),
                    start = Offset(widthTaken, height / 3 * index),
                    end = Offset(width + widthTaken, height / 3 * index),
                    pathEffect = pathEffect,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Demo() {
    YearBarChart(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2F),
        multiStockPointList = listOf(
            MultiStockPoint(
                stockPointList = listOf(
                    StockPoint(1626312377000, 2.0),
                    StockPoint(1626312377000, 3.0),
                    StockPoint(1626312377000, -1.0),
                ),
                positiveGraphColor = Color(0xFF2DC57B),
                negativeGraphColor = Color(0xFFE44848),
                null,
            )
        ),
        title = "WR Score"
    )
}
