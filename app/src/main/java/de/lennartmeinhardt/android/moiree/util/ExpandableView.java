package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import de.lennartmeinhardt.android.moiree.Expandable;
import de.lennartmeinhardt.android.moiree.R;

public class ExpandableView extends FrameLayout implements Expandable {

    private static final String KEY_SUPER_STATE = "de.lennartmeinhardt.android.moiree:expandableView:superState";
    private static final String KEY_EXPANDED = "de.lennartmeinhardt.android.moiree:expandableView:expanded";

    private boolean expanded = true;

    private boolean keepHeaderReference = false;
    private boolean keepContentReference = false;

    private OnExpandedStateChangedListener onExpandedStateChangedListener;

    private View headerView;
    private View contentView;


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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if(keepHeaderReference)
            headerView = findHeaderView();

        if(keepContentReference)
            contentView = findContentView();
    }

    private View findHeaderView() {
        return findViewById(R.id.expandable_view_header);
    }

    public View getHeaderView() {
        if(keepHeaderReference)
            return headerView;
        else
            return findHeaderView();
    }

    private View findContentView() {
        return findViewById(R.id.expandable_view_content);
    }

    public View getContentView() {
        if(keepContentReference)
            return contentView;
        else
            return findContentView();
    }

    private void loadAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableView, 0, 0);
        try {
            setExpandedInternal(a.getBoolean(R.styleable.ExpandableView_expanded, expanded), true);
            keepHeaderReference = a.getBoolean(R.styleable.ExpandableView_keepHeaderReference, keepHeaderReference);
            keepContentReference = a.getBoolean(R.styleable.ExpandableView_keepContentReference, keepContentReference);
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
            View contentView = getContentView();
            if(contentView != null)
                contentView.setVisibility(expanded ? View.VISIBLE : View.GONE);
            if(onExpandedStateChangedListener != null)
                onExpandedStateChangedListener.onExpandedStateChanged(this, expanded);
        }
    }

    public boolean isKeepHeaderReference() {
        return keepHeaderReference;
    }
    public void setKeepHeaderReference(boolean keepHeaderReference) {
        this.keepHeaderReference = keepHeaderReference;
        if(keepHeaderReference)
            headerView = findHeaderView();
        else
            headerView = null;
    }

    public boolean isKeepContentReference() {
        return keepContentReference;
    }
    public void setKeepContentReference(boolean keepContentReference) {
        this.keepContentReference = keepContentReference;
        if(keepContentReference)
            contentView = findContentView();
        else
            contentView = null;
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
