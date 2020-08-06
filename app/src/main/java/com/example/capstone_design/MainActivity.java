package com.example.capstone_design;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
//import android.widget.BaseAdapter; -> BaseAdapter 에서 ArrayAdapter 로 변경됨
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
    CustomAdapter customAdapter;
    ArrayList<String> items;
    //SwitchListener check;
    SharedPreferences mPrefs;
    Switch Switch1;

    public static int PositionNumber;
    public static int SwitchPosition;
    private View Customlistview;
    private View Customlistview2;
    private int SwitchNum;
    boolean checkSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 스위치 리스너 객체를 생성
       // check = new SwitchListener();

        //todo 위치1
        // Shared Preference를 불러온다.
        mPrefs = getSharedPreferences("mPrefs", MODE_PRIVATE);

        //TODO: 2020-08-05, 2순위 해결과제
        //커스텀 리스트뷰 객체 호출 -> 메모리적재
        Customlistview = LayoutInflater.from(this).inflate(R.layout.list1, null);
        Switch1 = (Switch) Customlistview.findViewById(R.id.switch1);

        //저장된 스위치 값들을 불러온다.
        checkSwitch = mPrefs.getBoolean("Check" + SwitchNum,false);



        //액티비티 전환
        //뷰(버튼)의 주소값을 얻어온다.
        // 뷰의 주소값을 담을 참조변수
        Button button1 = (Button)findViewById(R.id.button1);

        // 스위치 갱신 객체를 생성, 위치변경 X
        customAdapter = new CustomAdapter(MainActivity.this, R.layout.list1, R.id.textView_list, items);

        // 추가하기 버튼 리스너 객체를 생성
        BtnListener1 listener1 = new BtnListener1();

        // 추가하기 버튼의 리스너를 버튼 객체에 설정한다.
        button1.setOnClickListener(listener1);

        // 리스트 갱신 객체를 생성
        update = new ListUpdate();


        // 리스트뷰를 더한다.
        db = openOrCreateDatabase("Macro_DB", MODE_PRIVATE, null);
        listview = (ListView)findViewById(R.id.Mac_List);
        textview = (TextView)findViewById(R.id.Mac_List_Text);
        EditName = (EditText)findViewById(R.id.EditMacroName);


        try{
            //리스트뷰 갱신 // TODO: 2020-08-05 //1순위 해결과제
            update.ListUpdate();
            Switch1.setChecked(checkSwitch);
//            check.onCheckedChanged(Switch1, chk);
//            chk = mPrefs.getBoolean("mPref", true);
//            Switch1.setChecked(chk);
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
            Switch1.setChecked(checkSwitch);

//            check.onCheckedChanged(Switch1, chk);
//            chk = mPrefs.getBoolean("mPref", true);
//            Switch1.setChecked(chk);
        }

        catch (SQLException e) {
            Toast toast = Toast.makeText(this, "데이터베이스가 로드되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        // 뷰에 컨텍스트 메뉴를 설정한다.
        registerForContextMenu(listview); // 리스트뷰를 길게 누르면 컨텍스트 메뉴가 나온다.
    }

    // TODO: 2020-08-06  
    public void onStop() {
        super.onStop();
        mPrefs = getSharedPreferences("mPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit(); //Editor를 불러온다.

        Switch1 = (Switch) Customlistview.findViewById(R.id.switch1);

        //저장할 값들을 입력한다.
        editor.putBoolean("Check" + SwitchNum, Switch1.isChecked());

        editor.commit(); //저장한다.
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
        customAdapter.notifyDataSetChanged();
        listview.setAdapter(customAdapter);
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


    //ArrayListAdapter 를 상속한 커스텀어댑터 -> 커스텀 리스트뷰에 적용 (customAdapter)
    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public CustomAdapter(Context context, int resource, int textViewResourceId, ArrayList<String> objects) {
            super(context, resource, textViewResourceId, objects);
            this.items = objects;

            Customlistview = LayoutInflater.from(MainActivity.this).inflate(R.layout.list1, null);
            Switch Switch1 = (Switch) Customlistview.findViewById(R.id.switch1);

            Customlistview2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.list1, null);
            textview = (TextView) Customlistview2.findViewById(R.id.textView_list);
        }

        @Override
        // 리스트 뷰의 항목 개수를 반환하는 메서드
        public int getCount() {
            return result.length;
        }

        @Override
        // 우리가 자유롭게 리턴하고싶은 객체를 반환해주면된다.
        // position에 항목의 인덱스번호가 들어온다.
        public String getItem(int position) {
            return null;
        }

        @Override
        // 리턴받은 객체에 대한 뷰의 ID값을 알려주는 메서
        public long getItemId(int position) {
            return 0;
        }

        //리스트뷰 항목 하나를 구성하여 반환한다. (xml파일 뷰객체를 반환해주는 역할)
        // 화면에 보이는 항목들만 보여주게한다.
        //재사용 가능한 뷰가 없다면 뷰를 만들어준다.
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list1, null);
            }

            String str = result[position];

            if(str != null) {
                //뷰를 구성한다.
                TextView sub_text = (TextView) v.findViewById(R.id.textView_list);
                //체크박스를 얻어온다.
                final Switch Switch1 = (Switch) v.findViewById(R.id.switch1);

                if(sub_text != null) {
                    //스위치에 인덱스 값을 저장한다.
                    Switch1.setTag(position);
                    //리스트의 텍스트에도 포지션값을 저장한다.
                    sub_text.setText(result[position]);
                }



            }
            // 스위치가 null이 아니라면
            if (Switch1 != null) {

            }



            //스위치버튼 클릭리스너를 넣어둔다.
            Switch1.setOnClickListener(new View.OnClickListener() {
                @Override
                //todo 스위치버튼
                public void onClick(View v) {
                    SwitchNum = position;
                    int id = v.getId();
                    switch (id) {
                        case R.id.switch1 :
                            if(Switch1.isChecked() == true) {
                                Toast.makeText(MainActivity.this, (position + 1)+"번 매크로 활성화", Toast.LENGTH_SHORT).show();
                                save1();
                                Log.d("저장", "저장지점");

                                break;

                            } else if(Switch1.isChecked() != true) {
                                Toast.makeText(MainActivity.this, (position + 1)+"번 매크로 비활성화", Toast.LENGTH_SHORT).show();
                                load();
                                Log.d("해제", "해제지점");
                                break;
                            }
                    }
                }
            });

            return v;
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

                //추가된 내용
                items = new ArrayList<>(); // update 로 인해 items ArrayList 에 값이 대입된다.
                for(String temp : result) { //result == Array, items == ArrayList
                    items.add(temp);
                }
            }

            customAdapter = new CustomAdapter(MainActivity.this, R.layout.list1, R.id.textView_list, items);
            customAdapter.notifyDataSetChanged();
            listview.setAdapter(customAdapter);
            listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
    }


///////////////////////////////////////////////////////////////////////////////////////////
    //아래 전부 테스팅용 클래스들 추후 삭제나 수정 등의 조취를 취할 예쩡


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
    //todo 저장, 로드
    private void save1() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean("Check" + SwitchNum, true);
        editor.apply();
        editor.commit();
    }

//    private void save2() {
//        SharedPreferences.Editor editor = mPrefs.edit();
//
//        editor.putBoolean("Check" + SwitchNum, Switch1.isChecked() != true);
//        editor.apply();
//    }

    private void load() {
        checkSwitch = mPrefs.getBoolean("Check" + SwitchNum, false);

    }

    ////////////////////////////////////////////////////////////////////////////////////////
//    class SwitchListener implements CompoundButton.OnCheckedChangeListener{
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            // 체크상태값이 변경된 체크박스, 스위치 객체의 주소값이 buttonView 로 들어가고
//            // 해당 체크박스, 스위치의 체크상태값이 isChecked 로 들어간다.
//            // 체크상태가 변경된 스위치의 id를 가져온다.
//            int id = buttonView.getId();
//            // 아이디 값으로 분기한다.
//            switch (id){
//                case R.id.switch1 :
//                    if(isChecked) {
//                        //todo 스위치 활성화시 기능 추가할 부분
//                        //스위치 값 저장
//                        save1();
//                        break;
//                    }
//                    else {
//                        break;
//                        //todo 스위치 비활성화시 기능 추가할 부분
//                        //스위치 값 저장
////                        mPrefs = getSharedPreferences("mPrefs", MODE_PRIVATE);
////                        chk = mPrefs.getBoolean("Check" + Integer.toString(SwitchNum),  false);
////                        Switch1.setChecked(chk);
//                    }
//            }
//        }
//    }
    //추가된 내용들 끝

    //Log.d("Problem", "컨텍스트메뉴 문제발생지점"); -> 문제발생시 위치확인용으로 쓸 로그문 추후 삭제
}