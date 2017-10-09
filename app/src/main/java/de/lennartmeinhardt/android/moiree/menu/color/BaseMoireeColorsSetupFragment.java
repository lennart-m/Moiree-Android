package de.lennartmeinhardt.android.moiree.menu.color;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.lennartmeinhardt.android.moiree.MoireeColors;
import de.lennartmeinhardt.android.moiree.MoireeColorsHolder;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarter;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarterHolder;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;

abstract class BaseMoireeColorsSetupFragment extends MenuFragment {

    private static final String KEY_BACKUP_FOREGROUND_COLOR = "moireeColorsSetup:backupForegroundColor";
    private static final String KEY_BACKUP_BACKGROUND_COLOR = "moireeColorsSetup:backupBackgroundColor";

    MoireeColors moireeColors;
    private MoireeTransitionStarter moireeTransitionStarter;

    private int backupForegroundColor,
                backupBackgroundColor;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.moireeColors = ((MoireeColorsHolder) getActivity()).getMoireeColors();
        if(getActivity() instanceof MoireeTransitionStarterHolder)
            this.moireeTransitionStarter = ((MoireeTransitionStarterHolder) getActivity()).getMoireeTransitionStarter();

        if(savedInstanceState == null) {
            backupForegroundColor = moireeColors.foregroundColor.get();
            backupBackgroundColor = moireeColors.backgroundColor.get();
        } else {
            backupForegroundColor = savedInstanceState.getInt(KEY_BACKUP_FOREGROUND_COLOR);
            backupBackgroundColor = savedInstanceState.getInt(KEY_BACKUP_BACKGROUND_COLOR);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_BACKUP_FOREGROUND_COLOR, backupForegroundColor);
        outState.putInt(KEY_BACKUP_BACKGROUND_COLOR, backupBackgroundColor);
    }

    void resetColorsToBackup() {
        setColorsWithTransition(backupForegroundColor, backupBackgroundColor);
    }

    void setColorsWithTransition(int foregroundColor, int backgroundColor) {
        if(moireeTransitionStarter != null)
            moireeTransitionStarter.beginColorTransitionIfWanted();

        moireeColors.backgroundColor.set(backgroundColor);
        moireeColors.foregroundColor.set(foregroundColor);
    }

    void beginMoireeColorTransitionIfWanted() {
        if(moireeTransitionStarter != null)
            moireeTransitionStarter.beginColorTransitionIfWanted();
    }
}
