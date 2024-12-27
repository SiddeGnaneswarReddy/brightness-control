package com.example.mybrightnesscontrol

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.slider.Slider

class MainActivity : AppCompatActivity() {

    private lateinit var batteryPercentageText: TextView
    private lateinit var chargingStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for permissions to write system settings
        if (!Settings.System.canWrite(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = android.net.Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }

        setContentView(R.layout.activity_main)

        // Handle insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the brightness slider
        val brightnessSlider = findViewById<Slider>(R.id.brightnessSlider)
        brightnessSlider.addOnChangeListener { _, value, _ ->
            val brightnessValue = (value * 255).toInt() // Convert slider value to brightness (0-255)
            try {
                Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Permission required to adjust brightness.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Initialize TextViews for battery status
        batteryPercentageText = findViewById(R.id.batteryPercentage)
        chargingStatusText = findViewById(R.id.chargingStatus)

        // Register a receiver for battery status updates
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                // Get battery level and scale
                val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = (level / scale.toFloat()) * 100

                // Update battery percentage text
                batteryPercentageText.text = "Battery Percentage: ${batteryPct.toInt()}%"

                // Check charging status
                val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                chargingStatusText.text = if (isCharging) {
                    "Charging Status: Charging"
                } else {
                    "Charging Status: Not Charging"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the battery receiver
        unregisterReceiver(batteryReceiver)
    }
}
