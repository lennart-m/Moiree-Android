package de.lennartmeinhardt.android.moiree.menu;

public interface MenuHolder {

    boolean isMenuShowing();

    void showMenuIfHidden();

    void hideMenuIfShown();

    void toggleMenuShowing();

}
