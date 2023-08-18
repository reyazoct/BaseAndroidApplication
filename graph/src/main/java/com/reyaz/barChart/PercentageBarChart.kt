package com.reyaz.barChart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.reyaz.models.BarChartInfo
import com.reyaz.util.toDisplayShortPercentage
import com.reyaz.models.UpDownValue
import kotlin.math.absoluteValue

@Composable
fun PercentageBarChart(
    modifier: Modifier = Modifier,
    data: List<BarChartInfo>,
    percentageTextStyle: TextStyle = TextStyle.Default,
    typeTextStyle: TextStyle = TextStyle.Default,
) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        val height = size.height
        val width = size.width
        val maxPercentage = if (data.maxOf { it.percentage } < 0) 0.0 else data.maxOf { it.percentage }
        val minPercentage = if (data.minOf { it.percentage } > 0) 0.0 else data.minOf { it.percentage }
        val offsetOnDp = 12.dp.value
        data.forEachIndexed { index, barChartInfo ->

            val percentageMeasuredText = textMeasurer.measure(
                AnnotatedString(barChartInfo.percentage.toDisplayShortPercentage()),
                constraints = Constraints.fixedWidth(
                    width = ((size.width / data.size) - (offsetOnDp * 2)).toInt(),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = percentageTextStyle.copy(textAlign = TextAlign.Center),
            )

            val typeMeasuredText = textMeasurer.measure(
                AnnotatedString(barChartInfo.title),
                constraints = Constraints.fixedWidth(
                    width = ((size.width / data.size) - (offsetOnDp * 2)).toInt(),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = typeTextStyle.copy(textAlign = TextAlign.Center),
            )
            val topOffset = if (maxPercentage > 0) percentageMeasuredText.size.height + typeMeasuredText.size.height else 0
            val bottomOffset = if (minPercentage < 0) percentageMeasuredText.size.height + typeMeasuredText.size.height else 0
            val totalGraphHeight = height - topOffset - bottomOffset
            val division = (height - (bottomOffset + topOffset)) / (maxPercentage - minPercentage)

            val yOffsetForGraph = if (barChartInfo.percentage > 0) {
                totalGraphHeight - (division * (barChartInfo.percentage.absoluteValue - minPercentage))
            } else {
                if (barChartInfo.percentage < 0) {
                    division * maxPercentage
                } else {
                    null
                } ?: return@forEachIndexed
            }
            val heightForGraph = division * barChartInfo.percentage.absoluteValue

            val upDownValue = UpDownValue.getUpDownValue(barChartInfo.percentage)
            if (barChartInfo.percentage > 0) {
                drawText(
                    percentageMeasuredText,
                    color = upDownValue.getColorCode(),
                    topLeft = Offset(width / data.size * index, yOffsetForGraph.toFloat()),
                )
                drawText(
                    typeMeasuredText,
                    color = Color(0xFFFFFFFF),
                    topLeft = Offset(
                        width / data.size * index,
                        (yOffsetForGraph + typeMeasuredText.size.height).toFloat(),
                    ),
                )
            }
            if (barChartInfo.percentage < 0) {
                drawText(
                    percentageMeasuredText,
                    color = upDownValue.getColorCode(),
                    topLeft = Offset(
                        width / data.size * index,
                        (typeMeasuredText.size.height + topOffset + yOffsetForGraph + heightForGraph).toFloat(),
                    ),
                )
                drawText(
                    typeMeasuredText,
                    color = Color(0xFFFFFFFF),
                    topLeft = Offset(
                        width / data.size * index,
                        (topOffset + yOffsetForGraph + heightForGraph).toFloat(),
                    ),
                )
            }

            drawRect(
                color = barChartInfo.color,
                topLeft = Offset(
                    width / data.size * index,
                    (topOffset + yOffsetForGraph).toFloat(),
                ),
                size = Size(
                    width / data.size,
                    heightForGraph.toFloat(),
                ),
            )
            val strokeWidth = 4.dp.value
            drawLine(
                color = Color(0xFFFFFFFF),
                start = Offset(
                    width / data.size * index,
                    (if (barChartInfo.percentage > 0) topOffset + yOffsetForGraph + heightForGraph else topOffset + yOffsetForGraph).toFloat(),
                ),
                strokeWidth = strokeWidth,
                end = Offset(
                    width / data.size * (index + 1),
                    (if (barChartInfo.percentage > 0) topOffset + yOffsetForGraph + heightForGraph else topOffset + yOffsetForGraph).toFloat(),
                ),
            )
        }
    }
}

@Composable
@Preview
private fun Demo() {
    val barChartInfoList = listOf(
        BarChartInfo(
            -3.0,
            Color(0xFF2DC57B),
            "Company",
        ),
        BarChartInfo(
            -5.0,
            Color(0xFF2D9DC5),
            "Industry",
        ),
        BarChartInfo(
            1.0,
            Color(0xFFC58E2D),
            "Market",
        ),
    )
    Column(modifier = Modifier.fillMaxSize()) {
        PercentageBarChart(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2F),
            data = barChartInfoList,
        )
    }
}
