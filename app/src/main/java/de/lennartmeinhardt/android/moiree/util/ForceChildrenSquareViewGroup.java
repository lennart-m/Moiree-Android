package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ForceChildrenSquareViewGroup extends ViewGroup {

    public ForceChildrenSquareViewGroup(Context context) {
        super(context);
    }

    public ForceChildrenSquareViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForceChildrenSquareViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        int size = Math.max(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        for(int i = 0; i < getChildCount(); i++)
            getChildAt(i).layout(x, y, x + size, y + size);
    }
}
