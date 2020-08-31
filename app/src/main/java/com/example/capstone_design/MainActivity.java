package com.example.capstone_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;

import com.example.TouchService.Touch;
import com.example.permission.FloatWinPermissionCompat;
import com.example.utils.AccessibilityUtil;


public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db; // DB를 다루기 위한 SQLiteDabase 객체 생성
    SQLiteOpenHelper MacroDatabaseHelper;
    MacroDBHelper helper;
    Cursor cursor; // Select문 출력을 위해 사용하는 Cursor 형태객체 생성
    ListView listview; // Listview 객체 생성
    TextView textview;
    EditText EditName;
    String[] result; //ArrayAdapter에 넣을 배열을 생성한다.
    Boolean[] result2;
    String sql;
    ListUpdate update;
    CustomAdapter customAdapter;
    ArrayList<String> items;
    ArrayList<Integer> item_Position;
    Switch Switch1;
    TextView button1;

    private final String STRING_ACCESS = "시작하기";
    private final String STRING_START = "추가하기";


    public static int Position_N;
    public static int PositionNumber;
    private View Customlistview;
    //private View Customlistview2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //커스텀 리스트뷰 객체 호출 -> 메모리적재
        Customlistview = LayoutInflater.from(this).inflate(R.layout.list1, null);
        Switch1 = (Switch) Customlistview.findViewById(R.id.switch1);

        items = new ArrayList<String>();
        item_Position = new ArrayList<>();


        //액티비티 전환
        //뷰(버튼)의 주소값을 얻어온다.
        // 뷰의 주소값을 담을 참조변수
        button1 = (TextView) findViewById(R.id.button1);

        //DB 객체를 생성한다.
        // 리스트뷰를 더한다.
        MacroDatabaseHelper = new MacroDBHelper(this);
        db = openOrCreateDatabase("Macro_DB", MODE_PRIVATE, null);
        helper = new MacroDBHelper(this);
        db = helper.getWritableDatabase();

        listview = (ListView)findViewById(R.id.Mac_List);
        textview = (TextView)findViewById(R.id.Mac_List_Text);
        EditName = (EditText)findViewById(R.id.EditMacroName);

        // 스위치 갱신 객체를 생성, 위치변경 X
        customAdapter = new CustomAdapter(MainActivity.this, R.layout.list1, items);


        // 추가하기 버튼 리스너 객체를 생성
        BtnListener1 listener1 = new BtnListener1();

        // 추가하기 버튼의 리스너를 버튼 객체에 설정한다.
        button1.setOnClickListener(listener1);

        // 리스트 갱신 객체를 생성
        update = new ListUpdate();

        //리스트뷰를 읽어온다.
        try {
            update.ListUpdate();
        }

        catch (SQLException e) {
            Toast toast = Toast.makeText(this, "매크로가 로드되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        // 뷰에 컨텍스트 메뉴를 설정한다.
        registerForContextMenu(listview); // 리스트뷰를 길게 누르면 컨텍스트 메뉴가 나온다.

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkState();
    }

    public void onRestart() {
        super.onRestart();
        try {
            //리스트뷰 갱신
            update.ListUpdate();
        }

        catch (SQLException e) {
            Toast toast = Toast.makeText(this, "매크로가 로드되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        // 뷰에 컨텍스트 메뉴를 설정한다.
        registerForContextMenu(listview); // 리스트뷰를 길게 누르면 컨텍스트 메뉴가 나온다.
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
                Position_N = (info.position+1);
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

                } catch (Exception e) {
                    Toast toast = Toast.makeText(this, "오류발생", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
            // 컨텍스트 메뉴의 삭제버튼을 눌렀을 경우 동작하는 내용
            case R.id.Delete_Mac :
                try {
                    int i = 1;

                    //선택된 매크로 삭제
                    sql = "DELETE FROM Macro WHERE Mac_name = (?)";
                    //선택된 매크로보다 (Primary key) Mac_num 의 크기가 크면 하나씩 줄여서 순서를 재조정한다.
                    String sql1 = "UPDATE Macro SET Mac_num = Mac_num -1 WHERE Mac_num > (?)";

                    String[] name1 = {result[position]};
                    String[] name2 = {Integer.toString(position)};
                    //String[] name2 = {"" + (a + i - 1), "" + a + i};

                    //삭제
                    db.execSQL(sql, name1);
                    //재조정
                    db.execSQL(sql1, name2);

                    Toast toast = Toast.makeText(this, "매크로가 삭제되었습니다.", Toast.LENGTH_SHORT);
                    toast.show();

                    //리스트 갱신 메소드 호출
                    update.ListUpdate();

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
            switch (button1.getText().toString()) {
                case STRING_START :
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    startActivity(intent);
                    break;

                case STRING_ACCESS :
                    requestAcccessibility();
                    break;
            }
        }
    }

    private void checkState() {
        boolean hasAccessibility = AccessibilityUtil.isSettingOpen(Touch.class, MainActivity.this);
        boolean hasWinPermission = FloatWinPermissionCompat.getInstance().check(this);
        if (hasAccessibility) {
            if (hasWinPermission) {
                button1.setText(STRING_START);
            }
        } else {
            button1.setText(STRING_ACCESS);
        }
    }

    private void requestAcccessibility() {
        new AlertDialog.Builder(this).setTitle("서비스 접근권한 허용")
                .setMessage("\n" +
                        "권한을 허용해 주시 않을 경우 " + getString(R.string.app_name) + " 를 사용하실 수 없습니다.")
                .setPositiveButton("열기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 인증 인터페이스 표시
                        try {
                            AccessibilityUtil.jumpToSetting(MainActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("취소", null).show();
    }

    //ArrayListAdapter 를 상속한 커스텀어댑터 -> 커스텀 리스트뷰에 적용 (customAdapter)
    private class CustomAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> items;
        private int resource;
        //Switch Switch1;

        public CustomAdapter(Context context, int resource, ArrayList<String> items) {
            this.context = context;
            this.resource = resource;
            this.items = items;

            Customlistview = LayoutInflater.from(MainActivity.this).inflate(R.layout.list1, null);
            Switch1 = (Switch) Customlistview.findViewById(R.id.switch1);

//            Customlistview2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.list1, null);
//            textview = (TextView) Customlistview2.findViewById(R.id.textView_list);
        }

        // Holder 클래스 선언
        // 각 뷰를 보관하는 객체로 뷰를 재사용할때 다시 findViewById를 호출 하는 낭비를 줄이기 위해 사용됨
        private class ViewHolder {
            TextView textview;
            Switch Switch1;
        }

        @Override
        // 리스트 뷰의 항목 개수를 반환하는 메서드
        public int getCount() {
            return items.size();
        }

        @Override
        // 우리가 자유롭게 리턴하고싶은 객체를 반환해주면된다.
        // position에 항목의 인덱스번호가 들어온다.
        public ArrayList<String> getItem(int position) { return items; } // 변경되었음.

        @Override
        // 리턴받은 객체에 대한 뷰의 ID값을 알려주는 메서
        public long getItemId(int position) {
            return 0;
        }

        //리스트뷰 항목 하나를 구성하여 반환한다. (xml파일 뷰객체를 반환해주는 역할)
        // 화면에 보이는 항목들만 보여주게한다.
        //재사용 가능한 뷰가 없다면 뷰를 만들어준다.
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View v = convertView;
            final int SwitchPosition = position;

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list1, null, true);
            }

            String str = result[position];

            if(str != null) {
                holder = new ViewHolder();
                //뷰를 구성한다.
                holder.textview = (TextView) v.findViewById(R.id.textView_list);
                //체크박스를 얻어온다.
                holder.Switch1 = (Switch) v.findViewById(R.id.switch1);

                v.setTag(holder);

                Context context = Switch1.getContext();
                int SW = context.getResources().getIdentifier("Switch" + position, "d", context.getPackageName());

                if(holder.textview != null) {
                    //스위치에 인덱스 값을 저장한다.
                    holder.Switch1.setTag(position);

                    //holder.Switch1.setChecked(true);
                    //리스트의 텍스트에도 포지션값을 저장한다.
                    holder.textview.setText(result[position]);
                }

                // 스위치가 null이 아니라면
                if (holder.Switch1 != null) {

                    // 상태가 변경되면 저장하는 부분
                    // 상태가 변경되면 반응하는 부분 -> 기능적인 메소드를 집어넣을 공간에 해당한다.
                    // onClick 에서는 슬라이드를 감지하기 못하기때문에 삽입하였음
                    holder.Switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            listview.setItemChecked(position, isChecked);
                            if(isChecked) { //스위치가 ON 일 경우
                                for(int i = 0; i < item_Position.size(); i++) {
                                    if (item_Position.get(i) == SwitchPosition) {
                                        //todo 분기점1
                                        return;
                                    }
                                }
                                item_Position.add(SwitchPosition);

                                try {
                                    ContentValues SwitchValue = new ContentValues();
                                    SwitchValue.put("ACTIVE", holder.Switch1.isChecked());

                                    String sql1 = "UPDATE Macro SET ACTIVE = 1 WHERE Mac_num == (?)";
                                    String[] name1 = {Integer.toString(position+1)};

                                    //DB에 스위치의 상태값을 저장해놓는다.
                                    db.execSQL(sql1, name1);

                                } catch (SQLiteException e) {
                                    Toast toast = Toast.makeText(MainActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }

                            else { //스위치가 OFF 일 경우
                                for(int i = 0; i < item_Position.size(); i++) {
                                    if (item_Position.get(i) == SwitchPosition) {
                                        item_Position.remove(i);
                                        break;
                                    }
                                }

                                try {
                                    String sql1 = "UPDATE Macro SET ACTIVE = 0 WHERE Mac_num == (?)";
                                    String[] name1 = {Integer.toString(position+1)};

                                    //DB에 스위치의 상태값을 저장해놓는다.
                                    db.execSQL(sql1, name1);

                                } catch (SQLiteException e) {
                                    Toast toast = Toast.makeText(MainActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            }
                        }
                    });

                    // 체크된 아이템인지 판단할 boolean 함수
                    boolean isChecked = false;
                    for (int i = 0; i < item_Position.size(); i++) {
                        //만약 체크되었던 아이템이라면
                        // todo : keypoint 지점
                        if(item_Position.get(i) == SwitchPosition) {
                            holder.Switch1.setChecked(true);
                            isChecked = true;
                            break;
                        }
                    }
                    // 아니라면 체크안하는 부분
                    if (!isChecked) {
                        holder.Switch1.setChecked(false);
                    }

                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //DB 로부터 버튼의 ON / OFF 여부를 읽어와서 상태를 유지시키는 부분 -> 여기 외에 선언하면 제대로 작동하지 않는다.
                    String active = "SELECT ACTIVE FROM Macro"; // ACTIVE 컬럼으로부터 값을 읽어온다.
                    cursor = db.rawQuery(active, null);
                    int counts = cursor.getCount();

                    result2 = new Boolean[counts]; // ACTIVE 컬럼에서 읽어온 크기만큼의 Boolean 배열 객체를 선언

                    for(int j = 0; j < counts; j++) {
                        cursor.moveToNext();
                        Boolean a = (cursor.getInt(0) != 0); // 저장된 그대로 읽어온다.
                        result2[j] = a;
                    }
                    if (result2[position] == true) {
                        holder.Switch1.setChecked(true);
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    //버튼에 터치동작이 들어오면 동작하는 부분
                    holder.Switch1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        //todo 스위치버튼
                        public void onClick(View v) {
                            boolean status;
                            boolean ch = holder.Switch1.isChecked();

                            int id = v.getId();

                            switch (id) {
                                case R.id.switch1 :
                                    if(holder.Switch1.isChecked()) {
                                        Log.d("저장", "저장지점");
                                        try {
                                            Toast.makeText(MainActivity.this, (position + 1) + "번 매크로 활성화", Toast.LENGTH_SHORT).show();
                                            String sql1 = "UPDATE Macro SET ACTIVE = 1 WHERE Mac_num == (?)";
                                            String[] name1 = {Integer.toString(position+1)};

                                            //DB에 스위치의 상태값을 저장해놓는다.
                                            db.execSQL(sql1, name1);

                                        } catch (SQLiteException e) {
                                            Toast toast = Toast.makeText(MainActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }

                                    } else {

                                        Log.d("해제", "해제지점");
                                        try {
                                            Toast.makeText(MainActivity.this, (position + 1) + "번 매크로 비활성화", Toast.LENGTH_SHORT).show();
                                            String sql1 = "UPDATE Macro SET ACTIVE = 0 WHERE Mac_num == (?)";
                                            String[] name1 = {Integer.toString(position+1)};

                                            //DB에 스위치의 상태값을 저장해놓는다.
                                            db.execSQL(sql1, name1);

                                        } catch (SQLiteException e) {
                                            Toast toast = Toast.makeText(MainActivity.this, "데이터베이스 오류", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }

                                        break;
                                    }
                            }
                        }

                    });

                }
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            return v;
        }
    }

    // 커스텀 리스트뷰 갱신 메소드
    // todo update메서드
    public class ListUpdate {
        public void ListUpdate() {
            items = new ArrayList<String>();

            sql = "SELECT * FROM Macro"; //쿼리문 작성
            cursor = db.rawQuery(sql, null); // rawQuery : select 문에 사용됨.

            //db에 저장된 행 개수를 읽어온다.
            int count = cursor.getCount();

            // 불러온 행 개수만큼의 크기로 String 배열인 result 객체를 선언
            result = new String[count];
            //result2 = new Boolean[count];

            for (int i = 0; i < count; i++) {
                cursor.moveToNext(); // 모든 레코드를 읽어온다.

                // 두번째 열(Mac_name)의 내용들을 읽어온다.
                String name = cursor.getString(1);
                result[i] = name;

                items = new ArrayList<>();
                for(String temp : result) {
                    items.add(temp);
                }
            }
            customAdapter = new CustomAdapter(MainActivity.this, R.layout.list1, items);
            customAdapter.notifyDataSetChanged();
            listview.setAdapter(customAdapter);
        }
    }


}
//Log.d("Problem", "컨텍스트메뉴 문제발생지점"); -> 문제발생시 위치확인용으로 쓸 로그문 추후 삭제