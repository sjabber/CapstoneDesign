//package com.example.dialog;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.capstone_design.R;
//import com.example.capstone_design.TouchEventManager;
//import com.example.adapter.TouchPointAdapter;
//import com.example.Touch.TouchEvent;
//import com.example.Touch.TouchPoint;
//import com.example.utils.DensityUtil;
//import com.example.utils.DialogUtils;
//import com.example.utils.GsonUtils;
//import com.example.utils.SpUtils;
//import com.example.utils.ToastUtil;
//
//import java.util.List;
//
//public class MenuDialog extends BaseServiceDialog implements View.OnClickListener {
//
//    private Button btStop;
//    private RecyclerView rvPoints;
//    private AddPointDialog addPointDialog;
//    private Listener listener;
//    private TouchPointAdapter touchPointAdapter;
//    private RecordDialog recordDialog;
//
//    public MenuDialog(@NonNull Context context) {
//        super(context);
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.dialog_menu;
//    }
//
//    @Override
//    protected int getWidth() {
//        return DensityUtil.dip2px(getContext(), 350);
//    }
//
//    @Override
//    protected int getHeight() {
//        return WindowManager.LayoutParams.WRAP_CONTENT;
//    }
//
//    @Override
//    protected void onInited() {
//        setCanceledOnTouchOutside(true);
//        findViewById(R.id.bt_exit).setOnClickListener(this);
//        findViewById(R.id.bt_add).setOnClickListener(this);
//        findViewById(R.id.bt_record).setOnClickListener(this);
//        btStop = findViewById(R.id.bt_stop);
//        btStop.setOnClickListener(this);
//        rvPoints = findViewById(R.id.rv);
//        touchPointAdapter = new TouchPointAdapter();
//        touchPointAdapter.setOnItemClickListener(new TouchPointAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position, TouchPoint touchPoint) {
//                btStop.setVisibility(View.VISIBLE);
//                dismiss();
//                TouchEvent.postStartAction(touchPoint);
//                ToastUtil.show("터치 포인트가 켜져 있습니다." + touchPoint.getName());
//            }
//        });
//        rvPoints.setLayoutManager(new LinearLayoutManager(getContext()));
//        rvPoints.setAdapter(touchPointAdapter);
//        setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                if (TouchEventManager.getInstance().isPaused()) {
//                    TouchEvent.postContinueAction();
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d("아 진짜 거래", "onStart");
//        ToastUtil.show("터치 실행");
//        //Dialog Menu 실행되는 부분.
//        //如果正在触控，则暂停
//        TouchEvent.postPauseAction();
//        if (touchPointAdapter != null) {
//            List<TouchPoint> touchPoints = SpUtils.getTouchPoints(getContext());
//            Log.d("아 진짜 거래", GsonUtils.beanToJson(touchPoints));
//            touchPointAdapter.setTouchPointList(touchPoints);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.bt_add:
//                DialogUtils.dismiss(addPointDialog);
//                addPointDialog = new AddPointDialog(getContext());
//                addPointDialog.setOnDismissListener(new OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        MenuDialog.this.show();
//                    }
//                });
//                addPointDialog.show();
//                dismiss();
//                break;
//            case R.id.bt_record:
//                dismiss();
//                if (listener != null) {
//                    listener.onFloatWindowAttachChange(false);
//                    if (recordDialog ==null) {
//                        recordDialog = new RecordDialog(getContext());
//                        recordDialog.setOnDismissListener(new OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialog) {
//                                listener.onFloatWindowAttachChange(true);
//                                MenuDialog.this.show();
//                            }
//                        });
//                        recordDialog.show();
//                    }
//                }
//                break;
//            case R.id.bt_stop:
//                btStop.setVisibility(View.GONE);
//                TouchEvent.postStopAction();
//                ToastUtil.show("터치가 중지되었습니다.");
//                break;
//            case R.id.bt_exit:
//                TouchEvent.postStopAction();
//                if (listener != null) {
//                    listener.onExitService();
//                }
//                break;
//
//        }
//    }
//
//    public void setListener(Listener listener) {
//        this.listener = listener;
//    }
//
//    public interface Listener {
//        /**
//         * 부동 창에 상태 변경 표시
//         * @param attach
//         */
//        void onFloatWindowAttachChange(boolean attach);
//
//        /**
//         * 보조 끄기
//         */
//        void onExitService();
//    }
//}
