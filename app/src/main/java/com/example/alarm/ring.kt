package com.example.alarm

import android.R.attr.contentDescription
import android.R.attr.fontWeight
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.alarm.ui.theme.ALARMTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import kotlin.math.abs
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class AlarmActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: android.os.Vibrator? = null

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val timeText = intent.getStringExtra("time") ?: ""
        val amPmText = intent.getStringExtra("amPm") ?: ""
        val dayText = intent.getStringExtra( "day") ?: ""
        val dateText = intent.getStringExtra("Date") ?: ""
        val taskType = intent.getStringExtra("TASK_TYPE") ?: "None"
        val target = intent.getIntExtra("TASK_TARGET", 30)







// 1. BRUTE FORCE WAKE UP (Crucial for Emulators/Old Phones)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        // 2. NEWER WAKE UP LOGIC (For Android 8.1+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as android.app.KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }
        // media player :
        try{
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound2)
            val audioAttributes = AudioAttributes.Builder() //plays loud even if silent
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            mediaPlayer?.setAudioAttributes(audioAttributes)

            //loops it :
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()

            //vibrator :
            vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
            // This tells the phone: "Shake loud because it's an ALARM!"
            val vibrationAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

           // Pattern: Wait 0, Shake 1s, Pause 1s, Repeat (0)
            val effect = VibrationEffect.createWaveform(longArrayOf(0, 1000, 1000), 0)

            vibrator?.vibrate(effect, vibrationAttributes)

        } catch (e: Exception) {
            e.printStackTrace()


        }
        setContent {
            ALARMTheme {
            ringScreen(timeText, amPmText, dayText, dateText, taskType, target)
            }
        } }

        //stop song :
      override  fun onDestroy() {
            super.onDestroy()

            //stop   audio:
            if(mediaPlayer != null){
                if(mediaPlayer!!.isPlaying){
                    mediaPlayer!!.stop()
                }
            mediaPlayer?.release()
            mediaPlayer = null
        }
            //stop vibration :
            if(vibrator != null){
                vibrator?.cancel()
                vibrator = null
            }
      }
}


@Composable
fun ringScreen (time: String, amPm: String, day:String, date: String, taskType: String, target: Int) {
    var currentCount by remember { mutableIntStateOf(target) }
//for sensor  :
    val context = androidx.compose.ui.platform.LocalContext.current

// Connect the Sensor
    DisposableEffect(taskType) {

        //Shake :
        // This runs whenever a shake happens
        if (taskType == "Shake") {
            val shakeDetector = ShakeDetector(context) {
                if (currentCount > 0) {
                    currentCount--
                }
            }
            shakeDetector.start()
            onDispose { shakeDetector.stop() }
        }
        //WALK :
        else if (taskType == "Walk" || taskType == "Steps") {
            val stepDetector = StepDetector(context) {
                if (currentCount > 0) {
                    currentCount--
                }
            }
            stepDetector.start()
            onDispose { stepDetector.stop() }
        }

        // 3. NO TASK (Just dispose)
        else {
            onDispose { }
        }
    }
    val activity = context as? android.app.Activity

    Column(
        modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    //drag check if its upwrd
                    val isSwipeUp = dragAmount.y < -50

                    val isTaskComplete = (taskType == "None") || (currentCount <= 0)

                    if (isSwipeUp && isTaskComplete) {
                        change.consume()
                        activity?.finish()

                    } else if (isSwipeUp && !isTaskComplete) {
                        " TASK NOT DONE YET"
                    }

                        if(isSwipeUp && taskType == "None" ){
                            change.consume()
                            activity?.finish()
                        }

                }
            )
        }
        .fillMaxWidth()
        .background(Color(0x9E0F1115)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                time,
                modifier = Modifier.padding(top = 50.dp)
                    .alignByBaseline(),
                fontWeight = FontWeight.Light,
                fontSize = 70.sp,
                color = Color(0xFFEDEFF4)
            )

            Text(
                amPm,
                modifier = Modifier.alignByBaseline(),
                fontSize = 20.sp,
                color = Color(0xFFEDEFF4)
            )
        }

        Text(
            date,
            fontWeight = FontWeight.Light,
            fontSize = 20.sp,
            color = Color(0xFFEDEFF4),
        )

        Text(
            day,
            fontWeight = FontWeight.Light,
            fontSize = 20.sp,
            color = Color(0xFFEDEFF4),
        )

        Spacer(modifier = Modifier.weight(1f))

        if (taskType == "Shake" || taskType == "Steps" || taskType == "Walk") {
            Text(
                "$currentCount",
                fontSize = 80.sp
            )
        }

        val instructionText = when (taskType) {
            "Shake" -> "SHAKE YOUR PHONE"
            "steps","Steps","Walk", "walk"-> "WALK WALK WALK"
            "None", "none" -> "NO TASK TODAY"
            else -> "NO TASK TODAY"
        }

        Text(instructionText)

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            Icons.Filled.KeyboardArrowUp,
            contentDescription = "Up",
            tint = Color(0xFFEDEFF4),
            modifier = Modifier.size(40.dp)
        )


        if (taskType == "Shake" || taskType == "Steps" || taskType == "Walk") {
            Text(
                "Finish task to unlock Swipe.",
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                color = Color(0xFFEDEFF4),
                modifier = Modifier.padding(bottom = 40.dp)
                    .padding(horizontal = 50.dp),
                textAlign = TextAlign.Center
            )

            if (currentCount <= 0) {
                Text(
                    "Task Complete! Swipe up to dismiss",
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp,
                    color = Color(0xFFEDEFF4),
                    modifier = Modifier.padding(bottom = 40.dp)
                        .padding(horizontal = 50.dp),
                    textAlign = TextAlign.Center
                )
            }

        } else {
            Text(
                "Swipe up to dismiss the Alarm.",
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                color = Color(0xFFEDEFF4),
                modifier = Modifier.padding(bottom = 40.dp)
                    .padding(horizontal = 50.dp),
                textAlign = TextAlign.Center
            )
        }
    }

}

