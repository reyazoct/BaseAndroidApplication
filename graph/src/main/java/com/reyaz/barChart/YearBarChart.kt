package com.reyaz.barChart

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
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
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    disclaimerText: String? = null,
    @FloatRange(from = 0.0, to = 1.0) barWidthRatio: Float = 0.75F,
    @IntRange(from = 3, to = 9) valueCount: Int = 5,
    valueType: ValueType = ValueType.None,
    titleStyle: TextStyle = TextStyle.Default,
    tagStyle: TextStyle = TextStyle.Default,
    yearStyle: TextStyle = TextStyle.Default,
    valueStyle: TextStyle = TextStyle.Default,
    disclaimerStyle: TextStyle = TextStyle.Default,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
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
                    text = AnnotatedString(valueType.getDisplayTitle(title)),
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
                val step = (maxValue - minValue) / (valueCount - 1)
                var numberMaxWidth = Int.MIN_VALUE

                repeat(valueCount) { index ->
                    val numberMeasuredText = textMeasurer.measure(
                        text = AnnotatedString(valueType.getDisplayValue((minValue + (step * index)).toDisplayShortValue())),
                        maxLines = 1,
                        style = valueStyle,
                        softWrap = false,
                    )
                    drawText(
                        textLayoutResult = numberMeasuredText,
                        topLeft = Offset(
                            titleMeasuredText.size.height.toFloat() + 4.dp.value,
                            (((size.height - dummyYearMeasuredText.size.height - numberMeasuredText.size.height) / (valueCount - 1)) * (valueCount - index - 1))
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
            var heightTaken = 0F

            if (disclaimerText != null) {
                val disclaimerMeasuredText = textMeasurer.measure(
                    text = AnnotatedString(disclaimerText),
                    constraints = Constraints.fixedWidth((size.width - widthTaken).toInt()),
                    maxLines = 1,
                    style = disclaimerStyle.copy(textAlign = TextAlign.Center),
                )
                drawText(
                    textLayoutResult = disclaimerMeasuredText,
                    topLeft = Offset(
                        widthTaken,
                        size.height - disclaimerMeasuredText.size.height
                    ),
                )
                heightTaken = disclaimerMeasuredText.size.height.toFloat()
            }

            var yearHeight = 0F
            yearsEpoch.forEachIndexed { index, epoch ->
                val year = DateTime(epoch).year().get().toString()
                val yearMeasuredText = textMeasurer.measure(
                    text = AnnotatedString(year),
                    constraints = Constraints.fixedWidth(((size.width - widthTaken) / yearsEpoch.size).toInt()),
                    maxLines = 1,
                    style = yearStyle.copy(textAlign = TextAlign.Center),
                )

                drawText(
                    textLayoutResult = yearMeasuredText,
                    topLeft = Offset(
                        widthTaken + yearMeasuredText.size.width * index,
                        size.height - yearMeasuredText.size.height - heightTaken
                    ),
                )
                yearHeight = yearMeasuredText.size.height + 8.dp.value
            }
            heightTaken += yearHeight

            val division = (size.height - heightTaken) / (maxValue - minValue).absoluteValue

            val zeroPosition = if (maxValue.absoluteValue == minValue.absoluteValue) (size.height - heightTaken) / 2
            else if (minValue <= 0 && maxValue <= 0) 0F
            else size.height - heightTaken

            val numberOfBarGraphs = yearsEpoch.size * multiStockPointList.size
            val barWidth = (size.width - widthTaken) / (numberOfBarGraphs / barWidthRatio)
            val gapWidth = (size.width - widthTaken - (barWidth * numberOfBarGraphs)) / yearsEpoch.size
            multiStockPointList.fold(0F) { barMargin, multiStockPoint ->
                multiStockPoint.stockPointList.fold(gapWidth / 2) { totalGapTaken, stockPoint ->
                    val barHeight = (division * stockPoint.stockValue).toFloat()
                    val color = if (stockPoint.stockValue >= 0) multiStockPoint.positiveGraphColor else multiStockPoint.negativeGraphColor

                    val startGradient = color.copy(1F)
                    val endGradient = color.copy(0F)
                    val brush = if (stockPoint.stockValue >= 0) Brush.verticalGradient(listOf(startGradient, endGradient))
                    else Brush.verticalGradient(listOf(endGradient, startGradient))
                    val leftX = widthTaken + totalGapTaken + barMargin
                    val leftY = zeroPosition - barHeight
                    drawRect(
                        brush = brush,
                        topLeft = Offset(
                            leftX,
                            leftY,
                        ),
                        size = Size(
                            barWidth,
                            barHeight,
                        )
                    )

                    val valueMeasuredText = textMeasurer.measure(
                        text = AnnotatedString(valueType.getDisplayValue(stockPoint.stockValue.toDisplayShortValue())),
                        maxLines = 1,
                        style = valueStyle.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                        ),
                    )
                    if (valueMeasuredText.size.width < barWidth.absoluteValue) {
                        if (valueMeasuredText.size.height < barHeight.absoluteValue) {
                            drawText(
                                valueMeasuredText,
                                topLeft = Offset(
                                    leftX + (barWidth - valueMeasuredText.size.width) / 2,
                                    leftY + (barHeight - valueMeasuredText.size.height) / 2,
                                ),
                            )
                        } else {
                            drawText(
                                valueMeasuredText,
                                topLeft = Offset(
                                    leftX + (barWidth - valueMeasuredText.size.width) / 2,
                                    if (stockPoint.stockValue > 0) leftY - valueMeasuredText.size.height - 4.sp.value
                                    else leftY + 4.sp.value,
                                ),
                            )
                        }
                    }

                    totalGapTaken + gapWidth + (barWidth * multiStockPointList.size)
                }
                barMargin + barWidth
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
        val multiStockPointListWithTags = multiStockPointList.filter { it.tag != null }
        if (multiStockPointListWithTags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                multiStockPointListWithTags.forEach { multiStockPoint ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(multiStockPoint.positiveGraphColor, RoundedCornerShape(50)),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = multiStockPoint.tag.orEmpty(),
                        style = tagStyle,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
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
                    StockPoint(1692388804000, 2.0),
                    StockPoint(1660852804000, 0.5),
                    StockPoint(1629316804000, -2.0),
                    StockPoint(1597780804000, 1.0),
                    StockPoint(1566158404000, -5.0),
                    StockPoint(1534622404000, 4.0),
                    StockPoint(1503086404000, -0.5),
                    StockPoint(1471550404000, -4.0),
                    StockPoint(1439928004000, 1.0),
                ),
                positiveGraphColor = Color(0xFF2DC57B),
                negativeGraphColor = Color(0xFFE44848),
                null,
            ),
        ),
        valueType = ValueType.Currency.Rupees,
        title = "WR Score"
    )
}
