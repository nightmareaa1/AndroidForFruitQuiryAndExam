package com.example.userauth.ui.components

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun PieChart(
    data: List<Pair<String, Float>>,
    colors: List<Int>,
    modifier: Modifier = Modifier
){
    val ctx: Context = LocalContext.current
    AndroidView(factory = { ctx ->
        PieChart(ctx).apply {
            description.isEnabled = false
            isRotationEnabled = false
            setHoleColor(Color.WHITE)
            setDrawEntryLabels(false)
        }
    }, update = { chart ->
        val entries = data.map { PieEntry(it.second, it.first) }
        val ds = PieDataSet(entries, "")
        ds.colors = colors
        ds.sliceSpace = 2f
        val dataSet = PieData(ds)
        chart.data = dataSet
        chart.invalidate()
    }, modifier = modifier)
}
