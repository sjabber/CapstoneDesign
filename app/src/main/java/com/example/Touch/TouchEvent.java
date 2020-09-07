package com.example.Touch;

import androidx.annotation.NonNull;

import com.example.utils.GsonUtils;

import org.greenrobot.eventbus.EventBus;

public class TouchEvent {
    public static final int ACTION_START = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_RESTART = 3;
    public static final int ACTION_STOP = 4;

    public int action;
    public TouchPoint touchPoint;

    public TouchEvent(int action) {
        this(action, null);
    }

    public TouchEvent(int action, TouchPoint touchPoint) {
        this.action = action;
        this.touchPoint = touchPoint;
    }

    public int getAction() {return action;}

    public static void postStartAction(TouchPoint touchPoint) {
        postAction(new TouchEvent(ACTION_START, touchPoint));
    }

    public static void postPauseAction() {
        postAction(new TouchEvent(ACTION_PAUSE));
    }

    public static void postRestartAction(TouchPoint touchPoint) {
        postAction(new TouchEvent(ACTION_RESTART, touchPoint));
    }

    public static void postStopAction() {
        postAction(new TouchEvent(ACTION_STOP));
    }

    private static void postAction(TouchEvent touchEvent) {
        EventBus.getDefault().post(touchEvent);
    }

    //문제 발생시 auto Touch 소스 참조
    @NonNull
    @Override
    public String toString() {
        return "action=" + action + " touchPoint=" + (touchPoint == null ? "null" : GsonUtils.beanToJson(touchPoint));
    }


}
