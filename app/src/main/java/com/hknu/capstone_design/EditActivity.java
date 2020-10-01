package com.hknu.capstone_design;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hknu.capstone_design.MainActivity.PositionNumber;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    MacroDBHelper helper;
    SQLiteDatabase db = null;
    EditText EditName;
    String[] result;
    String sql;
    Cursor cursor;
    CheckMacroDB check;

    //광고
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //세로모드로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //광고
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //데이터베이스 변수 선언
        helper = new MacroDBHelper(this);
        db = helper.getWritableDatabase();

        // EditText뷰의 주소값을 받아올 변수 EditName
        EditName = findViewById(R.id.EditMacroName_1);
        //Macro 테이블의 Mac_name 속성정보를 배열 result 에 삽입한다.
        check = new CheckMacroDB();
        check.CheckDB();
        //입력창에 선택된 매크로의 이름을 띄운다.
        EditName.setText(result[PositionNumber]);

        findViewById(R.id.editButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //입력 받은 매크로 이름 저장
        String MacroName_Edit = EditName.getText().toString();

        //todo : 정규식을 이용하여 변경할 이름들의 값과 조건을 검증한다.
        //특수문자 포함 여부 확인
        //한글을 제외한 영어, 숫자, 특수문자, 공백(띄어쓰기) 모두 제외
        Pattern pattern = Pattern.compile("^[가-힣]*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(MacroName_Edit);
        boolean bool = matcher.find();
        if (bool == false) {
            Toast.makeText(this, "매크로 이름을 한글로만 설정해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            //매크로 이름이 9글자 초과일 경우 오류 메시지
            if (MacroName_Edit.length() > 9) {
                Toast.makeText(this, "매크로 이름을 2~8글자로 설정해주세요.", Toast.LENGTH_SHORT).show();
            }
            //매크로 이름이 2글자 미만일 경우 오류 메시지
            else if (MacroName_Edit.length() < 2) {
                Toast.makeText(this, "매크로 이름을 2~8글자로 설정해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                sql = "UPDATE Macro SET Mac_Name = (?) WHERE Mac_name is (?)";
                String[] arg = {MacroName_Edit, result[PositionNumber]};

                try {
                    db.execSQL(sql, arg);
                    Toast toast = Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.LEFT | Gravity.BOTTOM, 330, 180);
                    toast.show();
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "오류발생", Toast.LENGTH_SHORT);
                    Log.d("오류 발생", "매크로 이름 변경 오류 발생 지점");
                    toast.show();
                }
            }
        }
    }

    // Macro 테이블 조회 & 배열에 Mac_name 속성 삽입 메소드
    public class CheckMacroDB {
        private void CheckDB(){
            sql = "SELECT * FROM Macro";
            cursor = db.rawQuery(sql, null);

            int count = cursor.getCount();
            result = new String[count];

            for (int i = 0; i < count; i++) {
                cursor.moveToNext(); // 모든 레코드를 읽어온다.
                String name = cursor.getString(1);
                result[i] = name;
            }
        }
    }

}
