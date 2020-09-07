package com.example.capstone_design;

import com.example.Touch.TouchEvent;

public class TouchEventManager {

    private static TouchEventManager touchEventManager;
    private int touchAction;

    public static TouchEventManager getInstance() {
        if (touchEventManager == null) {
            synchronized (TouchEventManager.class) {
                if (touchEventManager == null) {
                    touchEventManager = new TouchEventManager();
                }
            }
        }
        return touchEventManager;
    }

    private TouchEventManager() { }

    public void setTouchAction(int touchAction) {
        this.touchAction = touchAction;
    }

//    public int getTouchAction() {
//        return touchAction;
//    }
//
//
//    //todo 추후 수정 및 삭제
//    public boolean isTouching() {
//        return touchAction == TouchEvent.ACTION_START || touchAction == TouchEvent.ACTION_CONTINUE;
//    }
//
//    public boolean isPaused() {
//        return touchAction == TouchEvent.ACTION_PAUSE;
//    }
}
