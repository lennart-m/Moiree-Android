package de.lennartmeinhardt.android.moiree.gesture;

import android.view.MotionEvent;

public class AngleDetector {

    private final AngleListener listener;

    private float startAngle, lastAngle;


    public AngleDetector(AngleListener listener) {
        this.listener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() > 1) {
                    startAngle = getPointerAngle(event);
                    lastAngle = startAngle;
                    listener.onGestureStarted(startAngle);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    float newAngle = getPointerAngle(event);
                    newAngle = removeAngleJump(newAngle, lastAngle);
                    lastAngle = newAngle;
                    listener.onAngleChanged(lastAngle);
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                if(event.getPointerCount() <= 2) {
                    listener.onGestureEnded(lastAngle);
                }
                return true;
            case MotionEvent.ACTION_UP:
                return false;
            case MotionEvent.ACTION_DOWN:
                return true;
        }
        return false;
    }

    private static float getPointerAngle(MotionEvent event) {
        return getPointerAngle(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    private static float getPointerAngle(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) (180 * Math.atan2(dy, dx) / Math.PI);
    }

    /*
     * Prevent the angle from jumping. If the new angle is more than 180° away from the last one, correct it so the difference is less
     */
    private static float removeAngleJump(float newAngle, float lastAngle) {
        float angleDifference = newAngle - lastAngle + 180;
        int jumpsRequired = (int) Math.floor(angleDifference / 360);

        return newAngle - 360 * jumpsRequired;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public float getLastAngle() {
        return lastAngle;
    }


    public interface AngleListener {

        void onGestureStarted(float startAngle);

        void onAngleChanged(float newAngle);

        void onGestureEnded(float endAngle);


        class Adapter implements AngleListener {
            @Override
            public void onGestureStarted(float startAngle) {
            }

            @Override
            public void onAngleChanged(float newAngle) {
            }

            @Override
            public void onGestureEnded(float endAngle) {
            }
        }
    }
}
