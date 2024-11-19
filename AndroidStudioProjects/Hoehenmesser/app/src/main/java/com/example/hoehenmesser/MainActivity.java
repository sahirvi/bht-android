package com.example.hoehenmesser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private float defaultP = 1013.25F; // Druck bei 0 m Höhe

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textview1 = (TextView) findViewById(R.id.textView);

        TextView textview2 = (TextView) findViewById(R.id.textView2);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE); // sharedPreferences Objekt zur lokalen Speicherung von Daten

        float p = sharedPref.getFloat(getString(R.string.defaultPressure), defaultP); // get von dem defaultPressure Wert

        SeekBar seekbar = findViewById(R.id.seekbar); // seekbar mit bestimmter ID
        seekbar.setProgress((int) p); // nimmt als Eingabe den sharedPreferences Wert
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat(getString(R.string.defaultPressure), progress); // ändert den sharedPreferences nach dem progress Wert
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SensorEventListener sensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float p = sharedPref.getFloat(getString(R.string.defaultPressure), defaultP); // nimmt den sharedPrefernces Wert für die Formel
                float[] values = sensorEvent.values;
                double pressure = values[0];
                double factor1 = 288.15 / 0.0065;
                double radicand = pressure / p;
                double root = Math.pow(radicand, 1 / 5.255);
                double factor2 = (1 - root);

                double altitude = (factor1 * factor2); // Formel zur Umrechnung von Druck zu Höhe
                textview1.setText("Die aktuelle Höhe gemessen am Luftdruck: " + altitude + " m");
                textview2.setText(("aktueller Luftdruck: " + String.valueOf(sharedPref.getFloat(getString(R.string.defaultPressure), defaultP))) + " mbar");

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        sensorManager.registerListener(sensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_UI);

    }
}