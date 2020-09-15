package com.hknu.capstone_design;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MacroDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Macro_DB";
    private static final int DB_VERSION = 3;


    public static final String TABLE_NAME = "Act";
    public static final String ACT_NUM = "Act_num";
    public static final String MAC_NUM = "Act_Mac";
    public static final String X = "Act_x";
    public static final String Y = "Act_y";
    public static final String TIME = "Act_time";


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
            String sql_2 = " CREATE TABLE " + TABLE_NAME + "("
                    + ACT_NUM +" INTEGER PRIMARY KEY, "
                    + MAC_NUM + " INTEGER, "
                    + X + " REAL, "
                    + Y + " REAL, "
                    + TIME + " REAL, "
                    + "FOREIGN KEY (" + MAC_NUM +") REFERENCES Macro(Mac_num))";
            db.execSQL(sql_2);
        }
    }
}
