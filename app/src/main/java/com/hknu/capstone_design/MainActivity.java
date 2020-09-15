package com.hknu.capstone_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.SpannableString;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.hknu.Touch.TouchEvent;
import com.hknu.TouchService.Touch_In;
import com.hknu.Tutorial.Tutorial;
import com.hknu.permission.FloatWinPermissionCompat;
import com.hknu.utils.AccessibilityUtil;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db; // db를 다루기 위한 SQLiteDabase 객체 생성
    Cursor cursor; // Select문 출력을 위해 사용하는 Cursor 형태객체 생성
    SQLiteOpenHelper MacroDatabaseHelper;
    MacroDBHelper helper;
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

    public static Activity MainActivity; // AddActivity.java 에서 사용할 객체를 선언한다.
    private static final int PERMISSION = 1;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private final String STRING_ACCESS = "시작하기";
    private final String STRING_START = "추가하기";

    public static int Mac_number;
    public static int Position_N;
    public static int PositionNumber;
    private View CustomListView;
    private Menu menu;

    //음성인식 기능 변수선언
    public static String Voices;
    Context cThis;
    Intent SttIntent;
    SpeechRecognizer mRecognizer;
    EditText txtSystem;
    String[] rs;
    boolean status = true;
    Handler handler,handler_stop;
    boolean speech_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cThis = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //음성인식 권한 확인
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        //안드로이드 버전이 충족되면 floating window 권한을 허용한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)) {
            askPermission();
        }

        //변수가 Main 자기 자신임을 확인 → Add 에서 사용
        MainActivity = MainActivity.this;


        //커스텀 리스트뷰 객체 호출 -> 메모리적재
        CustomListView = LayoutInflater.from(this).inflate(R.layout.list1, null);
        Switch1 = (Switch) CustomListView.findViewById(R.id.switch1);

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
            Toast.makeText(this, "매크로가 로드되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }

        // 뷰에 컨텍스트 메뉴를 설정한다.
        registerForContextMenu(listview); // 리스트뷰를 길게 누르면 컨텍스트 메뉴가 나온다.

        // TODO 음성인식1 기능
        txtSystem=(EditText)findViewById(R.id.txtSystem);
        //에디트텍스트 선택해도 가상키보드 안 뜨도록
        txtSystem.setInputType(0);

        // 음성인식 앱 켜지고 1초뒤 실행
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (status) {
                    Log.d("test","시작");
                    speech_recognition();

                    // handler.removeMessages(0);
                    handler.postDelayed(this,6000);
                }
            }
        },1000);

        // 음성인식 앱 켜지고 일정시간(19 = 1 + 6 + 6 + 6) 이후 음성인식 종료
        handler_stop = new Handler();
        handler_stop.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (true) {
                    speech_stop();
                    Log.d("test","끝");
                    changeIcon3();
                    txtSystem.setText("마이크 버튼을 눌러주세요.");
                }
            }
        },19000);
    }

    //권한상태 점검
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();

        if(mRecognizer != null) {
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer=null;
        }

//        TouchEvent.postStopAction();
//        finish();
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
                    //선택된 매크로 삭제
                    String sql = "DELETE FROM Macro WHERE Mac_name = (?)";

                    //선택된 매크로보다 (Primary key) Mac_num 의 크기가 크면 하나씩 줄여서 순서를 재조정한다.
                    String sql1 = "UPDATE Macro SET Mac_num = Mac_num -1 WHERE Mac_num > (?)";

                    //삭제된 매크로와 관련된 터치패턴도 삭제
                    String sql2 = "DELETE FROM Act WHERE Act_Mac = (?)";

                    //터치패턴의 값도 재조정한다.
                    String sql3 = "UPDATE Act SET Act_Mac = Act_Mac -1 WHERE Act_Mac > (?)";

                    String[] name1 = {result[position]};
                    String[] name2 = {Integer.toString(position)};
                    String[] name3 = {Integer.toString(position + 1)};
                    //String[] name2 = {"" + (a + i - 1), "" + a + i};

                    //매크로 삭제
                    db.execSQL(sql, name1);
                    //재조정
                    db.execSQL(sql1, name2);
                    //매크로 패턴삭제
                    db.execSQL(sql2, name3);
                    //매크로 패턴 재조정
                    db.execSQL(sql3, name3);

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
        this.menu = menu;
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // 액션바 아이콘 이미지 변경
    // 마이크 오류 발생시
    public void changeIcon1 () {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MenuItem item = menu.findItem(R.id.item3);
                item.setIcon(R.drawable.mic_err);
            }
        });
    }

    // 마이크 정상 작동시
    public void changeIcon2 () {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MenuItem item = menu.findItem(R.id.item3);
                item.setIcon(R.drawable.mic_in_operation);
            }
        });
    }

    // 마이크 대기 상태시
    public void changeIcon3 () {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MenuItem item = menu.findItem(R.id.item3);
                item.setIcon(R.drawable.mic);
            }
        });
    }


    //옵션 메뉴의 항목을 터치하면 호출되는 메소드드
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //사용자가 터치한 항목 객체의 id를 추출한다.
        int id = item.getItemId();
        //분기한다.
        switch (id) {
            case R.id.item2 :
                // 추후 튜토리얼 화면으로 넘어가는 부분을 여기에 넣을 예정
                handler.removeCallbacksAndMessages(null);
                speech_stop();

                Intent intent_tutorial = new Intent(MainActivity.this, Tutorial.class);
                startActivity(intent_tutorial);
                Log.d("test","튜토리얼 실행");
                break;

            case R.id.item3:
                Handler handler_item3 = new Handler();
                if (speech_status) {
                    handler.removeCallbacksAndMessages(null);
                    speech_stop();
                    speech_status = false;

                    changeIcon3();
                    txtSystem.setText("마이크 버튼을 눌러주세요.");
                } else {
                    speech_recognition();
                    handler_item3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            speech_status = true;

                            changeIcon3();
                            txtSystem.setText("마이크 버튼을 눌러주세요.");
                        }
                    },6000);

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //추가하기 버튼과 연결될 리스너로 권한을 허용한 경우와 허용하지 않은 경우 실행되는 부분을 구분짓는다.
    class BtnListener1 implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //버튼을 누르면 발생하는 일을 적는다.
            switch (button1.getText().toString()) {
                case STRING_START : //앱의 권한이 허용되어있지 않은경우
                    // 추가하기 버튼 클릭시 음성인식 종료
                    handler.removeCallbacksAndMessages(null);
                    speech_stop();

                    // 추가하기 액티비티로 이동
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    startActivity(intent);
                    Log.d("test","추가 액티비티로 이동");
                    break;

                case STRING_ACCESS : //앱의 권한을 허용한 경우
                    RequestAccessibility();
                    break;
            }
        }
    }

    //권한을 얻은 허용한 경우와 허용하지 않은경우 각각 버튼의 문구를 다르게 적용한다.
    private void checkState() {
        boolean hasAccessibility = AccessibilityUtil.isSettingOpen(Touch_In.class, MainActivity.this);
        boolean hasWinPermission = FloatWinPermissionCompat.getInstance().check(this);
        if (hasAccessibility) {
            if (hasWinPermission) {
                button1.setText(STRING_START);
            }
        } else {
            button1.setText(STRING_ACCESS);
        }
    }

    private void RequestAccessibility() {
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

    //floating window 권한 허용을 위한 메소드
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
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

            CustomListView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list1, null);
            Switch1 = (Switch) CustomListView.findViewById(R.id.switch1);

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
                holder.textview = v.findViewById(R.id.textView_list);
                //스위치를 구성한다.
                holder.Switch1 = v.findViewById(R.id.switch1);

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

                    String active = "SELECT ACTIVE FROM Macro"; // ACTIVE 컬럼으로부터 값을 읽어온다.
                    cursor = db.rawQuery(active, null);
                    int counts = cursor.getCount();

                    result2 = new Boolean[counts]; // ACTIVE 컬럼에서 읽어온 크기만큼의 Boolean 배열 객체를 선언

                    for(int j = 0; j < counts; j++) {
                        cursor.moveToNext();
                        Boolean a = (cursor.getInt(0) != 0); // 저장된 그대로 읽어온다.
                        result2[j] = a;
                    }

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
//                    String active = "SELECT ACTIVE FROM Macro"; // ACTIVE 컬럼으로부터 값을 읽어온다.
//                    cursor = db.rawQuery(active, null);
//                    int counts = cursor.getCount();
//
//                    result2 = new Boolean[counts]; // ACTIVE 컬럼에서 읽어온 크기만큼의 Boolean 배열 객체를 선언
//
//                    for(int j = 0; j < counts; j++) {
//                        cursor.moveToNext();
//                        Boolean a = (cursor.getInt(0) != 0); // 저장된 그대로 읽어온다.
//                        result2[j] = a;
//                    }
                    if (result2[position] == true) {
                        holder.Switch1.setChecked(true);
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    //버튼에 터치동작이 들어오면 동작하는 부분
                    holder.Switch1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        //todo 스위치버튼
                        public void onClick(View v) {

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
            }

            return v;
        }
    }


    // 커스텀 리스트뷰 갱신 메소드
    // todo ListUpdate 메서드
    private class ListUpdate {
        private void ListUpdate() {
            items = new ArrayList<String>();

            sql = "SELECT * FROM Macro"; //쿼리문 작성
            cursor = db.rawQuery(sql, null); // rawQuery : select 문에 사용됨.

            //db에 저장된 행 개수를 읽어온다.
            Mac_number = cursor.getCount();

            // 불러온 행 개수만큼의 크기로 String 배열인 result 객체를 선언
            result = new String[Mac_number];
            //result2 = new Boolean[count];

            for (int i = 0; i < Mac_number; i++) {
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


///////////////////////////////////////////////////////////////////////////////////////////
    // todo 음성인식2 모듈
    private RecognitionListener listener = new RecognitionListener() {
    @Override
    public void onReadyForSpeech(Bundle bundle) {
        changeIcon2();
        txtSystem.setText("음성 인식 준비");
    }

    @Override
    public void onBeginningOfSpeech() {
        txtSystem.setText("음성 인식 중");
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("test","음성인식 끝");
    }

    @Override
    public void onError(int i) {
        changeIcon1();
        mRecognizer.destroy();
        mRecognizer.cancel();
        Log.d("test","음성인식 에러");

    }

    @Override
    public void onResults(Bundle results) {
        // 음성인식 결과는 ArrayList 형태로 넘어온다.
        String key= "";
        key = SpeechRecognizer.RESULTS_RECOGNITION;
        ArrayList<String> mResult = results.getStringArrayList(key);
        assert mResult != null;
        rs = new String[mResult.size()];
        mResult.toArray(rs);
        Toast1(rs[0]);
        FuncVoiceOrderCheck(rs[0]);

        // mRecognizer.startListening(SttIntent);
        Log.d("test",rs[0]);
        changeIcon3();

    }

    @Override
    public void onPartialResults(Bundle bundle) {
        txtSystem.setText("onPartialResults..........."+"\r\n"+txtSystem.getText());
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        txtSystem.setText("onEvent..........."+"\r\n"+txtSystem.getText());
    }
};

    //입력된 음성 메세지 확인 후 동작 처리
    private void FuncVoiceOrderCheck(String VoiceMsg) {

        if (VoiceMsg.length() < 1) return;
        VoiceMsg = VoiceMsg.replace(" ", "");//음성입력의 공백을 제거한다.
        Voices = VoiceMsg;

        try {

            // 현재 저장되어 있는 매크로 이름을 불러와서 리스트에 저장
            // DB에 없는 음성명령을 내릴경우 발생하는 오류처리

            //음성으로 부른 매크로가 앱에 존재하는지 여부를 점검한다.
            Cursor c1 = db.rawQuery("SELECT Mac_name, ACTIVE from Macro", null);
            HashMap<String, Integer> Macro = new HashMap<String, Integer>();

            while (c1.moveToNext()) {
                String str_load = c1.getString(0);
                int active = c1.getInt(1);
                Macro.put(str_load, active);
            }

            if (Macro.containsKey(Voices) && Macro.get(Voices) == 1) {

                //홈화면으로 이동시킴
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);

                SystemClock.sleep(250); //딜레이가 없으면 아래 명령어가 제대로 실행되지않음
                startActivity(home); //완전히 바탕화면으로 가기위해선 두번 실행되야함

                SystemClock.sleep(250);
                TouchEvent.postVoiceAction();

            } else if (Macro.containsKey(Voices) && Macro.get(Voices) != 1) {
                Toast toast = Toast.makeText(MainActivity.this, "해당 매크로가 비활성화 상태입니다.", Toast.LENGTH_SHORT);
                toast.show();
            } else if (!Macro.containsKey(Voices) && Voices.equals("종료")) {
                android.os.Process.killProcess(android.os.Process.myPid());
                //앱을 종료한다.
            } else if(!Macro.containsKey(Voices) && Voices.equals("재시작")) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.capstone_design");
                startActivity(launchIntent);

                Toast toast = Toast.makeText(MainActivity.this, "앱을 재실행 시킵니다.", Toast.LENGTH_SHORT);
                toast.show();
                //앱을 재시작시킨다.
            }


            else {
                Toast toast = Toast.makeText(MainActivity.this, "해당 매크로가 존재하지 않습니다.", Toast.LENGTH_SHORT);
                toast.show();
            }

        } catch (Exception e) {
            Toast toast = Toast.makeText(MainActivity.this, "오류발생", Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    // 입력한 음섬 메시지 토스트
    private void Toast1(String VoiceMsg) {
        if(VoiceMsg.length()<1){
            return;
        }
        VoiceMsg=VoiceMsg.replace(" ","");
        Toast t1 = Toast.makeText(this, VoiceMsg, Toast.LENGTH_SHORT);
        t1.show();
    }

    // 음성인식 객체 생성 및 실행
    private void speech_recognition() {
        // 어플이 실행되면 자동으로 1초뒤에 음성인식 시작
        txtSystem.setText("음성 인식 일시정지");
        // 음성인식
        // 음성인식 인텐트 생성
        SttIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getApplicationContext().getPackageName());
        // 음성인식 언어 설정
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        SttIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000);
        // 음성인식 객체
        mRecognizer=SpeechRecognizer.createSpeechRecognizer(cThis);
        // 음성인식 리스너 등록
        mRecognizer.setRecognitionListener(listener);
        // 음성인식 시작
        mRecognizer.startListening(SttIntent);

        speech_status = true;

        Log.d("test","음성인식");
    }

    // status 상태 false로 변환 -> 음성인식 종료시 필요
    public void status_false() {
        status = false;
    }

    // 음성인식 종료
    public void speech_stop() {
        if(mRecognizer != null) {
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer=null;
        }

        status_false();
    }

    // 뒤로가기 버튼
    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        android.app.AlertDialog.Builder alBuilder = new android.app.AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 해당앱의 루트 액티비티 종료
                finishAffinity();
                // 현재 작업중인 쓰레드가 다 종료되면 종료
                System.runFinalization();
                // 현재 액티비티 종료
                System.exit(0);
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        // "리셋" 버튼을 누르면 음성인식 재시작
        alBuilder.setNeutralButton("음성인식 재시작", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent_main = getIntent();
                finish();
                startActivity(intent_main);
            }
        });


        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
    }


}