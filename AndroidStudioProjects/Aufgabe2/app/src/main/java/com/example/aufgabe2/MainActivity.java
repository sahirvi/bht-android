package com.example.aufgabe2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = (EditText) findViewById(R.id.inputNumber);
        Button buttonC = (Button) findViewById(R.id.buttonCelsius);
        Button buttonF = (Button) findViewById(R.id.buttonFahrenheit);
        TextView textView = (TextView) findViewById(R.id.result);


        buttonC.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        String inputString = editText.getText().toString();
                        int f = Integer.parseInt(inputString);
                        int celsius = (f - 32) * 5 / 9;
                        textView.setText(" " + celsius);
                        System.out.println(celsius);
                    }
                });

        buttonF.setOnClickListener(
                new View.OnClickListener() {

                    public void onClick(View view) {
                        String inputString = editText.getText().toString();
                        int c = Integer.parseInt(inputString);
                        int fahrenheit = c * 9 / 5 + 32;
                        textView.setText(" " + fahrenheit);
                        System.out.println(fahrenheit);

                    }

                });
    }
}