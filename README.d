# ALARM - The Challenge Alarm Clock

A functional alarm clock app built with Jetpack Compose that forces you to complete a task before the alarm can be dismissed.
This project was built to practice modern Android development, including UI with Compose, system services like `AlarmManager`, and hardware sensor integration.

## 📸 Screenshots ->

MAIN SCREEN :  ![Image](https://github.com/user-attachments/assets/b4f64075-d840-4674-92d6-3d938bddd13c)

MAIN SCREEN (ALARM CARDS) :  ![Image](https://github.com/user-attachments/assets/5a17b191-2cf8-4f0a-ba9f-27abc6ad36a0)

SET ALARAM : ![Image](https://github.com/user-attachments/assets/2cd2f435-4f88-42eb-a414-001e59f032a7)

CHOOSE CHALLENGE : ![Image](https://github.com/user-attachments/assets/7f177521-39a3-4286-92e5-878ca7304e87)

ALARAM SCREEN : ![Image](https://github.com/user-attachments/assets/ef88f184-e1ed-495f-877d-7e68e931aa3a)


  

## ✨ Features ->

*   **Custom Alarms:** Set alarms using a modern time picker.
*   **Multiple Wake-Up Challenges:**
    *   **Shake Challenge:** User must shake their phone a set number of times.
    *   **Steps Challenge:** User must walk a set number of steps.
    *   **Standard Dismiss:** A standard alarm with no challenge.
*   **Persistent Storage:** Alarms are saved to the device and are restored when the app is relaunched.
*   **Full-Screen Alarm:** The alarm screen appears over the lock screen, plays a sound, and vibrates to ensure the user wakes up.
*   **Dynamic UI:** The ringing screen adapts its instructions based on the selected task.

## 🛠️ Built With ->

*   **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern, declarative UI.
*   **Architecture:** State management using `mutableStateOf`, `remember`, and `ViewModel` (if you used one).
*   **System Services:** `AlarmManager` to schedule precise alarms that work even when the app is closed.
*   **Hardware Sensors:** `SensorManager` to integrate the **Accelerometer** (for shaking) and **Step Detector**.
*   **Persistence:** `SharedPreferences` with `Gson` to save and load the list of alarms.
*   **Language:** [Kotlin](https://kotlinlang.org/) (including Coroutines for background tasks).

## 🚀 What I Learned

*   Managing UI state in a declarative way with Jetpack Compose.
*   Interacting with low-level Android system services like `AlarmManager` and `Vibrator`.
*   Handling hardware sensor data and implementing logic based on it.
*   Using `BroadcastReceiver` to trigger actions from the background.
*   The importance of handling Android permissions (like `SCHEDULE_EXACT_ALARM` and `ACTIVITY_RECOGNITION`).
*   Saving and loading data locally on the device.


