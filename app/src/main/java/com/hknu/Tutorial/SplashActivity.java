package com.hknu.Tutorial;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.hknu.capstone_design.MainActivity;

public class SplashActivity extends Activity {
    SharedPreferences pref;
    //public static Activity SplashActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        //SplashActivity = SplashActivity.this;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 1초 이후에 튜토리얼 실행한다.
                checkFirstRun();
                ActivityCompat.finishAffinity(SplashActivity.this);
            }
        },1000);

    }

    public void checkFirstRun() {
        boolean isFirstRun = pref.getBoolean("isFirstRun",true);
        if(isFirstRun) {
            Intent tutorial_intent = new Intent(SplashActivity.this, Tutorial.class);
            startActivity(tutorial_intent);
            Log.d("test","튜토리얼 실행");

            pref.edit().putBoolean("isFirstRun",false).apply();
        } else {
            // 1초 이후에 튜토리얼 실행한다.
            Intent newIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(newIntent);
            Log.d("test","메인액티비티 실행");
        }
    }
}