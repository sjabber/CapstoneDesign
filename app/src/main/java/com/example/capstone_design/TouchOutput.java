//package com.example.capstone_design;
//
//
//// 음성으로 입력받은 String 과 Macro 테이블의 정보를 대조하여 일치하는 경우
//// 해당 좌표값을 전부 읽어와서 시간 간격값까지 계산하여 실행한다.
//// 정확도가 떨어질시 터치를 실행할때 0.3초 ~ 0.5초 정도의 여유를 준다.
//import android.app.Activity;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//
//import com.example.utils.ToastUtil;
//
//
//// 입력받았던 터치정보들을 출력할때 사용하는 클래스
//// 입력받은 음성정보와 매크로 이름을 대조하여 일치하는 경우
//// 해당 매크로의 Mac_num 을 리턴한다. -> Act_Mac
//
//
//public class TouchOutput extends Activity {
//
//    SQLiteOpenHelper MacroDatabaseHelper;
//    static MacroDBHelper helper;
//    static int Macro_Number;
//    static int a;
//    static String Macro_Name;
//    static Cursor cursor;
//    static SQLiteDatabase db2;
//
//    public void create_db(Context context) {
//        final String db_name = "Macro_DB";
//
//        try {
//            helper = new MacroDBHelper(this);
//            db = context.openOrCreateDatabase(db_name, MODE_PRIVATE, null);
//            db = helper.getWritableDatabase();
//            Log.d("DB 생성", "데이터베이스 오픈 성공");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("DB 오류", "데이터베이스 오픈 실패");
//        }
//    }
//
//
//    // 매크로 이름과 음성을 대조하여 일치할 경우 해당 매크로의 번호를 출력해주는 비교 메서드
//    public int comparison(String voice) {
//
//        create_db(this);
//
//        String sql = "SELECT Mac_num, Mac_name FROM Macro WHERE Mac_name = " + voice;
//        cursor = db.rawQuery(sql, null);
//
//        Macro_Number = cursor.getInt(0);
//        Macro_Name = cursor.getString(1);
//
//        if(Macro_Name == voice) {
//            a = Macro_Number;
//        } else {
//            ToastUtil.show("해당 매크로가 존재하지 않습니다.");
//        }
//
//        return a;
//    }
//}
