package com.reyaz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.reyaz.barChart.YearBarChart
import com.reyaz.models.MultiStockPoint
import com.reyaz.models.StockPoint
import com.reyaz.ui.theme.BaseApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaseApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    YearBarChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2F),
                        multiStockPointList = listOf(
                            MultiStockPoint(
                                stockPointList = listOf(
                                    StockPoint(1692388804000, 2.0),
                                    StockPoint(1660852804000, 3.0),
                                    StockPoint(1629316804000, 3.0),
                                    StockPoint(1597780804000, 1.0),
                                    StockPoint(1566158404000, -5.0),
                                    StockPoint(1534622404000, 4.0),
                                    StockPoint(1503086404000, -2.0),
                                    StockPoint(1471550404000, -4.0),
                                    StockPoint(1439928004000, 1.0),
                                ),
                                positiveGraphColor = Color(0xFF2DC57B),
                                negativeGraphColor = Color(0xFFE44848),
                                null,
                            ),
                            MultiStockPoint(
                                stockPointList = listOf(
                                    StockPoint(1692388804000, 2.0),
                                    StockPoint(1660852804000, 3.0),
                                    StockPoint(1629316804000, 3.0),
                                    StockPoint(1597780804000, 1.0),
                                    StockPoint(1566158404000, -5.0),
                                    StockPoint(1534622404000, 4.0),
                                    StockPoint(1503086404000, -2.0),
                                    StockPoint(1471550404000, -4.0),
                                    StockPoint(1439928004000, 1.0),
                                ),
                                positiveGraphColor = Color(0x772DC57B),
                                negativeGraphColor = Color(0x77E44848),
                                null,
                            ),
                            MultiStockPoint(
                                stockPointList = listOf(
                                    StockPoint(1692388804000, 2.0),
                                    StockPoint(1660852804000, 3.0),
                                    StockPoint(1629316804000, 3.0),
                                    StockPoint(1597780804000, 1.0),
                                    StockPoint(1566158404000, -5.0),
                                    StockPoint(1534622404000, 4.0),
                                    StockPoint(1503086404000, -2.0),
                                    StockPoint(1471550404000, -4.0),
                                    StockPoint(1439928004000, 1.0),
                                ),
                                positiveGraphColor = Color(0x222DC57B),
                                negativeGraphColor = Color(0x22E44848),
                                null,
                            )

                        ),
                        title = "WR Score"
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BaseApplicationTheme {
        Greeting("Android")
    }
}