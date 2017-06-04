package de.lennartmeinhardt.android.moiree.util;

import android.databinding.InverseBindingListener;

public class Expandables {

    public static void setExpandedListener(Expandable expandable, final InverseBindingListener bindingListener) {
        if (bindingListener == null)
            expandable.setOnExpandedStateChangedListener(null);
        else
            expandable.setOnExpandedStateChangedListener(new Expandable.OnExpandedStateChangedListener() {
                @Override
                public void onExpandedStateChanged(Expandable expandable, boolean expanded) {
                    bindingListener.onChange();
                }
            });
    }

    /**
     * Set an expandable listener to one {@link Expandable}, that will inform the listener as if a wrapping expandable had updated.
     * Example: a fragment is {@link Expandable} and holds an {@link ExpandableView} to which all {@link Expandable} method calls are forwarded.
     * A change in the expanded state will notify listeners by passing the {@link ExpandableView} as source argument.
     * This method add the listener such that the fragment is used as source, instead of the expandable view.
     *
     * @param wrappingExpandable             the expandable wrapping the other
     * @param wrappedExpandable              the expandable being wrapped
     * @param onExpandedStateChangedListener the listener to set
     */
    public static void setExpandedListenerForWrappedExpandable(
            final Expandable wrappingExpandable,
            Expandable wrappedExpandable,
            final Expandable.OnExpandedStateChangedListener onExpandedStateChangedListener
    ) {
        if (onExpandedStateChangedListener == null)
            wrappedExpandable.setOnExpandedStateChangedListener(null);
        else
            wrappedExpandable.setOnExpandedStateChangedListener(new Expandable.OnExpandedStateChangedListener() {
                @Override
                public void onExpandedStateChanged(Expandable expandable, boolean expanded) {
                    onExpandedStateChangedListener.onExpandedStateChanged(wrappingExpandable, expanded);
                }
            });
    }

    private Expandables() {
    }
}
