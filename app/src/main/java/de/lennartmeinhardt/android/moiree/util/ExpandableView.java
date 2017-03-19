package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import de.lennartmeinhardt.android.moiree.Expandable;
import de.lennartmeinhardt.android.moiree.R;


// TODO das ist, wie cardview, ein framelayout das ein view halten sollte. z.b. ein linearlayout. hat getheaderview und getxpandablecontentview. standard ist getheaderview.setonclicklistener(toggle)

public class ExpandableView extends LinearLayout implements Expandable {

    private static final String KEY_SUPER_STATE = "de.lennartmeinhardt.android.moiree:expandableView:superState";
    private static final String KEY_EXPANDED = "de.lennartmeinhardt.android.moiree:expandableView:expanded";

    private FrameLayout headerHolder;
    private FrameLayout expandableContentHolder;

    private boolean expanded = true;

    private OnExpandedStateChangedListener onExpandedStateChangedListener;


    public ExpandableView(Context context) {
        super(context);
        inflateExpandableRoot(context);
    }

    public ExpandableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateExpandableRoot(context);
        loadAttributes(context, attrs);
    }

    public ExpandableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateExpandableRoot(context);
        loadAttributes(context, attrs);
    }

    private void inflateExpandableRoot(Context context) {
        inflate(context, R.layout.expandable_view, this);
        setOrientation(LinearLayout.VERTICAL);
        setClipChildren(false);
        headerHolder = (FrameLayout) findViewById(R.id.expandable_view_header_holder);
        expandableContentHolder = (FrameLayout) findViewById(R.id.expandable_view_content_holder);
    }

    private void loadAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableView, 0, 0);
        try {
            int headerLayoutId = a.getResourceId(R.styleable.ExpandableView_headerLayout, 0);
            if(headerLayoutId != 0)
                LayoutInflater.from(context).inflate(headerLayoutId, headerHolder, true);

            int contentLayoutId = a.getResourceId(R.styleable.ExpandableView_contentLayout, 0);
            if(contentLayoutId != 0)
                LayoutInflater.from(context).inflate(contentLayoutId, expandableContentHolder, true);

            setExpandedInternal(a.getBoolean(R.styleable.ExpandableView_expanded, expanded), true);
        } finally {
            a.recycle();
        }
    }

    public void setExpandableContent(@LayoutRes int resId) {
        inflate(getContext(), resId, expandableContentHolder);
    }

    public void setExpandableContent(View view) {
        expandableContentHolder.removeAllViews();
        expandableContentHolder.addView(view);
    }

    public FrameLayout getExpandableContentHolder() {
        return expandableContentHolder;
    }

    public void setHeaderView(@LayoutRes int resId) {
        inflate(getContext(), resId, headerHolder);
    }

    public void setHeaderView(View view) {
        headerHolder.removeAllViews();
        headerHolder.addView(view);
    }

    public FrameLayout getHeaderHolder() {
        return headerHolder;
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
            expandableContentHolder.setVisibility(expanded ? View.VISIBLE : View.GONE);
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

    public void setOnExpandedStateChangedListener(OnExpandedStateChangedListener onExpandedStateChangedListener) {
        this.onExpandedStateChangedListener = onExpandedStateChangedListener;
    }

    public interface OnExpandedStateChangedListener {
        void onExpandedStateChanged(ExpandableView expandableView, boolean expanded);
    }
}
