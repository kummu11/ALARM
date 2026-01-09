package com.example.alarm

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt
import android.hardware.SensorEvent

class ShakeDetector(
    private val context : Context,
    private val onShake: () -> Unit
): SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0

    fun start() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }
        fun stop() {
            sensorManager?.unregisterListener(this)

        }

        override fun onSensorChanged(event: android.hardware.SensorEvent?) {
            if (event == null) return


            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate total force
            val gForce =
                kotlin.math.sqrt((x * x + y * y + z * z).toDouble()) / android.hardware.SensorManager.GRAVITY_EARTH

            // If force > 2.5 (A decent shake)
            if (gForce > 2.5) {
                val currentTime = System.currentTimeMillis()
                // Wait 500ms between shakes so it doesn't count too fast
                if (currentTime - lastShakeTime > 500) {
                    lastShakeTime = currentTime
                    onShake() // <--- THIS IS YOUR CODE RUNNING
                }
            }
        }

        override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {

        }
    }

