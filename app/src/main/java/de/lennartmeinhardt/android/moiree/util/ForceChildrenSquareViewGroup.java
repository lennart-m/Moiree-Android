package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.R;

public class ForceChildrenSquareViewGroup extends ViewGroup {

    private boolean cropChildren = false;


    public ForceChildrenSquareViewGroup(Context context) {
        super(context);
    }

    public ForceChildrenSquareViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

        readAttributes(context, attrs);
    }

    public ForceChildrenSquareViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        readAttributes(context, attrs);
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForceChildrenSquareViewGroup, 0, 0);
        try {
            cropChildren = a.getBoolean(R.styleable.ForceChildrenSquareViewGroup_cropChildren, cropChildren);
        } finally {
            a.recycle();
        }
    }


    public boolean isCropChildren() {
        return cropChildren;
    }

    public void setCropChildren(boolean cropChildren) {
        if(this.cropChildren != cropChildren) {
            this.cropChildren = cropChildren;
            requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        int size;
        if(cropChildren)
            size = Math.max(width, height);
        else
            size = Math.min(width, height);

        int x = (width - size) / 2;
        int y = (height - size) / 2;

        for(int i = 0; i < getChildCount(); i++)
            getChildAt(i).layout(x, y, x + size, y + size);
    }
}
