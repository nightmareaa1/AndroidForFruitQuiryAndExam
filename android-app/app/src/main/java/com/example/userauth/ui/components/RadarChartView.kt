package com.example.userauth.ui.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun RadarChartView(
    dimensions: List<String>,
    values: List<Float>,
    colors: List<Int>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(factory = { ctx -> RadarChart(ctx) }, modifier = modifier, update = { chart ->
        val entries = ArrayList<RadarEntry>()
        for (v in values) entries.add(RadarEntry(v))
        val ds = RadarDataSet(entries, "Dimensions")
        ds.colors = colors.take(dimensions.size)
        ds.setDrawFilled(true)
        ds.fillAlpha = 180
        val data = RadarData(ds)
        chart.data = data
        chart.description.isEnabled = false
        chart.webLineWidth = 1f
        chart.webColor = Color.GRAY
        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(dimensions)
        xAxis.textSize = 9f
        chart.invalidate()
    })
}
