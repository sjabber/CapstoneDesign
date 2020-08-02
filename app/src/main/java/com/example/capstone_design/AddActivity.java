package com.example.capstone_design;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    EditText EditName;
    MacroDBHelper helper;
    SQLiteDatabase db = null;
    public static String inputedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // EditText뷰의 주소값을 받아올 변수 EditName
        EditName = findViewById(R.id.EditMacroName);
        //데이터베이스 변수 선언
        helper = new MacroDBHelper(this);
        db = helper.getWritableDatabase();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)) {
            askPermission();
            //안드로이드 버전이 충족되면 floating window 권한을 허용한다.
        }

        findViewById(R.id.saveButton).setOnClickListener(this);
    }

    //floating window 권한 허용을 위한 메소드
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }


    @Override
    public void onClick(View v) {

        //홈화면으로 이동시킴
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(home);

        //입력받은 값을 Macro 테이블 Mac_name 애트리뷰트에 추가한다.
        String MacroName = EditName.getText().toString();
        inputedName = MacroName;

        //여기서 수행하던 기능은 FloatingViewService 에서 저장버튼을 누르면 실행되도록 수정함.
//        String sql1 = "INSERT INTO Macro (Mac_name) VALUES (?)";
//
//        //중복된 값이 1개 이상인지 검사하는 쿼리
//        //String sql2 = "SELECT Mac_name FROM Macro WHERE Mac_name IN"
//        //            +"(SELECT Mac_name FROM Macro GROUP BY Mac_name HAVING COUNT(*)>1)";
//        String[] arg = {MacroName};
//
//
//        try{
//            db.execSQL(sql1, arg);
//            Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//                Log.d("Problem1", "쿼리문제 발생지점");
//                Toast.makeText(getApplicationContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show();
//
//        }

        // 버튼이 눌리면 위의 작업과 함께 Floatting window도 실행된다.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(new Intent(AddActivity.this, FloatingViewService.class));
            finish();
        } else if (Settings.canDrawOverlays(this)) {
            startService(new Intent(AddActivity.this, FloatingViewService.class));
            finish();
        } else {
            askPermission();
            Toast.makeText(this, "이 작업을 위해선 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
