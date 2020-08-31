package com.example.dialog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.example.Touch.TouchEvent;
import com.example.capstone_design.MacroDBHelper;
import com.example.capstone_design.R;
import com.example.Touch.TouchPoint;
import com.example.utils.SpUtils; //SharedPreference 로 값을 저장하려고 함.
import com.example.utils.ToastUtil;

import java.util.Enumeration;

import static com.example.capstone_design.TouchInput2.num;


public class AddPointDialog extends BaseServiceDialog implements View.OnClickListener {

    MacroDBHelper helper;
    SQLiteDatabase db = null;
    private EditText etName;
    private EditText etTime;
    private Group groupInput;
    private View transview;
    private int x;
    private int y;

    public AddPointDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_add_point;
    }

    @Override
    protected int getWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void onInited() {
        transview = findViewById(R.id.layout_content);
        findViewById(R.id.layout_content).setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            x = (int) event.getRawX();
            y = (int) event.getRawY();
            num ++;
            int second = 0;
            transview.setVisibility(View.GONE);
            groupInput.setVisibility(View.VISIBLE);
            Log.d("브레이크 포인트1", "브레이크포인트1");
            TouchPoint touchPoint = new TouchPoint(x, y, second);
            TouchEvent.postStartAction(touchPoint);
            dismiss();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        helper = new MacroDBHelper(getContext());
        db = helper.getWritableDatabase();
        num = 0;

        switch (v.getId()) {
            case R.id.transparent_view:
                num ++;
                int second = 0;
                String sql = "INSERT INTO Act (Act_x, Act_y) VALUES (?, ?)";
                String[] arg = {Float.toString(x), Float.toString(y)};
                try {
                    db.execSQL(sql, arg);
                    ToastUtil.show( num +"번");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("브레이크 포인트1", "브레이크포인트1");
                TouchPoint touchPoint = new TouchPoint(x, y, second);
                //SpUtils.addTouchPoint(getContext(), touchPoint);
                TouchEvent.postStartAction(touchPoint);
                ToastUtil.show("터치 포인트가 켜져 있습니다.");
                dismiss();
                break;
        }
    }
}
