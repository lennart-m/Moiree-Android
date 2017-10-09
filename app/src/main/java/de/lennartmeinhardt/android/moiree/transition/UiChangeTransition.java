package de.lennartmeinhardt.android.moiree.transition;

import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.TransitionSet;

public class UiChangeTransition extends TransitionSet {

    public UiChangeTransition() {
        addTransition(new Fade(Fade.OUT));
        addTransition(new TransitionSet()
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeScroll())
                .setOrdering(ORDERING_TOGETHER));
        addTransition(new Fade(Fade.IN));
        setOrdering(ORDERING_SEQUENTIAL);
    }
}
