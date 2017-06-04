package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.R;

public class DrawableProgressPreviewHeader extends LinearLayout {

    private final TextView titleView;
    private final ImageView previewImageView;
    private final ProgressBar busyIndicator;


    public DrawableProgressPreviewHeader(Context context) {
        this(context, null);
    }

    public DrawableProgressPreviewHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableProgressPreviewHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.header_with_preview_and_progress, this);

        this.titleView = (TextView) findViewById(R.id.header_title);
        this.previewImageView = (ImageView) findViewById(R.id.header_preview);
        this.busyIndicator = (ProgressBar) findViewById(R.id.header_progress);

        setOrientation(HORIZONTAL);

        if(attrs != null)
            readAttributes(context, attrs);
        else
            setBusy(false);
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawableProgressPreviewHeader);
        try {
            setTitle(a.getString(R.styleable.DrawableProgressPreviewHeader_title));
            setDrawable(a.getDrawable(R.styleable.DrawableProgressPreviewHeader_drawable));
            setBusy(a.getBoolean(R.styleable.DrawableProgressPreviewHeader_busy, false));
        } finally {
            a.recycle();
        }
    }

    public void setDrawable(Drawable drawable) {
        previewImageView.setImageDrawable(drawable);
    }
    public void setDrawableResource(@DrawableRes int drawableId) {
        previewImageView.setImageResource(drawableId);
    }
    public Drawable getDrawable() {
        return previewImageView.getBackground();
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }
    public void setTitle(@StringRes int titleId) {
        titleView.setText(titleId);
    }
    public CharSequence getTitle() {
        return titleView.getText();
    }

    public void setBusy(boolean busy) {
        busyIndicator.setVisibility(busy ? VISIBLE : INVISIBLE);
    }
    public boolean isBusy() {
        return busyIndicator.getVisibility() == VISIBLE;
    }

    public ImageView getPreviewImageView() {
        return previewImageView;
    }
}
