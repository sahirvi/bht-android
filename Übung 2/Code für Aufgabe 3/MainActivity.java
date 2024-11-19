package com.example.hoehenmesser;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private double defaultP = 1013.25; // Druck bei 0 m Höhe

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textview1 = (TextView) findViewById(R.id.textView);

        TextView textview2 = (TextView) findViewById(R.id.textView2);

        SeekBar seekbar = findViewById(R.id.seekbar); // seekbar mit bestimmter ID
        seekbar.setProgress((int) defaultP); // nimmt als Eingabe den default Pressure Wert
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                defaultP = progress; // ändert den default Pressure nach dem Slider
                textview2.setText(("aktueller Luftdruck: " + String.valueOf(defaultP)) + " mbar");
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
                float[] values = sensorEvent.values;
                double pressure = values[0];
                double factor1 = 288.15 / 0.0065;
                double radicand = pressure / defaultP;
                double root = Math.pow(radicand, 1 / 5.255);
                double factor2 = (1 - root);

                double altitude = (factor1 * factor2); // Formel zur Umrechnung von Druck zu Höhe
                textview1.setText("Die aktuelle Höhe gemessen am Luftdruck: " + altitude + " m");
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