package com.example.capstone_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;


public class MainActivity extends AppCompatActivity {

    MacroDBHelper helper;
    SQLiteDatabase db; // DB를 다루기 위한 SQLiteDabase 객체 생성
    Cursor cursor; // Select문 출력을 위해 사용하는 Cursor 형태객체 생성
    ListView listview; // Listview 객체 생성
    String[] result; //ArrayAdapter에 넣을 배열을 생성한다.
    String sql;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액티비티 전환
        //뷰(버튼)의 주소값을 얻어온다.
        // 뷰의 주소값을 담을 참조변수 button1
        Button button1 = (Button)findViewById(R.id.button1);
        // 리스너 객체를 생성
        BtnListener1 listener1 = new BtnListener1();
        // 리스너를 버튼 객체에 설정한다.
        button1.setOnClickListener(listener1);

        // 리스트뷰를 더한다.
        db = openOrCreateDatabase("Macro_DB", MODE_PRIVATE, null);
        listview = (ListView)findViewById(R.id.Mac_List);

        try{
            sql = "SELECT * FROM Macro";
            cursor = db.rawQuery(sql, null); // select 사용시 사용됨

            int count = cursor.getCount(); //db에 저장된 행 개수를 읽어온다.
            result = new String[count];

            for(int i = 0; i < count; i++) {
                cursor.moveToNext(); // 모든 레코드를 읽어온다.
                String name = cursor.getString(1);
                result[i] =name;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list1, R.id.textView_list, result);
            Log.d("TEST1", "문제 발생지점");
            listview.setAdapter(adapter);

//            SQLiteOpenHelper MacroDBHelper = new MacroDBHelper(this);
//            db = MacroDBHelper.getReadableDatabase();
//            c = db.query("Macro",
//                    new String[]{"_id", "Mac_num", "Mac_name"},
//                    null, null, null, null, null);
//
//            CursorAdapter adapter =
//                    new SimpleCursorAdapter(MainActivity.this,
//                        R.layout.list1, c,
//                        new String[]{"Mac_name"},
//                        new int[]{android.R.id.text1}, 0);
//            listview.setAdapter(adapter);


        }

        catch (SQLException e) {
            Toast toast = Toast.makeText(this, "데이터베이스가 로드되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onRestart() {
        super.onRestart();

        try {
            sql = "SELECT * FROM Macro";
            cursor = db.rawQuery(sql, null); // select 사용시 사용됨

            int count = cursor.getCount(); //db에 저장된 행 개수를 읽어온다.
            result = new String[count];

            for (int i = 0; i < count; i++) {
                cursor.moveToNext(); // 모든 레코드를 읽어온다.
                String name = cursor.getString(1);
                result[i] = name;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list1, R.id.textView_list, result);
            Log.d("TEST1", "문제 발생지점");
            listview.setAdapter(adapter);

        } catch (SQLException e) {
            Toast toast = Toast.makeText(this, "데이터베이스가 로드되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // 옵션 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    //옵션 메뉴의 항목을 터치하면 호출되는 메소드드
   @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //사용자가 터치한 항목 객체의 id를 추출한다.
       int id = item.getItemId();
       //분기한다.
       switch (id) {
           case R.id.item1 :
               Intent intent1 = new Intent(MainActivity.this, Setting.class);
               startActivity(intent1);

               //추후 추가할 내용(info)
           //case R.id.item2 :
               //Intent intent2 = new Intent(MainActivity.this, info.class);
               //startActivity(inten2);
       }
       return super.onOptionsItemSelected(item);
    }

    //추가하기 버튼과 연결될 리스너
    class BtnListener1 implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //버튼을 누르면 발생하는 일을 적는다.
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        }
    }
}


