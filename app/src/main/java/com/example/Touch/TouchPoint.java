package com.example.Touch;

//터치 좌표들을 한시적으로 get, set 하는 지점
public class TouchPoint {
    private float x;
    private float y;
    private long delay;

    public TouchPoint(float x, float y, long delay) {
        this.x = x;
        this.y = y;
        this.delay = delay;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public long getDelay() { return delay; }
}
