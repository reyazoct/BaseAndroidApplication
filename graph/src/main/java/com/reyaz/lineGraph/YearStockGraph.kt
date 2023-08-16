package com.reyaz.lineGraph

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.reyaz.models.MultiStockPoint
import com.reyaz.models.StockPoint
import com.reyaz.util.toDisplayShortValue
import org.joda.time.DateTime

@Composable
fun YearStockGraph(
    modifier: Modifier = Modifier,
    multiStockPointList: List<MultiStockPoint>,
    title: String? = null,
    titleStyle: TextStyle = TextStyle.Default,
    tagStyle: TextStyle = TextStyle.Default,
    yearStyle: TextStyle = TextStyle.Default,
    valueStyle: TextStyle = TextStyle.Default,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (multiStockPointList.isEmpty()) return
        val flattenMap = multiStockPointList.asSequence().map { it.stockPointList }.flatten()

        if (flattenMap.toList().size <= 1) return

        val yearsEpoch = flattenMap.mapNotNull { it.epochTimestamp }.distinct().sorted().toList()
        val multiStockValues = flattenMap.mapNotNull { it.stockValue }

        val maxHeight = multiStockValues.max()
        val minHeight = multiStockValues.min()

        val textMeasurer = rememberTextMeasurer()
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
        ) {
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
                val step = (maxHeight - minHeight) / (counts - 1)
                var numberMaxWidth = Int.MIN_VALUE

                repeat(counts) { index ->
                    val numberMeasuredText = textMeasurer.measure(
                        text = AnnotatedString((minHeight + (step * index)).toDisplayShortValue()),
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
            var heightTaken = 0F
            yearsEpoch.forEachIndexed { index, epoch ->
                val year = DateTime(epoch).year().get().toString()
                val yearMeasuredText = textMeasurer.measure(
                    text = AnnotatedString(year),
                    maxLines = 1,
                    style = yearStyle,
                    softWrap = false,
                )

                val x =
                    (((size.width - widthTaken + yearMeasuredText.size.width) / (yearsEpoch.size - 1)) * index) + widthTaken - ((yearMeasuredText.size.width / (yearsEpoch.size - 1)) * index) - yearMeasuredText.size.width / 2
                drawText(
                    textLayoutResult = yearMeasuredText,
                    topLeft = Offset(
                        x,
                        size.height - yearMeasuredText.size.height
                    ),
                )
                heightTaken = yearMeasuredText.size.height + 8.dp.value
            }

            val strokeWidth = 4.dp.toPx()
            val width = size.width - strokeWidth - widthTaken
            val height = size.height - strokeWidth - heightTaken
            multiStockPointList.forEach { multiStockPoint ->
                val stockPointList = multiStockPoint.stockPointList
                val stockValues = stockPointList.mapNotNull { it.stockValue }

                val division = height / (maxHeight - minHeight)

                val offsetList = mutableListOf<Pair<Color, List<Offset>>>()
                var offsetListTillMid = mutableListOf<Offset>()
                stockValues.forEachIndexed { index, currentStockValue ->
                    val oldStockValue = stockValues.getOrNull(index - 1)

                    val currentValue = currentStockValue - minHeight
                    val currentX = ((width / (stockPointList.size - 1)) * index) + widthTaken
                    val currentY = currentValue * division

                    if (oldStockValue == null || currentStockValue >= 0.0 && oldStockValue >= 0.0 || currentStockValue < 0.0 && oldStockValue < 0.0) {
                        offsetListTillMid.add(Offset(currentX + strokeWidth / 2, (height - currentY).toFloat() + strokeWidth / 2))
                    } else {
                        val zeroValue = 0.0 - minHeight

                        val oldValue = oldStockValue - minHeight
                        val oldX = ((width / (stockPointList.size - 1)) * (index - 1)) + widthTaken
                        val oldY = oldValue * division

                        val slope = (currentY - oldY) / (currentX - oldX)
                        val yIntercept = oldY - slope * oldX

                        val zeroY = zeroValue * division
                        val zeroX = (zeroY - yIntercept) / slope

                        offsetListTillMid.add(Offset((zeroX + strokeWidth / 2).toFloat(), (height - zeroY).toFloat() + strokeWidth / 2))
                        val color = if (oldStockValue >= 0.0) multiStockPoint.positiveGraphColor else multiStockPoint.negativeGraphColor
                        offsetList.add(Pair(color, offsetListTillMid))

                        offsetListTillMid = mutableListOf()
                        offsetListTillMid.add(Offset((zeroX + strokeWidth / 2).toFloat(), (height - zeroY).toFloat() + strokeWidth / 2))
                        offsetListTillMid.add(Offset(currentX + strokeWidth / 2, (height - currentY).toFloat() + strokeWidth / 2))
                    }
                }
                val color = if (stockValues.last() >= 0.0) multiStockPoint.positiveGraphColor else multiStockPoint.negativeGraphColor
                offsetList.add(Pair(color, offsetListTillMid))

                offsetList.forEachIndexed { olIndex, (color, currentOffsetList) ->
                    val path = Path().apply {
                        repeat(currentOffsetList.size) { index ->
                            val currentOffset = currentOffsetList[index]
                            if (index == 0) {
                                moveTo(currentOffset.x, currentOffset.y)
                            } else {
                                val previousOffset = currentOffsetList[index - 1]
                                val conX1 = when {
                                    olIndex != 0 && index == 1 -> {
                                        previousOffset.x
                                    }

                                    olIndex != offsetList.lastIndex && index == currentOffsetList.lastIndex -> {
                                        currentOffset.x
                                    }

                                    else -> {
                                        (previousOffset.x + currentOffset.x) / 2f
                                    }
                                }
                                val conX2 = conX1.plus(0)

                                val conY1 = previousOffset.y
                                val conY2 = currentOffset.y

                                cubicTo(
                                    x1 = conX1,
                                    y1 = conY1,
                                    x2 = conX2,
                                    y2 = conY2,
                                    x3 = currentOffset.x,
                                    y3 = currentOffset.y,
                                )
                            }
                        }
                    }
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Butt,
                        ),
                    )

                    val newPath = Path().apply {
                        val zeroY = (height + minHeight.times(division)).toFloat() + strokeWidth / 2
                        if (minHeight < 0 && maxHeight > 0) {
                            moveTo(currentOffsetList.first().x, zeroY)
                        } else {
                            moveTo(currentOffsetList.first().x, height)
                        }

                        repeat(currentOffsetList.size) { index ->
                            val currentOffset = currentOffsetList[index]
                            if (index == 0) {
                                lineTo(currentOffset.x, currentOffset.y)
                            } else {
                                val previousOffset = currentOffsetList[index - 1]
                                val conX1 = when {
                                    olIndex != 0 && index == 1 -> {
                                        previousOffset.x
                                    }

                                    olIndex != offsetList.lastIndex && index == currentOffsetList.lastIndex -> {
                                        currentOffset.x
                                    }

                                    else -> {
                                        (previousOffset.x + currentOffset.x) / 2f
                                    }
                                }
                                val conX2 = conX1.plus(0)

                                val conY1 = previousOffset.y
                                val conY2 = currentOffset.y

                                cubicTo(
                                    x1 = conX1,
                                    y1 = conY1,
                                    x2 = conX2,
                                    y2 = conY2,
                                    x3 = currentOffset.x,
                                    y3 = currentOffset.y,
                                )
                            }
                        }
                        if (minHeight < 0 && maxHeight > 0) {
                            lineTo(currentOffsetList.last().x, zeroY)
                        } else {
                            lineTo(currentOffsetList.last().x, height)
                        }
                    }
                    val startGradient = color.copy(0.32F)
                    val endGradient = color.copy(0.00F)
                    val brush = Brush.verticalGradient(listOf(startGradient, endGradient))
                    drawPath(
                        path = newPath,
                        brush = brush,
                    )
                }
                if (minHeight < 0 && maxHeight > 0) {
                    val zeroY = (height + minHeight.times(division)).toFloat() + strokeWidth / 2
                    drawLine(
                        strokeWidth = strokeWidth / 2,
                        cap = StrokeCap.Round,
                        color = Color(0x33F2F2F2),
                        start = Offset(widthTaken, zeroY),
                        end = Offset(width + widthTaken, zeroY),
                    )
                }
            }
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            repeat(4) { index ->
                drawLine(
                    color = Color(0x33F2F2F2),
                    start = Offset(widthTaken, height / 3 * index),
                    end = Offset(width, height / 3 * index),
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
    YearStockGraph(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2F),
        multiStockPointList = listOf(
            MultiStockPoint(
                stockPointList = listOf(
                    StockPoint(1626312377000, 2.0),
                    StockPoint(1626312377000, 0.0),
                    StockPoint(1626312377000, -2.0),
                ),
                positiveGraphColor = Color(0xFF2DC57B),
                negativeGraphColor = Color(0xFFE44848),
                null,
            )
        ),
        title = "WR Score"
    )
}
