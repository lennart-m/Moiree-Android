package de.lennartmeinhardt.android.moiree;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.gesture.AngleDetector;
import de.lennartmeinhardt.android.moiree.gesture.DistanceDetector;
import de.lennartmeinhardt.android.moiree.gesture.PositionDetector;

public class TouchHandlerFragment extends Fragment {

    private SharedPreferences preferences;

    private MoireeTransformation moireeTransformation;
    private MoireeInputMethods moireeInputMethods;
    private boolean inputEnabled = true;

    private int statusBarHeight;


    private final DistanceDetector commonScalingDetector = new DistanceDetector(new DistanceDetector.DistanceListener.Adapter() {
        private float startDistance;
        private float startCommonScaling;

        @Override
        public void onGestureStarted(float startDistance) {
            this.startDistance = startDistance;
            startCommonScaling = moireeTransformation.getCommonScaling();
        }

        @Override
        public void onDistanceChanged(float newDistance) {
            if(startDistance <= 0 || !moireeInputMethods.isScalingInputEnabled())
                return;

            float newCommonScaling = getNewScaling(newDistance, startDistance, startCommonScaling);
            moireeTransformation.setCommonScaling(newCommonScaling);
        }
    });

    private final AngleDetector rotationDetector = new AngleDetector(new AngleDetector.AngleListener.Adapter() {
        private float startAngle;
        private float startRotation;

        @Override
        public void onGestureStarted(float startAngle) {
            this.startAngle = startAngle;
            startRotation = moireeTransformation.getRotation();
        }

        @Override
        public void onAngleChanged(float newAngle) {
            if(moireeInputMethods.isRotationInputEnabled()) {
                float angleDifference = newAngle - startAngle;
                float newRot = startRotation + moireeInputMethods.getRotationSensitivity() * angleDifference;
                moireeTransformation.setRotation(newRot);
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
            startTranslationX = moireeTransformation.getTranslationX();
            startTranslationY = moireeTransformation.getTranslationY();
        }

        @Override
        public void onPositionChanged(float newX, float newY) {
            if(moireeInputMethods.isTranslationInputEnabled()) {
                float newTranslationX = startTranslationX + moireeInputMethods.getTranslationSensitivity() * (newX - startX);
                moireeTransformation.setTranslationX(newTranslationX);
                // reverse y translation because bottom > top in android's coordinate system
                float newTranslationY = startTranslationY - moireeInputMethods.getTranslationSensitivity() * (newY - startY);
                moireeTransformation.setTranslationY(newTranslationY);
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
            float rotationAngle = moireeTransformation.getRotation();
            float scalingDirectionAngle = pointersAngle - rotationAngle;
            shallScaleHorizontally = ! isDirectionVerticalOnly(scalingDirectionAngle);
            shallScaleVertically = ! isDirectionHorizontalOnly(scalingDirectionAngle);
            this.startScalingX = moireeTransformation.getScalingX();
            this.startScalingY = moireeTransformation.getScalingY();
        }

        @Override
        public void onDistanceChanged(float newDistance) {
            if(startDistance <= 0 || ! moireeInputMethods.isScalingInputEnabled())
                return;

            if(shallScaleHorizontally) {
                float newScalingX = getNewScaling(newDistance, startDistance, startScalingX);
                moireeTransformation.setScalingX(newScalingX);
            }
            if(shallScaleVertically) {
                float newScalingY = getNewScaling(newDistance, startDistance, startScalingY);
                moireeTransformation.setScalingY(newScalingY);
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

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId != 0)
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        moireeInputMethods = new MoireeInputMethods(getResources());
        moireeInputMethods.loadFromPreferences(preferences);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View touchHandlerView = inflater.inflate(R.layout.fragment_touch_handler, container, false);
        initializeTouchListener(touchHandlerView);

        // TODO alternative: menuFragment übernimmt die ganze klick geschichte. der menuHolder wird dann klickbar (gucken ob er alles abfängt). das view hier ist nicht fokussierbar und die im moireeViewFragment sind es auch nicht

        return touchHandlerView;
    }

    private void initializeTouchListener(View touchHandlerView) {
        final GestureDetector tapDetector = new GestureDetector(touchHandlerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                ((MainActivity) getActivity()).toggleMenuShowing();

                return true;
            }
        });

        touchHandlerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(tapDetector.onTouchEvent(event))
                    return false;

                if(event.getY() <= statusBarHeight)
                    return false;

                boolean shouldReturnTrue = false;

                // handle rotation
                shouldReturnTrue |= rotationDetector.onTouchEvent(event);
                // handle scaling
                if(moireeTransformation.isUseCommonScaling())
                    shouldReturnTrue |= commonScalingDetector.onTouchEvent(event);
                else
                    shouldReturnTrue |= xyScalingDetector.onTouchEvent(event);

                // handle translation
                shouldReturnTrue |= translationDetector.onTouchEvent(event);

                return shouldReturnTrue;
            }
        });
    }

    private float getNewScaling(float distance, float startDistance, float startScaling) {
        float distanceQuotient = distance / startDistance;
        float sensitivity = moireeInputMethods.getScalingSensitivity();
        return (float) (startScaling * Math.pow(distanceQuotient, sensitivity));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.moireeTransformation = ((MoireeTransformationHolder) getActivity()).getMoireeTransformation();
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

    public boolean isInputEnabled() {
        return inputEnabled;
    }
    public void setInputEnabled(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
    }
}
