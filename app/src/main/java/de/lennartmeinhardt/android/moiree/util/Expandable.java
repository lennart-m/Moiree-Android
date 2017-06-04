package de.lennartmeinhardt.android.moiree.util;

import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;

@InverseBindingMethods(
        @InverseBindingMethod(type = Expandable.class, attribute = "expanded")
)
public interface Expandable {

    boolean isExpanded();

    void setExpanded(boolean expanded);

    void toggleExpanded();

    void setOnExpandedStateChangedListener(ExpandableView.OnExpandedStateChangedListener onExpandedStateChangedListener);


    interface OnExpandedStateChangedListener {

        void onExpandedStateChanged(Expandable expandable, boolean expanded);
    }
}
