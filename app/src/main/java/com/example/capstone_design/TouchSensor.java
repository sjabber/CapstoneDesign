package com.example.capstone_design;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class TouchSensor extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View view = new TouchSensor.MyView(this);
        setContentView(view);
    }

    protected class MyView extends View {

        public MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // TODO Auto-generated method stub
            super.onTouchEvent(event);

            //event
            //event 종류/각각의 특성

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                float x = event.getX();
                float y = event.getY();

                String msg = "터치를 입력받음 : " + x + " / " + y;

                Toast.makeText(TouchSensor.this, msg, Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        }
    }

    //    @Override
//      블로그에서 퍼온 찌거기 자료
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.layout.activity_test, menu);
//        return true;
//    }
}
