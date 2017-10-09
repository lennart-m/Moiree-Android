package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.R;

public class ArrowExpandableHeader extends LinearLayout implements Expandable {

    private OnExpandedStateChangedListener onExpandedStateChangedListener;

    private final int collapsedIndicatorRotationDegrees;

    private boolean expanded;

    private final TextView titleView;
    private final ImageView expandedIndicatorView;


    public ArrowExpandableHeader(Context context) {
        this(context, null);
    }

    public ArrowExpandableHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowExpandableHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.arrow_expandable_header, this);

        setOrientation(HORIZONTAL);

        titleView = findViewById(R.id.header_title);
        expandedIndicatorView = findViewById(R.id.header_expanded_indicator);

        this.collapsedIndicatorRotationDegrees = context.getResources().getInteger(R.integer.indicator_arrow_collapsed_rotation);

        if(attrs != null)
            readAttributes(context, attrs);
        else
            // default expanded state is false, force an update to ensure the arrow is rotated correctly
            setExpandedInternal(false, /*force*/ true);
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArrowExpandableHeader, 0, 0);
        try {
            setExpandedInternal(a.getBoolean(R.styleable.Expandable_expanded, this.expanded), /*force*/ true);
            setTitle(a.getString(R.styleable.ArrowExpandableHeader_title));
        } finally {
            a.recycle();
        }
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        setExpandedInternal(expanded, /*force*/ false);
    }

    private void setExpandedInternal(boolean expanded, boolean forceUpdate) {
        if(this.expanded != expanded || forceUpdate) {
            this.expanded = expanded;
            if(onExpandedStateChangedListener != null)
                onExpandedStateChangedListener.onExpandedStateChanged(this, expanded);
            // fire listener before rotating to make it possible to run a transition
            expandedIndicatorView.setRotation(expanded ? 0 : collapsedIndicatorRotationDegrees);
        }
    }

    @Override
    public void toggleExpanded() {
        setExpanded(! isExpanded());
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

    @Override
    public void setOnExpandedStateChangedListener(OnExpandedStateChangedListener onExpandedStateChangedListener) {
        this.onExpandedStateChangedListener = onExpandedStateChangedListener;
    }


    @BindingAdapter("expandedAttrChanged")
    public static void setExpandedListener(ArrowExpandableHeader arrowExpandableHeader, final InverseBindingListener bindingListener) {
        Expandables.setExpandedListener(arrowExpandableHeader, bindingListener);
    }
}
