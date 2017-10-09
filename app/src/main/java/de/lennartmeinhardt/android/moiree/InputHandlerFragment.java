package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.gesture.AngleDetector;
import de.lennartmeinhardt.android.moiree.gesture.DistanceDetector;
import de.lennartmeinhardt.android.moiree.gesture.PositionDetector;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;
import de.lennartmeinhardt.android.moiree.menu.MenuHolder;

public class InputHandlerFragment extends MenuFragment {

    private MenuHolder menuHolder;

    private MoireeTransformation moireeTransformation;
    private final MoireeInputMethods moireeInputMethods = new MoireeInputMethods();


    private final DistanceDetector commonScalingDetector = new DistanceDetector(new DistanceDetector.DistanceListener.Adapter() {
        private float startDistance;
        private float startCommonScaling;

        @Override
        public void onGestureStarted(float startDistance) {
            this.startDistance = startDistance;
            startCommonScaling = moireeTransformation.commonScaling.get();
        }

        @Override
        public void onDistanceChanged(float newDistance) {
            if(startDistance <= 0 || !moireeInputMethods.scalingInputEnabled.get())
                return;

            float newCommonScaling = getNewScaling(newDistance, startDistance, startCommonScaling);
            moireeTransformation.commonScaling.set(newCommonScaling);
        }
    });

    private final AngleDetector rotationDetector = new AngleDetector(new AngleDetector.AngleListener.Adapter() {
        private float startAngle;
        private float startRotation;

        @Override
        public void onGestureStarted(float startAngle) {
            this.startAngle = startAngle;
            startRotation = moireeTransformation.rotation.get();
        }

        @Override
        public void onAngleChanged(float newAngle) {
            if(moireeInputMethods.rotationInputEnabled.get()) {
                float angleDifference = newAngle - startAngle;
                float newRot = startRotation + moireeInputMethods.rotationSensitivity.get() * angleDifference;
                moireeTransformation.rotation.set(newRot);
            }
        }
    });

    private final PositionDetector translationDetector = new PositionDetector(new PositionDetector.PositionListener.Adapter() {
        private float startX, startY;
        private float startTranslationX, startTranslationY;
        private boolean thresholdReached;

        @Override
        public void onGestureStarted(float startX, float startY) {
            this.startX = startX;
            this.startY = startY;
            thresholdReached = false;
        }

        @Override
        public void onPositionChanged(float newX, float newY) {
            if(moireeInputMethods.translationInputEnabled.get()) {
                float dx = newX - startX;
                float dy = newY - startY;
                double distanceToStart = Math.sqrt(dx * dx + dy * dy);

                if(distanceToStart >= moireeInputMethods.translationDistanceThreshold.get()
                        && ! thresholdReached) {
                    thresholdReached = true;
                    startX = newX;
                    startY = newY;
                    startTranslationX = moireeTransformation.translationX.get();
                    startTranslationY = moireeTransformation.translationY.get();
                } else if(thresholdReached) {
                    float newTranslationX = startTranslationX + moireeInputMethods.translationSensitivity.get() * dx;
                    moireeTransformation.translationX.set(newTranslationX);
                    // reverse y translation because bottom > top in android's coordinate system
                    float newTranslationY = startTranslationY - moireeInputMethods.translationSensitivity.get() * dy;
                    moireeTransformation.translationY.set(newTranslationY);
                }
            }
        }
    });

    private final DistanceDetector xyScalingDetector = new DistanceDetector(new DistanceDetector.DistanceListener.Adapter() {
        private float startDistance;
        private float startScalingX, startScalingY;

        private boolean shallScaleHorizontally;
        private boolean shallScaleVertically;

        @Override
        public void onGestureStarted(float startDistance) {
            this.startDistance = startDistance;
            float pointersAngle = rotationDetector.getStartAngle();
            float rotationAngle = moireeTransformation.rotation.get();
            float scalingDirectionAngle = pointersAngle - rotationAngle;
            shallScaleHorizontally = ! isDirectionVerticalOnly(scalingDirectionAngle);
            shallScaleVertically = ! isDirectionHorizontalOnly(scalingDirectionAngle);
            this.startScalingX = moireeTransformation.scalingX.get();
            this.startScalingY = moireeTransformation.scalingY.get();
        }

        @Override
        public void onDistanceChanged(float newDistance) {
            if(startDistance <= 0 || ! moireeInputMethods.scalingInputEnabled.get())
                return;

            if(shallScaleHorizontally) {
                float newScalingX = getNewScaling(newDistance, startDistance, startScalingX);
                moireeTransformation.scalingX.set(newScalingX);
            }
            if(shallScaleVertically) {
                float newScalingY = getNewScaling(newDistance, startDistance, startScalingY);
                moireeTransformation.scalingY.set(newScalingY);
            }
        }

        // check if angle is in on of the {0, 7, 8, 15} 16th of the circle
        private boolean isDirectionHorizontalOnly(float degrees) {
            int sixteenthPart = sixteenthPartOfCircle(degrees);
            return sixteenthPart == 0 || sixteenthPart == 7 || sixteenthPart == 8 || sixteenthPart == 15;
        }

        private boolean isDirectionVerticalOnly(float degrees) {
            int sixteenthPart = sixteenthPartOfCircle(degrees);
            return sixteenthPart == 3 || sixteenthPart == 4 || sixteenthPart == 11 || sixteenthPart == 12;
        }

        private int sixteenthPartOfCircle(float degrees) {
            int sixteenthPart = (int) Math.floor(16 * degrees / 360);
            return mMod16(sixteenthPart);
        }

        private int mMod16(int value) {
            return (16 + value % 16) % 16;
        }
    });


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moireeInputMethods.loadFromResources(getResources());
        moireeInputMethods.loadFromPreferences(preferences);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_touch_handler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final GestureDetector tapDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                menuHolder.toggleMenuShowing();

                return true;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(tapDetector.onTouchEvent(event))
                    return false;

                // handle rotation
                rotationDetector.onTouchEvent(event);
                // handle scaling
                if(moireeTransformation.useCommonScaling.get())
                    commonScalingDetector.onTouchEvent(event);
                else
                    xyScalingDetector.onTouchEvent(event);

                // handle translation
                translationDetector.onTouchEvent(event);

                return true;
            }
        });
    }

    public boolean onKeyDown(int keyCode) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_MENU:
                menuHolder.toggleMenuShowing();
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                menuHolder.showMenuIfHidden();
                return true;
            default:
                return false;
        }
    }

    private float getNewScaling(float distance, float startDistance, float startScaling) {
        float distanceQuotient = distance / startDistance;
        float sensitivity = moireeInputMethods.scalingSensitivity.get();
        return (float) (startScaling * Math.pow(distanceQuotient, sensitivity));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.moireeTransformation = ((MoireeTransformationHolder) getActivity()).getMoireeTransformation();
        this.menuHolder = (MenuHolder) getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        moireeInputMethods.storeToPreferences(preferencesEditor);
        preferencesEditor.apply();
    }

    public MoireeInputMethods getMoireeInputMethods() {
        return moireeInputMethods;
    }
}
