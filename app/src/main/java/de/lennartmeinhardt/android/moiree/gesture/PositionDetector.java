package de.lennartmeinhardt.android.moiree.gesture;

import android.view.MotionEvent;

public class PositionDetector {

    private final PositionListener listener;

    private float startX, startY;
    private float lastX, lastY;


    public PositionDetector(PositionListener listener) {
        this.listener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                lastX = startX;
                lastY = startY;
                listener.onGestureStarted(startX, startY);
                return true;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                listener.onGestureEnded(lastX, lastY);
                return false;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() > 1) {
                    listener.onGestureEnded(lastX, lastY);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    lastX = event.getX();
                    lastY = event.getY();
                    listener.onPositionChanged(lastX, lastY);
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                if(event.getPointerCount() == 2) {
                    // get the position of the pointer that wasn't lifted to be the start
                    startX = event.getX(1 - event.getActionIndex());
                    startY = event.getY(1 - event.getActionIndex());
                    lastX = startX;
                    lastY = startY;
                    listener.onGestureStarted(startX, startY);
                }
                return true;
        }
        return false;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getLastX() {
        return lastX;
    }

    public float getLastY() {
        return lastY;
    }


    public interface PositionListener {

        void onGestureStarted(float startX, float startY);

        void onPositionChanged(float newX, float newY);

        void onGestureEnded(float endX, float endY);


        class Adapter implements PositionListener {
            @Override
            public void onGestureStarted(float startX, float startY) {
            }

            @Override
            public void onPositionChanged(float newX, float newY) {
            }

            @Override
            public void onGestureEnded(float endX, float endY) {
            }
        }
    }
}
