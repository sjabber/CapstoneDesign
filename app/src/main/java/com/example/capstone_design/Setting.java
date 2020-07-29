package com.example.capstone_design;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Setting extends MainActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button button2 = (Button)findViewById(R.id.button2);
        BtnListener2 listener2 = new BtnListener2();
        button2.setOnClickListener(listener2);

    }

    //추가하기 버튼과 연결될 리스너
    class BtnListener2 implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //버튼을 누르면 발생하는 일을 적는다.
            Intent intent = new Intent(Setting.this, Setting_name.class);
            startActivity(intent);
        }
    }
}

