package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.R;

public class ColorPreviewHeader extends LinearLayout {

    private final TextView titleView;
    private final ImageView previewImageView;


    public ColorPreviewHeader(Context context) {
        this(context, null);
    }

    public ColorPreviewHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPreviewHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.header_with_preview, this);
        setOrientation(HORIZONTAL);

        this.titleView = findViewById(R.id.header_title);
        this.previewImageView = findViewById(R.id.header_preview);

        if(attrs != null)
            readAttributes(context, attrs);
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPreviewHeader);
        try {
            setTitle(a.getString(R.styleable.ColorPreviewHeader_title));
            setColor(a.getColor(R.styleable.ColorPreviewHeader_color, Color.TRANSPARENT));
        } finally {
            a.recycle();
        }
    }

    public void setColor(int argb) {
        previewImageView.setBackgroundColor(argb);
    }
    public int getColor() {
        Drawable drawable = previewImageView.getBackground();
        if(drawable instanceof ColorDrawable)
            return ((ColorDrawable) drawable).getColor();
        else
            return 0;
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
}
