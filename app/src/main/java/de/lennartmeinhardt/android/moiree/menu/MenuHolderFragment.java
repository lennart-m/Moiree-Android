package de.lennartmeinhardt.android.moiree.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import de.lennartmeinhardt.android.moiree.MenuTransparencyConfig;
import de.lennartmeinhardt.android.moiree.MenuTransparencyConfigHolder;
import de.lennartmeinhardt.android.moiree.R;

public class MenuHolderFragment extends Fragment implements MenuTransparencyConfigHolder, MenuHolder {

    private MenuTransparencyConfig menuTransparencyConfig;

    private SharedPreferences preferences;

    private FrameLayout menuHolder;

    private OnMenuStatusChangedListener onMenuStatusChangedListener;

    private boolean menuShowable;

    private final FragmentManager.OnBackStackChangedListener backstackListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if(getChildFragmentManager().getBackStackEntryCount() == 0) {
                if (onMenuStatusChangedListener != null)
                    onMenuStatusChangedListener.onMenuHidden();
            } else {
                if (onMenuStatusChangedListener != null)
                    onMenuStatusChangedListener.onMenuShown();
            }
        }
    };

    private final MenuTransparencyConfig.OnMenuTransparencyChangedListener menuTransparencySetter = new MenuTransparencyConfig.OnMenuTransparencyChangedListener() {
        @Override
        public void onMenuTransparencyChanged(boolean transparencyEnabled, float menuAlpha) {
            float alpha;
            if(transparencyEnabled)
                alpha = menuAlpha;
            else
                alpha = 1f;
            menuHolder.setAlpha(alpha);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean defaultMenuTransparencyEnabled = getResources().getBoolean(R.bool.menu_transparency_default_enabled);
        float defaultMenuAlpha = getResources().getInteger(R.integer.menu_opacity_default_percents) / 100f;

        menuTransparencyConfig = new MenuTransparencyConfig(defaultMenuTransparencyEnabled, defaultMenuAlpha);
        menuTransparencyConfig.loadFromPreferences(preferences);

        getChildFragmentManager().addOnBackStackChangedListener(backstackListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_holder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.menuHolder = (FrameLayout) view.findViewById(R.id.menu_holder);

        menuTransparencyConfig.addAndFireMenuTransparencyListener(menuTransparencySetter);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        menuTransparencyConfig.storeToPreferences(preferencesEditor);
        preferencesEditor.apply();
    }

    public ViewGroup getMenuHolder() {
        return menuHolder;
    }

    @Override
    public boolean isMenuShowing() {
        if(getActivity() == null)
            return false;
        else
            return getChildFragmentManager().getBackStackEntryCount() > 0;
    }

    @Override
    public void hideMenuIfShown() {
        if(! isMenuShowing())
            return;

        getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void showMenuIfHidden() {
        if (isMenuShowing() || ! menuShowable)
            return;

        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_slide_in_left,
                        R.anim.fade_slide_out_right,
                        R.anim.fade_slide_in_right,
                        R.anim.fade_slide_out_left)
                .add(R.id.menu_holder, new MainMenuFragment())
                .addToBackStack("mainMenu")
                .commit();
    }

    public boolean onBackPressedConsumed() {
        if(isMenuShowing()) {
            closeCurrentMenu();
            return true;
        }
        else
            return false;
    }


    @Override
    public void toggleMenuShowing() {
        if(isMenuShowing())
            hideMenuIfShown();
        else
            showMenuIfHidden();
    }

    @Override
    public MenuTransparencyConfig getMenuTransparencyConfig() {
        return menuTransparencyConfig;
    }

    public void openMenuFragment(Fragment fragment, String tag) {
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_slide_in_left,
                        R.anim.fade_slide_out_right,
                        R.anim.fade_slide_in_right,
                        R.anim.fade_slide_out_left)
                .replace(R.id.menu_holder, fragment)
                .addToBackStack(tag)
                .commit();
    }

    public void setMenuShowable(boolean menuShowable) {
        this.menuShowable = menuShowable;
        if(! menuShowable)
            hideMenuIfShown();
    }

    public void setOnMenuStatusChangedListener(OnMenuStatusChangedListener onMenuStatusChangedListener) {
        this.onMenuStatusChangedListener = onMenuStatusChangedListener;
    }

    public void closeCurrentMenu() {
        getChildFragmentManager().popBackStack();
    }

    public interface OnMenuStatusChangedListener {
        void onMenuShown();

        void onMenuHidden();
    }
}
