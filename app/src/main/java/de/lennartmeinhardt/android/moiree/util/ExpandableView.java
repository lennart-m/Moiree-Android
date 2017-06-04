package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import de.lennartmeinhardt.android.moiree.R;

public class ExpandableView extends FrameLayout implements Expandable {

    private static final String KEY_SUPER_STATE = "de.lennartmeinhardt.android.moiree:expandableView:superState";
    private static final String KEY_EXPANDED = "de.lennartmeinhardt.android.moiree:expandableView:expanded";

    private boolean expanded = true;

    private OnExpandedStateChangedListener onExpandedStateChangedListener;


    public ExpandableView(Context context) {
        super(context);
    }

    public ExpandableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(context, attrs);
    }

    public ExpandableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttributes(context, attrs);
    }

    public View findHeaderView() {
        return findViewById(R.id.expandable_view_header);
    }
    public View findContentView() {
        return findViewById(R.id.expandable_view_content);
    }

    private void loadAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableView, 0, 0);
        try {
            setExpandedInternal(a.getBoolean(R.styleable.Expandable_expanded, expanded), true);
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
        setExpandedInternal(expanded, false);
    }
    @Override
    public void toggleExpanded() {
        setExpanded(! expanded);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle outState = new Bundle();
        Parcelable superState = super.onSaveInstanceState();
        if(superState != null)
            outState.putParcelable(KEY_SUPER_STATE, superState);
        outState.putBoolean(KEY_EXPANDED, expanded);
        return outState;
    }

    private void setExpandedInternal(boolean expanded, boolean forceUpdate) {
        if(this.expanded != expanded || forceUpdate) {
            this.expanded = expanded;
            View contentView = findContentView();
            if(contentView != null)
                contentView.setVisibility(expanded ? View.VISIBLE : View.GONE);
            if(onExpandedStateChangedListener != null)
                onExpandedStateChangedListener.onExpandedStateChanged(this, expanded);
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle instanceState = (Bundle) state;
        Parcelable superState = instanceState.getParcelable(KEY_SUPER_STATE);
        super.onRestoreInstanceState(superState);
        boolean expanded = instanceState.getBoolean(KEY_EXPANDED);
        setExpandedInternal(expanded, true);
    }

    @Override
    public void setOnExpandedStateChangedListener(OnExpandedStateChangedListener onExpandedStateChangedListener) {
        this.onExpandedStateChangedListener = onExpandedStateChangedListener;
    }

    @BindingAdapter("expandedAttrChanged")
    public static void setExpandedListener(ExpandableView expandableView, final InverseBindingListener bindingListener) {
        Expandables.setExpandedListener(expandableView, bindingListener);
    }
}
