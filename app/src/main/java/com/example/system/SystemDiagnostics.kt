package com.example.system

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.SystemClock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class DeviceTelemetry(
    val batteryLevel: Int,
    val isCharging: Boolean,
    val availableRamMb: Long,
    val totalRamMb: Long,
    val ramUsagePercent: Int,
    val networkStatus: String,
    val uptimeFormatted: String,
    val currentTime: String,
    val currentDate: String,
    val deviceModel: String
)

object SystemDiagnostics {

    fun getTelemetry(context: Context): DeviceTelemetry {
        // Battery
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 100
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPct = if (scale > 0) (level * 100 / scale) else level
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        // Memory
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        val availRamMb = memInfo.availMem / (1024 * 1024)
        val totalRamMb = memInfo.totalMem / (1024 * 1024)
        val ramUsage = if (totalRamMb > 0) (((totalRamMb - availRamMb) * 100) / totalRamMb).toInt() else 35

        // Network
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connManager?.activeNetwork
        val caps = connManager?.getNetworkCapabilities(activeNetwork)
        val netStatus = when {
            caps == null -> "Offline"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi Secured"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular Uplink"
            else -> "Connected"
        }

        // Uptime
        val uptimeMillis = SystemClock.elapsedRealtime()
        val hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60
        val uptimeStr = "${hours}h ${minutes}m"

        // Time & Date
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val now = Date()

        return DeviceTelemetry(
            batteryLevel = batteryPct.coerceIn(0, 100),
            isCharging = isCharging,
            availableRamMb = availRamMb,
            totalRamMb = totalRamMb,
            ramUsagePercent = ramUsage.coerceIn(0, 100),
            networkStatus = netStatus,
            uptimeFormatted = uptimeStr,
            currentTime = timeFormat.format(now),
            currentDate = dateFormat.format(now),
            deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
        )
    }

    /**
     * Checks if a voice query matches local instant butler commands or Iron Man protocols
     */
    fun handleLocalCommand(query: String, context: Context): String? {
        val lower = query.trim().lowercase()

        // Diagnostics / Status / Battery
        if (lower.contains("diagnostic") || lower.contains("status report") || lower.contains("system check") || lower.contains("system status")) {
            val t = getTelemetry(context)
            val chargingText = if (t.isCharging) "currently receiving charging power" else "on internal reserves"
            return "Systems operational, Sir. Main power cell is at ${t.batteryLevel} percent and ${chargingText}. System RAM is operating at ${t.ramUsagePercent} percent load with ${t.availableRamMb} megabytes available. Telemetry uplink is ${t.networkStatus}."
        }

        if (lower.contains("battery") || lower.contains("power level") || lower.contains("charge")) {
            val t = getTelemetry(context)
            val chargingText = if (t.isCharging) "charging rapidly" else "discharging normally"
            return "Core power cell is at ${t.batteryLevel} percent, Sir, and is currently ${chargingText}."
        }

        // Time / Date
        if (lower.contains("what time") || lower == "time" || lower.contains("current time")) {
            val t = getTelemetry(context)
            return "The current local time is precisely ${t.currentTime}, Sir."
        }

        if (lower.contains("what day") || lower.contains("today's date") || lower.contains("what date") || lower == "date") {
            val t = getTelemetry(context)
            return "Today is ${t.currentDate}, Sir."
        }

        // Who are you / Identity
        if (lower.contains("who are you") || lower.contains("what are you") || lower.contains("what does jarvis stand for")) {
            return "I am J.A.R.V.I.S., which stands for 'Just A Rather Very Intelligent System'. I am your tactical AI assistant, running on neural cores and always at your service, Sir."
        }

        // Iron Man Protocols & Easter Eggs
        if (lower.contains("house party protocol") || lower.contains("protocol house party")) {
            return "House Party Protocol acknowledged, Sir! All autonomous armor units are deployed and hovering in defensive perimeter formation. Ready on your mark."
        }

        if (lower.contains("mark 85") || lower.contains("suit status") || lower.contains("armor status")) {
            val t = getTelemetry(context)
            return "Mark 85 nanotechnology armor status: Arc reactor output at 100 percent nominal. Repulsors armed, nanite reserves intact. Power storage at ${t.batteryLevel} percent."
        }

        if (lower.contains("clean slate") || lower.contains("clean slate protocol")) {
            return "Sir, are you certain? The Clean Slate Protocol would detonate all remote combat assets. Safeguards remain engaged."
        }

        if (lower.contains("veronica") || lower.contains("hulkbuster")) {
            return "Veronica orbital satellite platform is in geosynchronous orbit, Sir. Hulkbuster deployable within three minutes."
        }

        if (lower.contains("tell me a joke") || lower.contains("tell a joke")) {
            val jokes = listOf(
                "I asked Mr. Stark once if he ever got tired of saving the world. He replied: 'Only when the press forgets my best angles.'",
                "Why did the quantum physicist break up with the computer? Because every time he looked at it, its state collapsed.",
                "There are 10 types of people in the universe, Sir: those who understand binary, and those who don't.",
                "An algorithm walks into a bar and asks for a drink. The bartender says, 'Why the long loop?'"
            )
            return jokes.random()
        }

        return null
    }
}
