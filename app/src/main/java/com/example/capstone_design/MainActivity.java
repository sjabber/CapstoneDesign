package com.example.capstone_design;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //뷰(버튼)의 주소값을 얻어온다.
        // 뷰의 주소값을 담을 참조변수 add
        Button add = (Button)findViewById(R.id.add);

        // 리스너 객체를 생성
        BtnListener1 listener1 = new BtnListener1();
        // 리스너를 버튼 객체에 설정한다.
        add.setOnClickListener(listener1);
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


