package com.bea.lifecyclev6

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sqrt

class HomepageActivity : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null

    //Step counter
    private var stepcounter: Sensor? = null

    //Accelerometer som backup
    private var accelerometer: Sensor? = null

    private var usingStepCounter = false

    //används när accelerometer backup
    private var steps = 0
    private var lastMagnitude = 0f
    private var stepText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)

        stepText = findViewById<TextView>(R.id.steps)

        //försök använda step counter
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepcounter = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        //hämta accelerometern som backup
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        //kontrollera om step countern finns
        if (stepcounter != null) {
            usingStepCounter = true
            stepText!!.setText("Your steps: 0")
        } else {
            //step counter finns inte
            usingStepCounter = false

            if (accelerometer != null) {
                stepText!!.setText(
                    "Step Counter not supported.\n" +
                            "Using Accelerometer instead.\n\n" +
                            "Steps: 0"
                )
            } else {
                stepText!!.setText(
                    "Step Counter and Accelerometer " +
                            "are not supported on this device."
                )
            }
        }

        //byt till räkna bmi vy
        val btn = findViewById<Button>(R.id.countBMIbtn)
        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val i = Intent(this@HomepageActivity, BmiActivity::class.java)
                startActivity(i)
            }
        })

        val profileText = findViewById<TextView>(R.id.profile);

        //hämtar data från sharedPreferences
        val preferences = getSharedPreferences("FitnessApp", MODE_PRIVATE);
        val name = preferences.getString("name", "");
        val gender = preferences.getString("gender", "");
        val phone = preferences.getString("phonenumber", "");
        val birthday = preferences.getLong("birthday",0L);
        val weight = preferences.getFloat("weight", 0f);
        val height = preferences.getFloat("height", 0f);
        val bmi = preferences.getFloat("bmi", 0f);

        //formaterar timestamp till yyyy-MM-dd
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val birthdaytext = formatter.format(java.util.Date(birthday));

        profileText.text =
            "Name: $name\n\n" +
            "Gender: $gender\n\n" +
            "Phonenumber: $phone\n\n"+
            "Birthday: $birthdaytext\n\n" +
            "Weigth: $weight kg\n\n" +
            "Height: $height cm\n\n" +
            "BMI: $bmi"

        //logga ut
        val logout = findViewById<Button>(R.id.logout)
        logout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val i = Intent(this@HomepageActivity, MainActivity::class.java)
                startActivity(i)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.main),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })
    }

    override fun onPause() {
        super.onPause()
        //stäng av sensorn när activity inte längre är aktiv
        sensorManager!!.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()

        if (usingStepCounter && stepcounter != null) {
            //aktivera step counter
            sensorManager!!.registerListener(this, stepcounter, SensorManager.SENSOR_DELAY_NORMAL)
        } else if (accelerometer != null) {
            //backup: aktivera accelerometer
            sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        //step counter
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            stepText!!.setText("Steps: " + totalSteps)
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            //beräkna rörelse storlek
            val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            //stegräkning
            if (magnitude - lastMagnitude > 2.0f) {
                steps++

                stepText!!.setText(
                    "Step Counter not supported.\n" +
                            "Using Accelerometer.\n\n" +
                            "Steps: " + steps
                )
            }

            lastMagnitude = magnitude
        }
    }
}