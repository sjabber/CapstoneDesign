package com.example.capstone_design;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MacroDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Macro_DB";

    public MacroDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String sql = "CREATE TABLE Macro("
                + "Mac_num INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Mac_name TEXT UNIQUE, "
                + "CONSTRAINT CK_NAME CHECK (Mac_name != NULL))";
                // + "Mac_act BOOL not null)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            //예전에 어플을 다운로드 받았던 사람들은 이 과정을 통해 최신으로 테이블을 업그레이드 할 수 있다.

            case 1 :
                // 1에서 2버전 형태로 테이블 구조를 변경시키는 작업을 한다.
            case 2 :
                // 2에서 3버전 형태로 테이블 구조를 변경시키는 작업을 한다.
        }
    }
}
