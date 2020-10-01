package com.hknu.Tutorial;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.hknu.capstone_design.MainActivity;
import com.hknu.capstone_design.R;

public class Tutorial extends AppCompatActivity {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext, setting, access;
    private int position_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_tutorial);

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);
        setting = findViewById(R.id.settings);
        access = findViewById(R.id.access);

// 변화될 레이아웃들 주소
        layouts = new int[] {
                R.layout.page1,
                R.layout.page2,
                R.layout.page3,
                R.layout.page4,
                R.layout.page5,
                R.layout.page6};
        addBottomDots(0);

        changeStatusBarColor();


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && position_number == 0) {
            //가로모드일 경우
            layouts[0] = R.layout.page1_land;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && position_number == 0) {
            //세로모드일 경우
            layouts[0] = R.layout.page1;
        }

        pagerAdapter = new PagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //설정 바로가기
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });

        access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //접근성 설정 바로가기
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });


        btnSkip.setOnClickListener(new View.OnClickListener() { // 건너띄기 버튼 클릭시 메인화면으로 이동
            @Override
            public void onClick(View v) {
                moveMainPage();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() { // 하나의 버튼을 이용하기 때문에 if else로 두가지 동작을 하게 만듬
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
//                    마지막 페이지가 아니라면 다음 페이지로 이동
                    viewPager.setCurrentItem(current);
                } else {
//                마지막 페이지라면 메인페이지로 이동
                    moveMainPage();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) { // 하단 점(선택된 점, 선택되지 않은 점)
        dots = new TextView[layouts.length]; // 레이아웃 크기만큼 하단 점 배열에 추가

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void moveMainPage() {
//        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Tutorial.this, MainActivity.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            position_number = position;

//            다음 / 시작 버튼 바꾸기
            if (position == 0) {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
                setting.setVisibility(View.GONE);
                access.setVisibility(View.GONE);
            }
            else if(position == 3) {
                //세로모드로 고정
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                //4번째 페이지에서 설정 바로가기 버튼이 뜨도록 한다.
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
                access.setVisibility(View.GONE);
                setting.setVisibility(View.VISIBLE);
            } else if (position == layouts.length - 1) {
                //세로모드로 고정
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                //마지막 페이지에서는 다음 버튼을 시작버튼으로 교체
                btnNext.setText(getString(R.string.start)); // 다음 버튼을 시작버튼으로 글자 교체
                btnSkip.setVisibility(View.GONE);
                setting.setVisibility(View.GONE);
                access.setVisibility(View.VISIBLE);
            } else {
                //세로모드로 고정
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                //마지막 페이지가 아니라면 다음과 건너띄기 버튼 출력
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
                setting.setVisibility(View.GONE);
                access.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void changeStatusBarColor() { // 최상단 바 색 변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter { // 아답터
        private LayoutInflater layoutInflater;

        public PagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}