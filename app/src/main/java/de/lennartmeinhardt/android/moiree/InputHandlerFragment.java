package de.lennartmeinhardt.android.moiree;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.gesture.AngleDetector;
import de.lennartmeinhardt.android.moiree.gesture.DistanceDetector;
import de.lennartmeinhardt.android.moiree.gesture.PositionDetector;
import de.lennartmeinhardt.android.moiree.menu.MenuHolder;

public class InputHandlerFragment extends Fragment {

    private MenuHolder menuHolder;

    private SharedPreferences preferences;

    private MoireeTransformation moireeTransformation;
    private MoireeInputMethods moireeInputMethods;


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

        @Override
        public void onGestureStarted(float startX, float startY) {
            this.startX = startX;
            this.startY = startY;
            startTranslationX = moireeTransformation.translationX.get();
            startTranslationY = moireeTransformation.translationY.get();
        }

        @Override
        public void onPositionChanged(float newX, float newY) {
            if(moireeInputMethods.translationInputEnabled.get()) {
                float newTranslationX = startTranslationX + moireeInputMethods.translationSensitivity.get() * (newX - startX);
                moireeTransformation.translationX.set(newTranslationX);
                // reverse y translation because bottom > top in android's coordinate system
                float newTranslationY = startTranslationY - moireeInputMethods.translationSensitivity.get() * (newY - startY);
                moireeTransformation.translationY.set(newTranslationY);
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
            return mMod(sixteenthPart, 16);
        }

        private int mMod(int value, int mod) {
            return (mod + value % mod) % mod;
        }
    });


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int defaultSensitivityPercents = getResources().getInteger(R.integer.sensitivity_default_percents);
        moireeInputMethods = new MoireeInputMethods(true, defaultSensitivityPercents / 100f);
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
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        moireeInputMethods.storeToPreferences(preferencesEditor);
        preferencesEditor.apply();
    }

    public MoireeInputMethods getMoireeInputMethods() {
        return moireeInputMethods;
    }
}
