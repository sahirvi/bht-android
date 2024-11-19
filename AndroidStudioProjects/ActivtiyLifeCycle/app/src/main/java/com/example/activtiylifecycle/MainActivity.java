package com.example.activtiylifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Cycle","onCreate wurde ausgeführt"); // Debugmeldung sobald die Methode ausgeführt wird
        Toast.makeText(this, "onCreate ausgeführt", Toast.LENGTH_SHORT).show(); // Toastnachricht sobald die Methode ausgeführt wird
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Cycle","onStart wurde ausgeführt"); // Debugmeldung sobald die Methode ausgeführt wird
        Toast.makeText(this, "onStart ausgeführt", Toast.LENGTH_SHORT).show(); // Toastnachricht sobald die Methode ausgeführt wird
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Cycle","onResume wurde ausgeführt"); // Debugmeldung sobald die Methode ausgeführt wird
        Toast.makeText(this, "onResume ausgeführt", Toast.LENGTH_SHORT).show(); // Toastnachricht sobald die Methode ausgeführt wird
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Cycle","onPause wurde ausgeführt"); // Debugmeldung sobald die Methode ausgeführt wird
        Toast.makeText(this, "onPause ausgeführt", Toast.LENGTH_SHORT).show(); // Toastnachricht sobald die Methode ausgeführt wird
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Cycle","onStop wurde ausgeführt"); // Debugmeldung sobald die Methode ausgeführt wird
        Toast.makeText(this, "onStop ausgeführt", Toast.LENGTH_SHORT).show(); // Toastnachricht sobald die Methode ausgeführt wird
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Cycle","onDestroy wurde ausgeführt"); // Debugmeldung sobald die Methode ausgeführt wird
        Toast.makeText(this, "onDestroy ausgeführt", Toast.LENGTH_SHORT).show(); // Toastnachricht sobald die Methode ausgeführt wird
    }
}