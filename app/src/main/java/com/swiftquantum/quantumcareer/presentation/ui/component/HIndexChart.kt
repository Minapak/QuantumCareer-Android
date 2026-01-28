package com.swiftquantum.quantumcareer.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swiftquantum.quantumcareer.domain.model.CitationHistoryPoint
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumCareerTheme
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumPurple
import com.swiftquantum.quantumcareer.presentation.ui.theme.QuantumTeal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HIndexChart(
    hIndex: Int,
    i10Index: Int,
    citationHistory: List<CitationHistoryPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Citation Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    label = "h-Index",
                    value = hIndex.toString(),
                    color = QuantumPurple
                )
                MetricItem(
                    label = "i10-Index",
                    value = i10Index.toString(),
                    color = QuantumTeal
                )
            }

            if (citationHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Citation History",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                CitationLineChart(
                    data = citationHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CitationLineChart(
    data: List<CitationHistoryPoint>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val density = LocalDensity.current
    val textPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = with(density) { 10.sp.toPx() }
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }

    val maxCitations = remember(data) {
        data.maxOfOrNull { it.cumulativeCitations } ?: 1
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val paddingBottom = 30.dp.toPx()
        val paddingLeft = 40.dp.toPx()
        val chartWidth = width - paddingLeft
        val chartHeight = height - paddingBottom

        // Draw grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = chartHeight * i / gridLines
            drawLine(
                color = surfaceVariant,
                start = Offset(paddingLeft, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )

            // Draw y-axis labels
            val labelValue = maxCitations * (gridLines - i) / gridLines
            drawContext.canvas.nativeCanvas.drawText(
                labelValue.toString(),
                paddingLeft - 8.dp.toPx(),
                y + 4.dp.toPx(),
                textPaint.apply { textAlign = android.graphics.Paint.Align.RIGHT }
            )
        }

        if (data.size >= 2) {
            val path = Path()
            val stepX = chartWidth / (data.size - 1)

            data.forEachIndexed { index, point ->
                val x = paddingLeft + stepX * index
                val y = chartHeight - (point.cumulativeCitations.toFloat() / maxCitations * chartHeight)

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }

                // Draw data points
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw x-axis labels (first and last)
            val dateFormatter = DateTimeFormatter.ofPattern("MMM")
            if (data.isNotEmpty()) {
                drawContext.canvas.nativeCanvas.drawText(
                    data.first().date.format(dateFormatter),
                    paddingLeft,
                    height - 8.dp.toPx(),
                    textPaint.apply { textAlign = android.graphics.Paint.Align.LEFT }
                )
                drawContext.canvas.nativeCanvas.drawText(
                    data.last().date.format(dateFormatter),
                    width - 8.dp.toPx(),
                    height - 8.dp.toPx(),
                    textPaint.apply { textAlign = android.graphics.Paint.Align.RIGHT }
                )
            }
        }
    }
}

@Composable
fun CompactIndexDisplay(
    hIndex: Int,
    i10Index: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "h: ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = hIndex.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = QuantumPurple
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "i10: ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = i10Index.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = QuantumTeal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HIndexChartPreview() {
    QuantumCareerTheme {
        HIndexChart(
            hIndex = 12,
            i10Index = 8,
            citationHistory = listOf(
                CitationHistoryPoint(LocalDate.now().minusMonths(5), 10, 10),
                CitationHistoryPoint(LocalDate.now().minusMonths(4), 25, 15),
                CitationHistoryPoint(LocalDate.now().minusMonths(3), 45, 20),
                CitationHistoryPoint(LocalDate.now().minusMonths(2), 70, 25),
                CitationHistoryPoint(LocalDate.now().minusMonths(1), 100, 30),
                CitationHistoryPoint(LocalDate.now(), 120, 20)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
