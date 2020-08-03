package com.example.capstone_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db; // DB를 다루기 위한 SQLiteDabase 객체 생성
    Cursor cursor; // Select문 출력을 위해 사용하는 Cursor 형태객체 생성
    ListView listview; // Listview 객체 생성
    TextView textview;
    EditText EditName;
    ArrayAdapter<String> adapter;
    String[] result; //ArrayAdapter에 넣을 배열을 생성한다.
    String sql;
    ListUpdate update;




    public static int PositionNumber;
    public static int SwitchPosition;
    private View Customlistview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액티비티 전환
        //뷰(버튼)의 주소값을 얻어온다.
        // 뷰의 주소값을 담을 참조변수
        Button button1 = (Button)findViewById(R.id.button1);

        //Switch Switch_1 = (Switch)findViewById(R.id.switch1);

        //커스텀 리스트뷰 객체 호출 -> 메모리적재
        Customlistview = LayoutInflater.from(this).inflate(R.layout.list1, null);
        final Switch SwitchButton = Customlistview.findViewById(R.id.switch1);

        final onClickSwitch SwitchListener1 = new onClickSwitch();


        // 작동 제대로 안되는 코드
        //// TODO: 2020-08-03
        SwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int position = (Integer) buttonView.getId();

                SwitchListener1.onClick(SwitchButton);
                int clickSwitch = (int) R.id.switch1;
                Toast.makeText(MainActivity.this, "매크로 활성화", Toast.LENGTH_SHORT).show();
                Log.d("스위치", "on버튼");

                if (SwitchPosition == clickSwitch) {
                if (isChecked) {
                    Toast.makeText(MainActivity.this, "매크로 활성화", Toast.LENGTH_SHORT).show();
                    Log.d("스위치", "on버튼");
                } else {
                    Toast.makeText(MainActivity.this, "매크로 비활성화", Toast.LENGTH_SHORT).show();
                  }
                }
            }
        });


        // 리스너 객체를 생성
        BtnListener1 listener1 = new BtnListener1();


        // 리스너를 버튼 객체에 설정한다.
        button1.setOnClickListener(listener1);

        // 리스트 갱신 객체를 생성
        update = new ListUpdate();

        // 리스트뷰를 더한다.
        db = openOrCreateDatabase("Macro_DB", MODE_PRIVATE, null);
        listview = (ListView)findViewById(R.id.Mac_List);
        textview = (TextView)findViewById(R.id.Mac_List_Text);
        EditName = (EditText)findViewById(R.id.EditMacroName);

        try{
            //리스트뷰 갱신
            update.ListUpdate();
        }

        catch (SQLException e) {
            Toast toast = Toast.makeText(this, "데이터베이스가 로드되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        // 뷰에 컨텍스트 메뉴를 설정한다.
        registerForContextMenu(listview); // 리스트뷰를 길게 누르면 컨텍스트 메뉴가 나온다.

    }

    public void onRestart() {
        super.onRestart();
        try {
            //리스트뷰 갱신
            update.ListUpdate();
        }

        catch (SQLException e) {
            Toast toast = Toast.makeText(this, "데이터베이스가 로드되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        // 뷰에 컨텍스트 메뉴를 설정한다.
        registerForContextMenu(listview); // 리스트뷰를 길게 누르면 컨텍스트 메뉴가 나온다.
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    // 컨텍스트 메뉴가 설정되어 있는 뷰를 길게 누르면 컨텍스트 메뉴 구성을 위해서 호출하는 메서드드
   @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        final int view_id = v.getId();


        switch(view_id) {
            case R.id.Mac_List :
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                menu.setHeaderTitle((info.position+1)+ "번 매크로 설정");
//                Toast toast = Toast.makeText(this, "매크로이름 : " + result[info.position], Toast.LENGTH_SHORT);
//                toast.show();
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

            // EditActivity 클래스에서도 사용돼야해서 중요함.
            PositionNumber = info2.position;
        }

        switch (id) {
            // 컨텍스트 메뉴의 수정버튼을 눌렀을 경우 동작하는 내용
            case R.id.Edit_Mac :
                try {
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    startActivity(intent);

                    //sql = "SELECT Mac_name FROM MACRO WHERE Mac_Name = (?)";


                    //Toast toast = Toast.makeText(this, "아직 구현되지 못함", Toast.LENGTH_SHORT);
                    //toast.show();

                } catch (Exception e) {
                    Toast toast = Toast.makeText(this, "오류발생", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            // 컨텍스트 메뉴의 삭제버튼을 눌렀을 경우 동작하는 내용
            case R.id.Delete_Mac :
                try {
                    sql = "DELETE FROM Macro WHERE Mac_name = (?)";
                    String[] name1 = {result[position]};
                    db.execSQL(sql, name1);

                    //리스트 갱신 메소드 호출
                    update.ListUpdate();

                    Toast toast = Toast.makeText(this, "매크로가 삭제되었습니다.", Toast.LENGTH_SHORT);
                    toast.show();

                } catch (SQLException e) {
                    Toast toast = Toast.makeText(this, "오류발생", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(@NonNull Menu menu) {
        super.onContextMenuClosed(menu);
        adapter.notifyDataSetChanged();
        listview.setAdapter(adapter);
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

    class ListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return result.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 재사용 가능한 뷰가 없다면 뷰를 만들어준다.
            if(convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.list1, null);
            }

            //뷰를 구성한다.
            TextView sub_text = (TextView)convertView.findViewById(R.id.textView_list);
            Switch sub_switch = (Switch)convertView.findViewById(R.id.switch1);

            //스위치에 인덱스 값을 저장한다.
            sub_switch.setTag(position);
            sub_text.setText(result[position]);

            //뷰를 반환한다.
            return convertView;
        }

    }

    // 커스텀 리스트뷰 갱신 메소드
    public class ListUpdate {
        public void ListUpdate() {
            sql = "SELECT * FROM Macro";
            cursor = db.rawQuery(sql, null); // select 사용시 사용됨

            int count = cursor.getCount(); //db에 저장된 행 개수를 읽어온다.
            result = new String[count];

            for (int i = 0; i < count; i++) {
                cursor.moveToNext(); // 모든 레코드를 읽어온다.
                String name = cursor.getString(1);
                result[i] = name;
            }
            // list1 레이아웃에 속한 textView의 id를 listview 객체(Main 레아아웃의 리스트 아이디 Mac_List 를 가진)에
            // 대입시키고 이에 쿼리결과인 result를 대입한다.
            adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list1, R.id.textView_list, result);
            adapter.notifyDataSetChanged();
            listview.setAdapter(adapter);
        }
    }





///////////////////////////////////////////////////////////////////////////////////////////
    //아래 전부 테스팅용 클래스들 추후 삭제나 수정 등의 조취를 취할 예쩡
    public class onClickSwitch implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SwitchPosition = v.getId();
        }

//        public int onClickSwitch() {
//            SwitchPosition = 0;
//            return SwitchPosition;
//        }
    }

    public class CheckableLinearLayout extends LinearLayout implements Checkable {
        public CheckableLinearLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        // 현재 Checked 상태를 리턴한다.
        public boolean isChecked() {
            Switch sw = (Switch) findViewById(R.id.switch1);

            return sw.isChecked();
        }

        // 현재 Checked 상태를 바꿈 (UI에 반영)
        @Override
        public void toggle() {
            Switch sw = (Switch) findViewById(R.id.switch1);
            setChecked(sw.isChecked() ? false : true);
        }

        // Checked 상태를 checked 변수대로 설정한다.
        @Override
        public void setChecked(boolean checked) {
            Switch sw = (Switch) findViewById(R.id.switch1);
            if (sw.isChecked() != checked) {
                sw.setChecked(checked);
            }
        }
    }
    //추가된 내용들 끝

    //Log.d("Problem", "컨텍스트메뉴 문제발생지점"); -> 문제발생시 위치확인용으로 쓸 로그문 추후 삭제
}