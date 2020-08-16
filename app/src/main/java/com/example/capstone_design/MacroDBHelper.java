package com.example.capstone_design;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MacroDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Macro_DB";
    private static final int DB_VERSION = 3;

    public MacroDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, 3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //예전에 어플을 다운로드 받았던 사람들은 이 과정을 통해 최신으로 테이블을 업그레이드 할 수 있다.
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
                String sql_1 = "CREATE TABLE Macro("
                        + "Mac_num INTEGER PRIMARY KEY, "
                        + "Mac_name TEXT UNIQUE, "
                        + "CONSTRAINT CK_NAME CHECK (Mac_name != NULL))";
                // + "Mac_act BOOL not null)";
                db.execSQL(sql_1);
        }
        //Switch, case 문 대신 if 문으로 업데이트 사항 추가.
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE Macro ADD COLUMN ACTIVE NUMERIC;");
        }

        if (oldVersion < 3) {
            String sql_2 = "CREATE TABLE Act("
                    + "Act_num INTEGER PRIMARY KEY, "
                    + "Act_Mac INTEGER, "
                    + "Act_x REAL, "
                    + "Act_y REAL, "
                    + "Act_time REAL, "
                    + "FOREIGN KEY(Act_Mac) REFERENCES Macro(Mac_num))";
            db.execSQL(sql_2);
        }
    }
}
