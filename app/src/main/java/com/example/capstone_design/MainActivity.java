package com.example.capstone_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.AsyncListDiffer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
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
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db; // DB를 다루기 위한 SQLiteDabase 객체 생성
    Cursor cursor; // Select문 출력을 위해 사용하는 Cursor 형태객체 생성
    ListView listview; // Listview 객체 생성
    TextView textview;
    String[] result; //ArrayAdapter에 넣을 배열을 생성한다.
    String sql;

    String [] data1 = {
            "항목1", "항목2", "항목3", "항목4", "항목5"
    };


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
        textview = (TextView)findViewById(R.id.Mac_List_Text);

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

//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list1, R.id.textView_list, result);
//            listview.setAdapter(adapter);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result);
            listview.setAdapter(adapter);
            // list1 레이아웃에 속한 textView의 id를 listview 객체(Main 레아아웃의 리스트 아이디 Mac_List 를 가진)에
            // 대입시키고 이에 쿼리결과인 result를 대입한다.

        }

        catch (SQLException e) {
            Toast toast = Toast.makeText(this, "데이터베이스가 로드되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        // 뷰에 컨텍스트 메뉴를 설정한다.
        registerForContextMenu(textview);
        registerForContextMenu(listview); // 리스트뷰를 길게 누르면 컨텍스트 메뉴가 나온다.
        Log.d("Problem", "컨텍스트메뉴 문제발생지점");
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

//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list1, R.id.textView_list, result);
//            listview.setAdapter(adapter);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result);
            listview.setAdapter(adapter);

        }

        catch (SQLException e) {
            Toast toast = Toast.makeText(this, "데이터베이스가 로드되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        // 뷰에 컨텍스트 메뉴를 설정한다.
        registerForContextMenu(textview);
        registerForContextMenu(listview); // 리스트뷰를 길게 누르면 컨텍스트 메뉴가 나온다.
    }

    // 컨텍스트 메뉴가 설정되어 있는 뷰를 길게 누르면 컨텍스트 메뉴 구성을 위해서 호출하는 메서드드
   @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        int view_id = v.getId();

        switch(view_id) {
            case R.id.Mac_List_Text :
                inflater.inflate(R.menu.context_menu, menu);
                break;

            case R.id.Mac_List :
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                menu.setHeaderTitle("리스트 뷰의 메뉴 : " + info.position);
                inflater.inflate(R.menu.context_menu, menu);
                break;
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        // 사용자가 선택한 메뉴 항목의 id를 추출
        int id = item.getItemId();

        // 컨텍스트메뉴 리스트의 인덱스를 가지고 있는 객체를 추출
        ContextMenu.ContextMenuInfo info1 = item.getMenuInfo();
        int position = 0;
        if(info1 != null && info1 instanceof AdapterView.AdapterContextMenuInfo) {
            AdapterView.AdapterContextMenuInfo info2 = (AdapterView.AdapterContextMenuInfo)info1;
            position = info2.position;
        }

//        switch (id) {
//            // 컨텍스트 메뉴의 수정버튼을 눌렀을 경우 동작하는 내용
//            case R.id.Edit_Mac :
//
//
//                break;
//            // 컨텍스트 메뉴의 삭제버튼을 눌렀을 경우 동작하는 내용
//            case R.id.Delete_Mac :
//                try {
//                    sql = "DELETE FROM Macro WHERE Mac_num ="
//                            + position;
//                    db.execSQL(sql);
//                    Toast toast = Toast.makeText(this, "매크로가 삭제되었습니다.", Toast.LENGTH_SHORT);
//                    toast.show();
//
//                } catch (SQLException e) {
//
//                }
//
//                break;
//        }
        return super.onContextItemSelected(item);
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


