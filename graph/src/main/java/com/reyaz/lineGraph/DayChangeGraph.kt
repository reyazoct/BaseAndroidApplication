package com.reyaz.lineGraph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reyaz.models.StockPoint

@Composable
fun DayChangeGraph(
    modifier: Modifier = Modifier,
    stockPointList: List<StockPoint>,
) {
    if (stockPointList.size <= 1) return
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val stockValues = stockPointList.mapNotNull { it.stockValue }
        val maxHeight = stockValues.max()
        val minHeight = stockValues.min()

        val firstStockValue = stockPointList.first().stockValue ?: 0.0
        val lastStockValue = stockPointList.last().stockValue ?: 0.0
        val graphColor = if (firstStockValue > lastStockValue) {
            Color(0xFFE96D6D)
        } else if (firstStockValue < lastStockValue) {
            Color(0xFF2DC57B)
        } else {
            Color(0xFFFFFFFF)
        }

        val division = height / (maxHeight - minHeight)

        val offsetList = stockValues.mapIndexed { index, stockValue ->
            val currentValue = stockValue - minHeight
            val currentX = (width / (stockPointList.size - 1)) * index
            val currentY = currentValue * division

            Offset(currentX, (height - currentY).toFloat())
        }
        val path = Path().apply {
            repeat(offsetList.size) { index ->
                val currentOffset = offsetList[index]
                if (index == 0) {
                    moveTo(currentOffset.x, currentOffset.y)
                } else {
                    lineTo(currentOffset.x, currentOffset.y)
                }
            }
        }
        val startGradient = graphColor.copy(0.32F)
        val endGradient = graphColor.copy(0.00F)
        val brush = Brush.verticalGradient(listOf(startGradient, endGradient))
        val newPath = Path().apply {
            moveTo(0F, height)
            repeat(offsetList.size) { index ->
                val currentOffset = offsetList[index]
                lineTo(currentOffset.x, currentOffset.y)
            }
            lineTo(width, height)
        }

        drawPath(
            path = path,
            color = graphColor,
            style = Stroke(
                width = 4.dp.value,
                cap = StrokeCap.Round,
            ),
        )
        drawPath(
            path = newPath,
            brush = brush,
        )
    }
}

@Composable
@Preview
private fun Demo() {
    DayChangeGraph(
        modifier = Modifier
            .width(50.dp)
            .aspectRatio(4F),
        stockPointList = listOf(
            StockPoint(0, 1.0),
            StockPoint(0, 3.0),
            StockPoint(0, 2.0),
        ),
    )
}
