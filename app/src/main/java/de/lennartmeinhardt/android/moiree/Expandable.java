package de.lennartmeinhardt.android.moiree;

import de.lennartmeinhardt.android.moiree.util.ExpandableView;

public interface Expandable {

    boolean isExpanded();

    void setExpanded(boolean expanded);

    void toggleExpanded();

    void addOnExpandedStateChangedListener(ExpandableView.OnExpandedStateChangedListener onExpandedStateChangedListener);

    void removeOnExpandedStateChangedListener(ExpandableView.OnExpandedStateChangedListener onExpandedStateChangedListener);


    interface OnExpandedStateChangedListener {

        void onExpandedStateChanged(Expandable expandable, boolean expanded);
    }
}
