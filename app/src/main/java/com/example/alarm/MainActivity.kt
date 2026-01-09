package com.example.alarm

import android.R
import android.R.attr.checked
import android.R.attr.onClick
import android.R.attr.thickness
import android.R.attr.top
import android.app.PendingIntent
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log.e
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alarm.ui.theme.ALARMTheme
import androidx.compose.ui.input.key.Key.Companion.F
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.TimePicker
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.java
import java.util.Calendar
import kotlin.time.Duration.Companion.hours
import android.R.attr.icon
import android.R.attr.text
import androidx.compose.foundation.layout.PaddingValues


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ALARMTheme {
                setAlarmPage()
            }
        }
    }
}


fun schedulesystemAlarm(context: Context, hour: Int, minute: Int, taskType: String , target: Int ) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)

    intent.putExtra("TASK_TYPE", taskType)
    intent.putExtra("TASK_TARGET", target)

    val uniqueId = System.currentTimeMillis().toInt()
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        if (before(Calendar.getInstance())) {
            add(Calendar.DATE, 1)
        }
    }
    try {
        alarmManager.setExactAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}





// If I ever make an 'AlarmItem', it must have these 3 things, this helps us create a list later :
data class AlarmItem(val time: String, val amPm: String, var isActive: Boolean, val taskType: String, val taskTarget: Int)


//this is main page layout where user can enter new alrm plus the list will be stored here:
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun setAlarmPage() {
    val context = LocalContext.current
    val alarmsList = remember {
        mutableStateListOf<AlarmItem>() .apply{
            addAll(AlarmStorage.loadAlarms(context))
        }
    }
    var showClock by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState()
    var showTaskBox by remember { mutableStateOf(false) }
    var tempTime by remember { mutableStateOf("") } //to remb the temprory stored time user enetered while the user selects the t
    var tempAmPm by remember { mutableStateOf("") }





    Scaffold(
        containerColor = Color(0xFF0F141A),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showClock = true },
                containerColor = Color(0xFF1A1D24),
                contentColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Alarm")
            }
        }

    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(
                "Alarm",
                textAlign = TextAlign.Center,
                fontSize = 40.sp,
                color = Color(0xFFEDEFF4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.1.sp
            )

            Divider(
                modifier = Modifier.width(190.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, bottom = 10.dp),
                thickness = 2.dp,
                color = Color(0xBF9A9BAF)
            )

            if (alarmsList.isEmpty()) {
               Box(modifier = Modifier.fillMaxSize(),
                   contentAlignment = Alignment.Center){ Text(
                    "press + to add an alarm ",
                    fontSize = 20.sp,
                    color = Color(0xD24CAF50),
                    textAlign = TextAlign.Center,
                ) }
            } else {


                LazyColumn (modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 100.dp,
                            top = 10.dp )
                ) {
                    itemsIndexed( alarmsList) { index, alarm ->
                        alarmCard(
                            time = alarm.time,
                            amPm = alarm.amPm,
                            onDelete = { alarmsList.removeAt(index)
                            AlarmStorage.saveAlarms(context, alarmsList)
                                       },
                            isActive = alarm.isActive)


                        //gap btw cards
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
            if (showClock) {
                AlertDialog(
                    onDismissRequest = { showClock = false },
                    title = { Text("Set Alarm Time") },

                    text = { TimePicker(state = timeState) },

                    confirmButton = {
                        TextButton(onClick = {
                            //we will get the data now
                            val hour = timeState.hour
                            val minute = timeState.minute
                            val ampm = if (hour >= 12) "pm" else "am"
                            //convert to 12 hours format
                            var formattedhour = if (hour > 12) hour - 12 else hour
                            if (formattedhour == 0) formattedhour = 12

                            val displayMinute =
                                if (minute < 10) "0$minute" else minute.toString()

// save temprory time:
                            tempTime = "$formattedhour : $displayMinute"
                            tempAmPm = ampm

                            showClock = false
                            showTaskBox = true
                        }) { Text("NEXT") }
                    }
                )
            }

            if(showTaskBox){
                val scope = rememberCoroutineScope()
                var feedbackText by remember { mutableStateOf("") }
                LaunchedEffect(Unit) {
                    delay(5000) //5 sec

                    if(feedbackText.isEmpty()){
                        feedbackText = "Still thinking? C'mon challenge yourself."
                    }
                }

                //Interaction:
                var ShakeInteraction = remember { MutableInteractionSource() }
                var StepsInteraction = remember { MutableInteractionSource() }
                var lazyInteraction = remember { MutableInteractionSource() }


                    if (ShakeInteraction.collectIsPressedAsState().value) feedbackText =
                        "Good Choice! Stay Awake."
                    if (StepsInteraction.collectIsPressedAsState().value) feedbackText =
                        "Good Choice! Stay healthy."
                    if (lazyInteraction.collectIsPressedAsState().value) feedbackText =
                        "Fine, But you know what this leads to."




                AlertDialog(
                    onDismissRequest = { showTaskBox = false },
                    title = {
                        Row(Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically)
                           {
                                IconButton(onClick = {
                                    showTaskBox = false
                                    showClock = true
                                } )
                                {
                                    Icon(Icons.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color(0xFFEDEFF4),
                                        modifier = Modifier.size(30.dp))
                                }
                            Text("Choose your pain.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFEDEFF4) )
                           }
                        },
                    containerColor = Color(0xFF1F2933),
                    text = {
                        Column {
                            // Steps
                            Button(onClick = {
                                scope.launch { delay(1000)
                                alarmsList.add(AlarmItem(tempTime, tempAmPm, true, "Steps", 20))
                                    AlarmStorage.saveAlarms(context, alarmsList)
                                    schedulesystemAlarm(context, timeState.hour, timeState.minute, "Steps", 20)

                                showTaskBox = false } },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6A3D)),
                                interactionSource = StepsInteraction,
                                    modifier = Modifier.fillMaxWidth()
                            )
                            {  Text("Walk 20 Steps") }

                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Shake
                            Button(onClick = {
                                scope.launch {
                                    delay(1000)
                                alarmsList.add(AlarmItem(tempTime, tempAmPm, true, "Shake", 20))
                                    AlarmStorage.saveAlarms(context, alarmsList)
                                    schedulesystemAlarm(context, timeState.hour, timeState.minute, "Shake", 20)
                                showTaskBox = false } },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6A3D)),
                                interactionSource = ShakeInteraction,
                                modifier = Modifier.fillMaxWidth()
                            )
                            {  Text("Shake your phone 30 times") }

                            Spacer(modifier = Modifier.height(8.dp))


                            // NORMALLY WAKE UP
                            Button(onClick = {
                                scope.launch { delay(1000)
                                alarmsList.add(AlarmItem(tempTime, tempAmPm, true, "None", 0))
                                    AlarmStorage.saveAlarms(context, alarmsList)
                                    schedulesystemAlarm(context, timeState.hour, timeState.minute, "None", 0)
                                showTaskBox = false } },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A8F98)),
                                interactionSource = lazyInteraction,
                                modifier = Modifier.fillMaxWidth()
                            )
                            {  Text("I choose not to challenge myself.") }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = feedbackText,
                                color = Color(0xFFEDEFF4),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.height(20.dp)
                                    .fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {}
                )

            }
        }
    }
}


// this is layout of a single alarm card
@Composable
fun alarmCard(time: String, amPm: String, isActive: Boolean, onDelete: () -> Unit) {
    var switch by remember { mutableStateOf(isActive) }
    var delete by remember { mutableStateOf(false)  }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box() {


            Card(
                onClick = {delete = true},
                modifier = Modifier.size(width = 390.dp, height = 100.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D24)),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Row(modifier = Modifier.padding(start = 5.dp)) {
                        Text(
                            time,
                            modifier = Modifier.alignByBaseline(),
                            fontSize = 45.sp,
                            color = Color(0xFFEDEFF4)
                        )
                        Text(
                            amPm,
                            fontSize = 20.sp,
                            color = Color(0xFFEDEFF4),
                            modifier = Modifier.alignByBaseline()
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = switch,
                            onCheckedChange = { switch = it },
                            colors = SwitchDefaults.colors(
                                //INACTIVE STATE
                                checkedThumbColor = Color(0xFF8A8F9C),
                                checkedTrackColor = Color(0xFF4CCE54),

                                //ACTIVE STATE
                                uncheckedThumbColor = Color(0xFFFFFFFF),
                                uncheckedTrackColor = Color(0xFF2A2E39)
                            )

                        )

                    }
                }

            }

            if(delete){
                Card(modifier = Modifier.size(width = 390.dp, height = 100.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x7E1A1D24)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    onClick = {delete = false}) {
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()) {
                        IconButton(
                            onClick = {
                                onDelete()
                                delete = false
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(30.dp),
                                tint = Color.Red
                            )
                        }
                      }
                    }
                }
            }
        }
    }
