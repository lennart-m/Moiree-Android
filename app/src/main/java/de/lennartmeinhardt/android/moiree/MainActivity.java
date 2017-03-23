package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.menu.MainMenuFragment;
import de.lennartmeinhardt.android.moiree.util.ImageCreatorHolder;
import de.lennartmeinhardt.android.moiree.util.StateReportingActivity;

// TODO: first steps activity: holt sich aus dieser activity die view bounds und zeigt ein overlay

// TODO transition: alle casts zu mainactivity weg, sowie alle findViewById(android.R.id.content) und viewgroup-casts
// TODO casts zu mainactivity entfernen. besser mehr interfaces definieren wie transformationHolder. damit werden fragments leichter wiederverwendbar
// TODO mittels MainActivity -> find usages. und suchen nach (ViewGroup), android.R.id.content, R.id.menu_holder

// TODO menuHolder als eigenes fragment auslagern?
// TODO MenuActivity schreiben. darin kann der ganze menü-kram ausgelagert werden. die kennt nur ihren menuHolder
// TODO außerhalb des menüs klicken macht nicht alle menüs zu sondern merkt sich das letzte. es wird nur das menü ausgeblendet, nicht entfernt

// TODO pattern: add and fire listener

// TODO alle vectordrawables knallgrün machen (oder so). dann alle mit tint versehen -> textPrimary

public class MainActivity extends StateReportingActivity
		implements
		MoireeInputMethodsHolder,
		MoireeTransformationHolder,
		MoireeColorsHolder,
		MenuTransparencyConfigHolder,
		MoireeTransitionStarterHolder,
		ImageCreatorHolder {

	private static final boolean DEBUG = true; // TODO raus

	private static final String KEY_SYSTEM_UI_VISIBLE = "mainActivity:systemUiVisible";
	private static final String KEY_FIRST_START = "mainActivity:firstStart";

	private static final String TAG_MAIN_MENU = "mainMenu";

	private MoireeViewFragment moireeViewFragment;

	private MenuTransparencyConfig menuTransparencyConfig;

	private int immersiveModeDelay;

	private MoireeTransitionStarter moireeTransitionStarter;

	private MoireeColors moireeColors;

	private TouchHandlerFragment touchHandlerFragment;

	private MoireeTransformation moireeTransformation;

	private ViewGroup menuHolder;

	private MoireeImageFragment moireeImageFragment;

	private SharedPreferences preferences;

	private boolean shouldAnimateImageChange;

	private final Handler enterImmersiveModeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(! isMenuShowing())
				enterImmersiveMode();
		}
	};

	private View decorView;
	private ViewGroup rootView;

	private MenuTransparencyConfig.OnMenuTransparencyChangedListener menuTransparencySetter = new MenuTransparencyConfig.OnMenuTransparencyChangedListener() {
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
	protected void onDestroy() {
		super.onDestroy();

		SharedPreferences.Editor preferencesEditor = preferences.edit();
		preferencesEditor.putBoolean(KEY_FIRST_START, false);

		moireeColors.storeToPreferences(preferencesEditor);
		MoireeTransformationPreferencesHelper.storeMoireeTransformationToPreferences(preferencesEditor, moireeTransformation);

		menuTransparencyConfig.storeToPreferences(preferencesEditor);

		preferencesEditor.apply();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if(hasFocus)
			delayedHide();
		else
			enterImmersiveModeHandler.removeMessages(0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		immersiveModeDelay = getResources().getInteger(R.integer.immersive_mode_delay_millis);

		setContentView(R.layout.activity_main);

		decorView = getWindow().getDecorView();
		rootView = (ViewGroup) findViewById(android.R.id.content);
		menuHolder = (ViewGroup) findViewById(R.id.menu_holder);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		moireeTransformation = MoireeTransformationPreferencesHelper.loadMoireeTransformationFromPreferences(preferences);
		initializeMoireeColors(preferences);

		moireeViewFragment = (MoireeViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_moiree_view);
		touchHandlerFragment = (TouchHandlerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_touch_handler);

		moireeTransitionStarter = new MoireeTransitionStarter((ViewGroup) moireeViewFragment.getView());
		// TODO sinnvoller
		moireeTransitionStarter.setColorTransitionEnabled(true);
		moireeTransitionStarter.setColorTransitionDuration(300); // TODO woanders setzen. getResources().getInteger(android.R.integer.config_shortAnimTime)
		moireeTransitionStarter.setTransformationTransitionEnabled(true);
		moireeTransitionStarter.setTransformationTransitionDuration(2000);

		boolean defaultMenuTransparencyEnabled = getResources().getBoolean(R.bool.menu_transparency_default_enabled);
		float defaultMenuAlpha = getResources().getInteger(R.integer.menu_opacity_default_percents) / 100f;
		menuTransparencyConfig = new MenuTransparencyConfig(defaultMenuTransparencyEnabled, defaultMenuAlpha);
		menuTransparencyConfig.addMenuTransparencyListener(menuTransparencySetter);
		menuTransparencyConfig.loadFromPreferences(preferences);

		moireeImageFragment = (MoireeImageFragment) getSupportFragmentManager().findFragmentById(R.id.moiree_image_fragment);

		decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				boolean visible = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
				if(visible)
					delayedHide();
			}
		});

		// when the menu is showing, show the status and navigation bar. when it's disappearing hide them
		getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				if(getSupportFragmentManager().getBackStackEntryCount() == 0)
					enterImmersiveMode();
				else
					enterNonImmersiveMode();
			}
		});

		if(savedInstanceState != null)
			setSystemUiVisible(savedInstanceState.getBoolean(KEY_SYSTEM_UI_VISIBLE));
		else
			enterImmersiveMode();

		if(DEBUG || ! preferences.contains(KEY_FIRST_START))
			showFirstStartMessage();

		shouldAnimateImageChange = savedInstanceState == null;
	}

	private void delayedHide() {
		enterImmersiveModeHandler.removeMessages(0);
		enterImmersiveModeHandler.sendEmptyMessageDelayed(0, immersiveModeDelay);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		delayedHide();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			toggleMenuShowing();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void toggleMenuShowing() {
		if(isMenuShowing())
			hideMenuIfShown();
		// TODO die bedingung ist unsinn
		else// if(! moireeImageFragment.isCalculating())
			showMenuIfHidden();
	}

	private void showFirstStartMessage() {
		Snackbar welcomeMessage = Snackbar.make(rootView, R.string.first_start_message, Snackbar.LENGTH_LONG);
		welcomeMessage.getView().getBackground().setAlpha(200);
		welcomeMessage.addCallback(new Snackbar.Callback() {
			@Override
			public void onDismissed(Snackbar transientBottomBar, int event) {
				Snackbar menuHint = Snackbar.make(rootView, R.string.click_to_open_menu, Snackbar.LENGTH_LONG);
				menuHint.getView().getBackground().setAlpha(200);
				menuHint.show();
			}
		});
		welcomeMessage.show();
	}

	private boolean isSystemUiVisible() {
		return (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION ) == 0;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(KEY_SYSTEM_UI_VISIBLE, isSystemUiVisible());
	}

	private void setSystemUiVisible(boolean systemUiVisible) {
		if(systemUiVisible)
			enterNonImmersiveMode();
		else
			enterImmersiveMode();
	}

	private void enterImmersiveMode() {
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	private void enterNonImmersiveMode() {
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	public void showMenuIfHidden() {
		if(isMenuShowing())
			return;

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		MainMenuFragment mainMenuFragment = new MainMenuFragment();
		ft.setCustomAnimations(
				R.anim.fade_slide_in_left,
				R.anim.fade_slide_out_right,
				R.anim.fade_slide_in_right,
				R.anim.fade_slide_out_left);
		ft.add(R.id.menu_holder, mainMenuFragment);
		ft.addToBackStack(TAG_MAIN_MENU);
		ft.commit();
	}

	public boolean isMenuShowing() {
		return menuHolder.getChildCount() > 0;
	}

	public void hideMenuIfShown() {
		if(! isMenuShowing())
			return;

		// TODO problem: hier werden alle fragments nochmal erzeugt und wieder entfernt.
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public MoireeTransformation getMoireeTransformation() {
		return moireeTransformation;
	}

	@Override
	public MoireeColors getMoireeColors() {
		return moireeColors;
	}

	public void setImageCreatorAndRecreateImage(MoireeImageCreator imageCreator) {
		moireeImageFragment.setImageCreatorAndRecreateImage(imageCreator);
	}

	private void initializeMoireeColors(SharedPreferences preferences) {
		moireeColors = new MoireeColors();
		moireeColors.loadFromPreferences(preferences);
	}


	public void openSubMenu(Fragment fragment, String tag) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(
				R.anim.fade_slide_in_left,
				R.anim.fade_slide_out_right,
				R.anim.fade_slide_in_right,
				R.anim.fade_slide_out_left);
		ft.replace(R.id.menu_holder, fragment);
		ft.addToBackStack(tag);
		ft.commit();
	}

	@Override
	public MoireeInputMethods getMoireeInputMethods() {
		return touchHandlerFragment.getMoireeInputMethods();
	}

	@Override
	public MenuTransparencyConfig getMenuTransparencyConfig() {
		return menuTransparencyConfig;
	}

	@Override
	public MoireeTransitionStarter getMoireeTransitionStarter() {
		return moireeTransitionStarter;
	}

	public void onMoireeImageCreated(Drawable moireeImage) {
		System.out.println("on image created");

		moireeViewFragment.setMoireeImage(moireeImage, true);

		// TODO weiter. evtl enabled state des menüs ändern oder ähnliches. auf jeden fall einblenden der moiree-image-views, zurücksetzen der trafo, und dann animieren der trafo
		if(shouldAnimateImageChange && moireeTransitionStarter.isTransformationTransitionEnabled()) {
			moireeViewFragment.setMoireeViewsVisible(false);
			MoireeTransformation backupTransformation = new MoireeTransformation();
			MoireeTransformations.copyTransformationValues(moireeTransformation, backupTransformation);
			moireeTransformation.setToIdentity();

			TransitionSet enterNewImageTransition = new TransitionSet()
					.addTransition(new Fade(Fade.IN))
					.addTransition(moireeTransitionStarter.createTransformationTransition())
					.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

			moireeTransitionStarter.beginTransformationTransitionIfWanted(enterNewImageTransition);
			moireeViewFragment.setMoireeViewsVisible(true);
			MoireeTransformations.copyTransformationValues(backupTransformation, moireeTransformation);
		}

		// animate all following changes
		shouldAnimateImageChange = true;
	}

	public void onPreCreateMoireeImage() {
		if(shouldAnimateImageChange) {
			TransitionManager.beginDelayedTransition(((ViewGroup) moireeViewFragment.getView()), new Fade(Fade.OUT));
			moireeViewFragment.setMoireeViewsVisible(false);
		}
	}

	public int getMoireeImageWidth() {
		return moireeViewFragment.getMoireeImageWidth();
	}

	public int getMoireeImageHeight() {
		return moireeViewFragment.getMoireeImageHeight();
	}

	@Override
	public MoireeImageCreator getImageCreator() {
		return moireeImageFragment.getImageCreator();
	}
}
