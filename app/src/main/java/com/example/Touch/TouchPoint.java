package com.example.Touch;

public class TouchPoint {
    private float x;
    private float y;
    private int delay;

    public TouchPoint(float x, float y, int delay) {
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

    public int getDelay() { return delay; }
}
