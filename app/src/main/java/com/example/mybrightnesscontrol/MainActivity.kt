package com.example.mybrightnesscontrol

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.slider.Slider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for permissions if granted it will directly go the set content part otherwise it will be redirected to the settings of the adjusting part
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

        // Find the brightness slider in the layout
        val brightnessSlider = findViewById<Slider>(R.id.brightnessSlider)

        // Set up the slider to control brightness
        brightnessSlider.addOnChangeListener { _, value, _ ->
            val brightnessValue = (value * 100).toInt()
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
    }
}
