package com.example.Tutorial;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.capstone_design.MainActivity;
import com.example.capstone_design.R;

import me.relex.circleindicator.CircleIndicator;

public class Tutorial extends AppCompatActivity {
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 1;
    FragmentPagerAdapter adapterViewPager;
    private static final int PERMISSION = 1;
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

//        //음성인식 권한 확인
//        if (Build.VERSION.SDK_INT >= 23) {
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET,
//                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
//        }
//
//        //안드로이드 버전이 충족되면 floating window 권한을 허용한다.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)) {
//            askPermission();
//        }




        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        //하단 튜토리얼 페이지를 안내해주는 원표시
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);

        //튜토리얼 종료 버튼
        start = findViewById(R.id.tutorial_btn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(Tutorial.this, MainActivity.class);
                startActivity(newIntent);
                Log.d("test","튜토리얼 실행");
            }
        });
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:

                    return Tutorial_1.newInstance(0, "Page # 1");
                case 1:
                    return Tutorial_2.newInstance(1, "Page # 2");
                case 2:
                    return Tutorial_3.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

//    private void askPermission() {
//        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                Uri.parse("package:" + getPackageName()));
//        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
//    }
}
