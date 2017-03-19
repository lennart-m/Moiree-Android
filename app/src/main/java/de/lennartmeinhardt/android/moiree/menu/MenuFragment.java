package de.lennartmeinhardt.android.moiree.menu;

import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.MainActivity;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.transition.UiChangeTransition;

public abstract class MenuFragment extends Fragment {

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected void openSubMenu(Fragment fragment, String tag) {
        getMainActivity().openSubMenu(fragment, tag); // TODO
    }

    protected void beginMenuBoundsTransition() {
        Transition transition = createMenuBoundsTransition();
        beginMenuBoundsTransition(transition);
    }

    protected void beginMenuBoundsTransition(Transition transition) {
        ViewGroup root = (ViewGroup) getActivity().findViewById(R.id.menu_holder);
        TransitionManager.beginDelayedTransition(root, transition);
        // TODO das kann sonst auch in ein MenuTransitionStarter-Objekt
    }

    protected static Transition createMenuBoundsTransition() {
        return new UiChangeTransition();
    }
}
