package com.example.Tutorial;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends Activity {
    SharedPreferences pref;
    //public static Activity SplashActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SplashActivity = SplashActivity.this;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 1초 이후에 튜토리얼 실행한다.
                Intent newIntent = new Intent(SplashActivity.this, Tutorial.class);
                startActivity(newIntent);
                finish();
                Log.d("test","튜토리얼 실행");
            }
        },1000);

    }
}