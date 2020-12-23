package com.hknu.capstone_design;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class AddActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    EditText EditName;
    Button save;
    MacroDBHelper helper;
    SQLiteDatabase db = null;
    public static String inputedName;

    // 객체 MainActivity 의 변수 MA를 선언한다.
    MainActivity MA = (MainActivity) MainActivity.MainActivity;

    //광고
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //세로모드로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //광고
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // EditText뷰의 주소값을 받아올 변수 EditName
        EditName = findViewById(R.id.EditMacroName);
        //데이터베이스 변수 선언
        helper = new MacroDBHelper(this);
        db = helper.getWritableDatabase();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)) {
            askPermission();
            //안드로이드 버전이 충족되면 floating window 권한을 허용한다.
        }
        EditName.setOnKeyListener(this);

        // 저장버튼을 누르면 화면 FloatingViewService 로 넘어간다.
        save = findViewById(R.id.saveButton);
        save.setOnClickListener(this);
    }


    // 편의성 : 엔터키 입력 시 저장버튼 클릭으로 동작한다.
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //Enter key Action
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            save.setOnClickListener(this);
            save.performClick();
            return true;
        }
        return false;
    }

    //floating window 권한 허용을 위한 메소드
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
        finish(); // todo : 이 부분의 역할이 무엇인지?
    }


    @Override
    public void onClick(View v) {
        //todo : 2020.09.03 추가된 부분
        //음성인식 종료

                        //입력 받은 매크로 이름 저장
                        String MacroName = EditName.getText().toString();

                        //todo : 정규식을 이용하여 입력받는 이름들을 검증한다.
                        //특수문자 포함 여부 확인
                        //한글을 제외한 영어, 숫자, 특수문자, 공백(띄어쓰기) 모두 제외
                        Pattern pattern = Pattern.compile("^[가-힣]*$", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(MacroName);
                        boolean bool = matcher.find();
                        if (bool == false) {
                            Toast.makeText(this, "매크로 이름을 한글로만 설정해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            //매크로 이름이 9글자 초과일 경우 오류 메시지
                            if (MacroName.length() > 9) {
                                Toast.makeText(this, "매크로 이름을 2~8글자로 설정해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            //매크로 이름이 2글자 미만일 경우 오류 메시지
                            else if (MacroName.length() < 2) {
                                Toast.makeText(this, "매크로 이름을 2~8글자로 설정해주세요.", Toast.LENGTH_SHORT).show();
                            } else {

                                //현재 저장되어 있는 매크로 이름을 불러와서 리스트에 저장
                                Cursor c1 = db.rawQuery("SELECT Mac_name from Macro", null);
                                ArrayList<String> Mac_name_list = new ArrayList<>();
                                while (c1.moveToNext()) {
                                    String str_load = c1.getString(0);
                                    //DB에서 불러오는 매크로 이름에서 공백 제거 : 위에서 공백을 허용 안 함으로 주석처리
                                    //str_load = str_load.replaceAll(" ", "");
                                    Mac_name_list.add(str_load);
                                }

                                //저장된 매크로가 하나도 없을 경우에 매크로 이름 비교 없이 바로 생성
                                if (Mac_name_list.size() == 0) {
                                    //입력한 매크로 이름에서 공백 제거 : 공백허용을 안 함으로 주석처리
                                    String str_save = MacroName;
                                    str_save = str_save.replaceAll(" ", "");
                                    System.out.println(str_save);

                                    //입력받은 값을 Macro 테이블 Mac_name 애트리뷰트에 추가한다.
                                    inputedName = MacroName;

                                    //홈화면으로 이동시킴
                                    Intent home = new Intent(Intent.ACTION_MAIN);
                                    home.addCategory(Intent.CATEGORY_HOME);
                                    home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(home);

                                    SystemClock.sleep(250);
                                    startActivity(home);


                                    // 버튼이 눌리면 위의 작업과 함께 Floatting window도 실행된다.
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                        //홈 화면으로 이동한다.
                                        startService(new Intent(AddActivity.this, FloatingViewService.class));
                                        MA.finish();
                                        finish();
                    } else if (Settings.canDrawOverlays(this)) {
                        //홈 화면으로 이동한다.
                        startService(new Intent(AddActivity.this, FloatingViewService.class));
                        MA.finish();
                        finish();
                    } else {
                        askPermission();
                        Toast.makeText(this, "이 작업을 위해선 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                //저장된 매크로가 1개 이상 있을 경우
                else {
                    //입력한 매크로 이름과 저장된 매크로 이름을 비교해서 중복되지 않을 경우 Floating window 실행
                    for (int i = 0; i < Mac_name_list.size(); i++) {

                    //입력한 매크로 이름에서 공백 제거 : 공백허용을 안 함으로 주석처리
                    String str_save = MacroName;
                    str_save = str_save.replaceAll(" ", "");
                    System.out.println(str_save);

                        //매크로 이름을 비교
                        if (Mac_name_list.contains(MacroName)) {
                            Toast.makeText(getApplicationContext(), "매크로 이름이 중복되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            //입력받은 값을 Macro 테이블 Mac_name 애트리뷰트에 추가한다.
                            inputedName = MacroName;

                            //홈화면으로 이동시킴
                            Intent home = new Intent(Intent.ACTION_MAIN);
                            home.addCategory(Intent.CATEGORY_HOME);
                            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(home);
                            SystemClock.sleep(250);
                            startActivity(home);


                            // 버튼이 눌리면 위의 작업과 함께 Floatting window도 실행된다.
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                startService(new Intent(AddActivity.this, FloatingViewService.class));
                                MA.finish(); // 메인 Activity 종료
                                finish(); // AddActivity 종료
                            } else if (Settings.canDrawOverlays(this)) {
                                startService(new Intent(AddActivity.this, FloatingViewService.class));
                                MA.finish();
                                finish();
                            } else {
                                askPermission();
                                Toast.makeText(this, "이 작업을 위해선 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }


            }
        }
    }

    // 뒤로가기 버튼 클릭시 음성인식 재시작 필요
    public void onBackPressed() {
        Intent intent_main = new Intent(AddActivity.this, MainActivity.class);
        startActivity(intent_main);
    }
}
