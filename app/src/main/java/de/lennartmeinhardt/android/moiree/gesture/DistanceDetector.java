package de.lennartmeinhardt.android.moiree.gesture;

import android.view.MotionEvent;

public class DistanceDetector {

    private final DistanceListener listener;

    private float startDistance, lastDistance;

    public DistanceDetector(DistanceListener listener) {
        this.listener = listener;
    }


    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() > 1) {
                    startDistance = getPointerDistance(event);
                    lastDistance = startDistance;
                    listener.onGestureStarted(startDistance);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    lastDistance = getPointerDistance(event);
                    listener.onDistanceChanged(lastDistance);
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() <= 2)
                    listener.onGestureEnded(lastDistance);
                return true;
            case MotionEvent.ACTION_UP:
                return false;
        }
        return false;
    }

    private static float getPointerDistance(MotionEvent event) {
        return getPointerDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    private static float getPointerDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(sq(x2 - x1) + sq(y2 - y1));
    }

    private static float sq(float f) {
        return f * f;
    }

    public float getStartDistance() {
        return startDistance;
    }

    public float getLastDistance() {
        return lastDistance;
    }


    public interface DistanceListener {

        void onGestureStarted(float startDistance);

        void onDistanceChanged(float newDistance);

        void onGestureEnded(float endDistance);


        class Adapter implements DistanceListener {
            @Override
            public void onGestureEnded(float endDistance) {
            }

            @Override
            public void onDistanceChanged(float newDistance) {
            }

            @Override
            public void onGestureStarted(float startDistance) {
            }
        }
    }
}
