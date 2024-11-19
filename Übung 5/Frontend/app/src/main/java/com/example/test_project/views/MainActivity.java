package com.example.test_project.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test_project.R;
import com.example.test_project.viewHelper.UserSessionManager;

/**
 * @author: Dominik Domonell
 * Activity that shows splash screen after being invoked.
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new UserSessionManager(getApplicationContext());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        openOnBoardingActivity();
    }

    /**
     * opens onBoarding Activity
     */
    private void openOnBoardingActivity() {
        Handler handler = new Handler();
        Runnable runnable = () -> {
            Intent intent = new Intent(MainActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        };
        handler.postDelayed(runnable, 2000);
    }

}