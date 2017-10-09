package de.lennartmeinhardt.android.moiree.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.transition.SimpleChangeTransform;
import de.lennartmeinhardt.android.moiree.transition.UiChangeTransition;

/**
 * A fragment that is (part of) a menu. Provdes utility functions for menu-transitions.
 */
public abstract class MenuFragment extends Fragment {

    protected SharedPreferences preferences;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get the first parent (or parents' parent) {@link MenuHolderFragment} to which this fragment is attached.
     *
     * @return an instance of some parent {@link MenuHolderFragment}, or null.
     */
    protected MenuHolderFragment getMenuHolder() {
        Fragment current = this;
        while(current != null) {
            if(current instanceof MenuHolderFragment)
                return (MenuHolderFragment) current;
            current = current.getParentFragment();
        }
        return null;
    }

    /**
     * Start a transition that animates a layout change in the menu.
     */
    protected void beginMenuBoundsTransition() {
        Transition transition = createMenuBoundsTransition();
        beginMenuTransition(transition);
    }

    /**
     * Start a transition for the menu.
     *
     * @param transition the transition to start
     */
    protected void beginMenuTransition(Transition transition) {
        ViewGroup root = getMenuHolder().getMenuRoot();
        TransitionManager.beginDelayedTransition(root, transition);
    }

    /**
     * Create the default layout change transition.
     *
     * @return the menu layout change transition
     */
    protected static Transition createMenuBoundsTransition() {
        return new UiChangeTransition();
    }

    /**
     * Create a transition that animates menu layout changes, and rotates a header expanded indicator.
     *
     * @return transition for menu layout change and header expanded indicator rotation.
     */
    protected static Transition createMenuBoundsAndHeaderIndicatorTransition() {
        return new TransitionSet()
                .addTransition(createMenuBoundsTransition())
                .addTransition(new SimpleChangeTransform().addTarget(R.id.header_expanded_indicator))
                .setOrdering(TransitionSet.ORDERING_TOGETHER);
    }
}
