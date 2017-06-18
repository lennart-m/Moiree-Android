package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import de.lennartmeinhardt.android.moiree.R;

public class SeekPane extends View {

    private static final int[] STATE_INPUT_ACTIVE = {R.attr.state_input_active};

    private int maxX, maxY;
    private int positionX, positionY;
    private int stepX, stepY;

    private Drawable thumbDrawable;
    private int thumbOffsetHorizontal, thumbOffsetVertical;

    private boolean inputActive;

    private OnPosition2DChangeListener changeListener;


    public SeekPane(Context context) {
        super(context);
        initializeSeekPane(context);
    }

    public SeekPane(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeSeekPane(context);
        loadFromAttrs(context, attrs);
    }

    public SeekPane(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeSeekPane(context);
        loadFromAttrs(context, attrs);
    }


    private void initializeSeekPane(Context context) {
        maxX = 100;
        maxY = 100;
        positionX = 0;
        positionY = 0;
        stepX = 5;
        stepY = 5;
        setThumbDrawable(ContextCompat.getDrawable(context, R.drawable.thumb_circle));
        setClickable(true);
        setFocusable(true);
    }

    private void loadFromAttrs(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.SeekPane, 0, 0);
        try {
            maxX = a.getInteger(R.styleable.SeekPane_maxX, maxX);
            maxY = a.getInteger(R.styleable.SeekPane_maxY, maxY);
            int posX = a.getInteger(R.styleable.SeekPane_posX, positionX);
            int posY = a.getInteger(R.styleable.SeekPane_posY, positionY);
            setPositionX(posX);
            setPositionY(posY);

            if(a.hasValue(R.styleable.SeekPane_thumbDrawable)) {
                setThumbDrawable(a.getDrawable(R.styleable.SeekPane_thumbDrawable));
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        if(inputActive) {
            int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
            mergeDrawableStates(drawableState, STATE_INPUT_ACTIVE);
            return drawableState;
        }
        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (thumbDrawable != null && thumbDrawable.isStateful() && thumbDrawable.setState(getDrawableState())) {
            invalidateDrawable(thumbDrawable);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updateThumbPos();
    }

    public void setThumbDrawable(Drawable thumbDrawable) {
        if(this.thumbDrawable != thumbDrawable) {
            this.thumbDrawable = thumbDrawable;

            if(this.thumbDrawable != null) {
                thumbOffsetHorizontal = thumbDrawable.getIntrinsicWidth() / 2;
                thumbOffsetVertical = thumbDrawable.getIntrinsicHeight() / 2;
                updateThumbPos();
            }
        }
    }

    private void updateThumbPos() {
        if (thumbDrawable == null)
            return;

        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int thumbWidth = thumbDrawable.getIntrinsicWidth();
        final int thumbHeight = thumbDrawable.getIntrinsicHeight();
        availableWidth -= thumbWidth;
        availableHeight -= thumbHeight;

        availableWidth += thumbOffsetHorizontal * 2;
        availableHeight += thumbOffsetVertical * 2;

        float relativeX = maxX == 0 ? .5f : (1f * positionX) / maxX;
        float relativeY = maxY == 0 ? .5f : (1f * positionY) / maxY;
        relativeY = 1 - relativeY;
        final int left = (int) (relativeX * availableWidth + .5f);
        final int right = left + thumbWidth;

        final int top = (int) (relativeY * availableHeight + .5f);
        final int bottom = top + thumbHeight;

        final Drawable background = getBackground();
        if (background != null) {
            final int offsetX = getPaddingLeft() - thumbOffsetHorizontal;
            final int offsetY = getPaddingTop() - thumbOffsetVertical;
            DrawableCompat.setHotspotBounds(background, left + offsetX, top + offsetY,
                    right + offsetX, bottom + offsetY);
        }

        thumbDrawable.setBounds(left, top, right, bottom);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (thumbDrawable != null) {
            canvas.save();
            canvas.translate(getPaddingLeft() - thumbOffsetHorizontal, getPaddingTop() - thumbOffsetVertical);
            thumbDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (! isEnabled())
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTrackingTouch();
                attemptClaimDrag();
                trackTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                trackTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                trackTouchEvent(event);
//                break;
            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                break;
        }

        return true;
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void onStartTrackingTouch() {
        setInputActive(true);
        if (changeListener != null)
            changeListener.onStartTrackingTouch(this);
    }

    private void setInputActive(boolean active) {
        if(this.inputActive != active) {
            this.inputActive = active;
            refreshDrawableState();
            invalidate();
        }
    }
    private void toggleInputActive() {
        setInputActive(! inputActive);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(! isEnabled())
            return false;

        // if the "okay" button is clicked toggle input mode
        if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            toggleInputActive();
            return true;
        }

        // if input is active handle the arrow keys (if they point in a direction where the value is not at the boundary)
        if(inputActive) {
            switch(keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(positionY < maxY) {
                        stepY(stepY, true);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(positionY > 0) {
                        stepY(-stepY, true);
                    }
                    return true;
                // TODO RTL
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(positionX < maxX) {
                        stepX(stepX, true);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(positionX > 0) {
                        stepX(-stepX, true);
                    }
                    return true;
            }
        }

        // nothing particular was clicked, or input mode isn't active. proceed with focus etc
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if(! gainFocus)
            setInputActive(false);
    }

    private void onStopTrackingTouch() {
        if (changeListener != null)
            changeListener.onStopTrackingTouch(this);
        setInputActive(false);
    }


    private void trackTouchEvent(MotionEvent event) {
        final int x = Math.round(event.getX());
        final int width = getWidth();
        final int availableWidth = width - getPaddingLeft() - getPaddingRight();

        final float relativeX;
        if (x < getPaddingLeft()) {
            relativeX = 0.0f;
        } else if (x > width - getPaddingRight()) {
            relativeX = 1.0f;
        } else {
            relativeX = (x - getPaddingLeft()) / (float) availableWidth;
        }

        final int y = Math.round(event.getY());
        final int height = getHeight();
        final int availableHeight = height - getPaddingTop() - getPaddingBottom();

        final float relativeY;
        if (y < getPaddingTop()) {
            relativeY = 0.0f;
        } else if (y > height - getPaddingBottom()) {
            relativeY = 1.0f;
        } else {
            relativeY = (y - getPaddingTop()) / (float) availableHeight;
        }

        setHotspot(x, y);
        int positionX = Math.round(relativeX * maxX);
        int positionY = Math.round((1 - relativeY) * maxY);
        internalSetPositionX(positionX, true);
        internalSetPositionY(positionY, true);
    }

    private void internalSetPositionX(int positionX, boolean fromUser) {
        positionX = trimX(positionX);
        if (this.positionX != positionX) {
            this.positionX = positionX;
            updateThumbPos();
            if (changeListener != null)
                changeListener.onPositionXChanged(this, positionX, fromUser);
        }
    }

    private void internalSetPositionY(int positionY, boolean fromUser) {
        positionY = trimY(positionY);
        if (this.positionY != positionY) {
            this.positionY = positionY;
            updateThumbPos();
            if (changeListener != null)
                changeListener.onPositionYChanged(this, positionY, fromUser);
        }
    }

    private void setHotspot(float x, float y) {
        final Drawable bg = getBackground();
        if (bg != null) {
            DrawableCompat.setHotspot(bg, x, y);
        }
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        internalSetPositionX(positionX, false);
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        internalSetPositionY(positionY, false);
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
        internalSetPositionX(positionX, false);
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
        internalSetPositionY(positionY, false);
    }

    private static int trim(int val, int max) {
        return Math.max(0, Math.min(max, val));
    }

    private int trimX(int val) {
        return trim(val, maxX);
    }

    private int trimY(int val) {
        return trim(val, maxY);
    }

    public int getStepX() {
        return stepX;
    }
    public void setStepX(int stepX) {
        this.stepX = stepX;
    }

    public int getStepY() {
        return stepY;
    }
    public void setStepY(int stepY) {
        this.stepY = stepY;
    }

    private void stepX(int stepSize, boolean fromUser) {
        internalSetPositionX(getPositionX() + stepSize, fromUser);
    }
    private void stepY(int stepSize, boolean fromUser) {
        internalSetPositionY(getPositionY() + stepSize, fromUser);
    }

    public void setOnPosition2DChangeListener(OnPosition2DChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public boolean isInputActive() {
        return inputActive;
    }


    public interface OnPosition2DChangeListener {
        void onPositionXChanged(SeekPane seekPane, int positionX, boolean fromUser);

        void onPositionYChanged(SeekPane seekPane, int positionY, boolean fromUser);

        void onStartTrackingTouch(SeekPane seekPane);

        void onStopTrackingTouch(SeekPane seekPane);


        class Adapter implements OnPosition2DChangeListener {
            @Override
            public void onPositionXChanged(SeekPane seekPane, int positionX, boolean fromUser) {
            }

            @Override
            public void onPositionYChanged(SeekPane seekPane, int positionY, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekPane seekPane) {
            }

            @Override
            public void onStopTrackingTouch(SeekPane seekPane) {
            }
        }
    }
}
