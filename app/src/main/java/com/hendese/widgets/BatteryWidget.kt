package com.hendese.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

object BatteryWidget : GlanceAppWidget() {
    private val batteryPercentage = mutableStateOf(0)
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content(context = context)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun Content(context: Context) {
        LocalSize.current

        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        batteryPercentage.value = batteryLevel

        val progressBarBackgroundColor = Color.White
        val progressBarColor = when {
            batteryPercentage.value > 50 -> Color.Green
            batteryPercentage.value > 20 -> Color.Yellow
            else -> Color.Red
        }

        Log.d(
            "BatteryWidget",
            "Battery level: $batteryLevel Width: ${LocalSize.current.width} Height: ${LocalSize.current.height}"
        )

        Column(
            modifier = GlanceModifier.fillMaxSize().background(Color.DarkGray.copy(alpha = 0.25f)),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Button(
                text = "Pil: ${batteryPercentage.value}%",
                modifier = GlanceModifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = ColorProvider(Color.White.copy(alpha = 0.00f))),
                style = TextStyle(fontWeight = FontWeight.Bold, color = ColorProvider(Color.White), fontSize = 30.sp),
                onClick = {
                    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                    val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                    batteryPercentage.value = batteryLevel
                },
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Box(
                modifier = GlanceModifier
                    .background(Color.Blue.copy(alpha = 0.0f))
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(5.dp)
            ) {
                Box(
                    modifier = GlanceModifier
                        .width((LocalSize.current.width.value - 10).dp)
                        .height(30.dp)
                        .padding(1.dp)
                        .background(progressBarBackgroundColor)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxSize().fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.Start
                    ) {
                        Box(
                            modifier = GlanceModifier
                                .width((LocalSize.current.width.value * batteryPercentage.value / 100).dp) // Get current widgetSize here
                                .fillMaxHeight()
                                .background(progressBarColor)
                        ) {}
                    }
                }
            }
        }
    }

    class BatteryWidgetReceiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget: GlanceAppWidget
            get() = BatteryWidget

        override fun onReceive(context: Context, intent: Intent) {
            super.onReceive(context, intent)
            Log.d(
                "BatteryWidgetReceiver",
                "onReceive called with intent: " + intent.action + " ms: " + System.currentTimeMillis().toString()
            )
            /*if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                Log.d("BatteryWidgetReceiver", "ACTION_BATTERY_CHANGED: " + intent.action + " ms: " + System.currentTimeMillis().toString())
                val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
                coroutineScope.launch {
                    val glanceAppWidgetManager = GlanceAppWidgetManager(context)
                    val glanceIds = glanceAppWidgetManager.getGlanceIds(BatteryWidget::class.java)
                    glanceIds.forEach { glanceId ->
                        BatteryWidget.update(context, glanceId)
                    }
                }
            }*/
        }
    }
}