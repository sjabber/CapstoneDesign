package com.hknu.capstone_design;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

//MainActivity을 import하여 public 객체들을 사용하고자함
import static com.hknu.capstone_design.MainActivity.PositionNumber;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    MacroDBHelper helper;
    SQLiteDatabase db = null;
    EditText EditName;
    String[] result;
    String sql;
    Cursor cursor;
    CheckMacroDB check;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

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
        String MacroName_Edit = EditName.getText().toString();
        // 변경할 값, 조건
        sql = "UPDATE Macro SET Mac_Name = (?) WHERE Mac_name is (?)";
        String[] arg = {MacroName_Edit, result[PositionNumber]};

        try{
            db.execSQL(sql, arg);
            Toast toast = Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.LEFT|Gravity.BOTTOM, 330, 180);
            toast.show();
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            startActivity(intent);
        }
        catch (Exception e) {
            Toast toast =Toast.makeText(getApplicationContext(), "오류발생", Toast.LENGTH_SHORT);
            toast.show();
        };

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

//    public class GetPosition extends MainActivity{
//        public int getposition() {
//            int position = 0;
//
//
//            return position;
//
//        }
//    }


}
