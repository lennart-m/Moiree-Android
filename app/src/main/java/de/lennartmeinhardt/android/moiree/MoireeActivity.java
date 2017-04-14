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
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.menu.MenuHolder;
import de.lennartmeinhardt.android.moiree.menu.MenuHolderFragment;
import de.lennartmeinhardt.android.moiree.transition.ChangeAlpha;

public class MoireeActivity extends AppCompatActivity implements
		MoireeInputMethodsHolder,
		MoireeTransformationHolder,
		MoireeColorsHolder,
		MoireeImaging,
		MenuHolder,
		MenuTransparencyConfigHolder,
		MoireeTransitionStarterHolder {

	private static final String KEY_SYSTEM_UI_VISIBLE = "mainActivity:systemUiVisible";
	private static final String KEY_FIRST_START = "mainActivity:firstStart";

	private SharedPreferences preferences;
	private int immersiveModeDelay;

	private MoireeViewFragment moireeViewFragment;
	private MenuHolderFragment menuHolderFragment;
	private InputHandlerFragment inputHandlerFragment;
	private MoireeImageFragment moireeImageFragment;

	private View decorView;
	private ViewGroup rootView;

	private MoireeColors moireeColors;
	private MoireeTransformation moireeTransformation;
	private MoireeTransitionStarter moireeTransitionStarter;

	private boolean shouldAnimateNextImageChange;

	private final Handler enterImmersiveModeHandler = new EnterImmersiveModeHandler(this);


	@Override
	protected void onDestroy() {
		super.onDestroy();

		SharedPreferences.Editor preferencesEditor = preferences.edit();
		preferencesEditor.putBoolean(KEY_FIRST_START, false);

		moireeColors.storeToPreferences(preferencesEditor);
		moireeTransformation.storeToPreferences(preferencesEditor);

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

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		moireeTransformation = MoireeTransformation.loadDefaultTransformationFromResources(getResources());
		moireeTransformation.loadFromPreferences(preferences);
		initializeMoireeColors(preferences);

		moireeViewFragment = (MoireeViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_moiree_view);
		inputHandlerFragment = (InputHandlerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_touch_handler);

		menuHolderFragment = (MenuHolderFragment) getSupportFragmentManager().findFragmentById(R.id.menu_holder);

		initializeMoireeTransformationStarter();

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
		menuHolderFragment.setOnMenuStatusChangedListener(new MenuHolderFragment.OnMenuStatusChangedListener() {
			@Override
			public void onMenuShown() {
				enterNonImmersiveMode();
			}

			@Override
			public void onMenuHidden() {
				enterImmersiveMode();
			}
		});

		if(savedInstanceState != null)
			setSystemUiVisible(savedInstanceState.getBoolean(KEY_SYSTEM_UI_VISIBLE));
		else
			enterImmersiveMode();

		if(! preferences.contains(KEY_FIRST_START))
			showFirstStartMessage();

		// animate the next image change only when this is starting for the first time; not if it's a restart
		shouldAnimateNextImageChange = savedInstanceState == null;
	}

	@Override
	public void onBackPressed() {
		if(! menuHolderFragment.onBackPressedConsumed())
			finish();
	}

	private void initializeMoireeTransformationStarter() {
		moireeTransitionStarter = new MoireeTransitionStarter((ViewGroup) moireeViewFragment.getView());
		moireeTransitionStarter.setColorTransitionEnabled(true);
		int colorAnimationDurationMillis = getResources().getInteger(R.integer.color_animation_duration_millis);
		int transformationAnimationDurationMillis = getResources().getInteger(R.integer.transformation_animation_duration_millis);
		moireeTransitionStarter.setColorTransitionDuration(colorAnimationDurationMillis);
		moireeTransitionStarter.setTransformationTransitionEnabled(true);
		moireeTransitionStarter.setTransformationTransitionDuration(transformationAnimationDurationMillis);
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

	@Override
	public MoireeTransformation getMoireeTransformation() {
		return moireeTransformation;
	}

	@Override
	public MoireeColors getMoireeColors() {
		return moireeColors;
	}

	@Override
	public void setImageCreatorAndRecreateImage(MoireeImageCreator imageCreator) {
		moireeImageFragment.setImageCreatorAndRecreateImage(imageCreator);
	}

	private void initializeMoireeColors(SharedPreferences preferences) {
		int defaultBackgroundColor = ResourcesCompat.getColor(getResources(), R.color.moiree_background_default, getTheme());
		int defaultForegroundColor = ResourcesCompat.getColor(getResources(), R.color.moiree_foreground_default, getTheme());
		moireeColors = new MoireeColors(defaultForegroundColor, defaultBackgroundColor);
		moireeColors.loadFromPreferences(preferences);
	}

	@Override
	public MoireeInputMethods getMoireeInputMethods() {
		return inputHandlerFragment.getMoireeInputMethods();
	}

	@Override
	public MenuTransparencyConfig getMenuTransparencyConfig() {
		return menuHolderFragment.getMenuTransparencyConfig();
	}

	@Override
	public MoireeTransitionStarter getMoireeTransitionStarter() {
		return moireeTransitionStarter;
	}

	@Override
	public void onMoireeImageCreated(Drawable moireeImage) {
		moireeViewFragment.setMoireeImage(moireeImage, true);

		if(shouldAnimateNextImageChange && moireeTransitionStarter.isTransformationTransitionEnabled()) {
			moireeViewFragment.setMoireeViewsVisible(false);
			MoireeTransformation backupTransformation = new MoireeTransformation();
			backupTransformation.setToTransformation(moireeTransformation);
			moireeTransformation.setToIdentity();

			TransitionSet enterNewImageTransition = new TransitionSet()
					.addTransition(new ChangeAlpha())
					.addTransition(moireeTransitionStarter.createTransformationTransition())
					.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

			moireeTransitionStarter.beginTransformationTransitionIfWanted(enterNewImageTransition);
			moireeViewFragment.setMoireeViewsVisible(true);
			moireeTransformation.setToTransformation(backupTransformation);
		} else
			moireeViewFragment.setMoireeViewsVisible(true);

		// animate all following changes
		shouldAnimateNextImageChange = true;

		menuHolderFragment.setMenuShowable(true);
	}

	@Override
	public void onPreCreateMoireeImage() {
		if(shouldAnimateNextImageChange) {
			ViewGroup moireeViewRoot = (ViewGroup) moireeViewFragment.getView();
			TransitionManager.beginDelayedTransition(moireeViewRoot, new Fade(Fade.OUT));
			moireeViewFragment.setMoireeViewsVisible(false);
		}
		menuHolderFragment.setMenuShowable(false);
	}

	@Override
	public int getMoireeImageWidth() {
		return moireeViewFragment.getMoireeImageWidth();
	}

	@Override
	public int getMoireeImageHeight() {
		return moireeViewFragment.getMoireeImageHeight();
	}

	@Override
	public MoireeImageCreator getImageCreator() {
		return moireeImageFragment.getImageCreator();
	}

	@Override
	public boolean isMenuShowing() {
		return menuHolderFragment.isMenuShowing();
	}

	@Override
	public void showMenuIfHidden() {
		menuHolderFragment.showMenuIfHidden();
	}

	@Override
	public void hideMenuIfShown() {
		menuHolderFragment.hideMenuIfShown();
	}

	@Override
	public void toggleMenuShowing() {
		menuHolderFragment.toggleMenuShowing();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(inputHandlerFragment.onKeyDown(keyCode))
			return true;
		else
			return super.onKeyDown(keyCode, event);
	}

	private static class EnterImmersiveModeHandler extends Handler {
		private final WeakReference<MoireeActivity> mainActivityWeakReference;

		private EnterImmersiveModeHandler(MoireeActivity moireeActivity) {
			this.mainActivityWeakReference = new WeakReference<>(moireeActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			MoireeActivity moireeActivity = mainActivityWeakReference.get();

			if(moireeActivity != null && (! moireeActivity.menuHolderFragment.isMenuShowing()))
				moireeActivity.enterImmersiveMode();
		}
	}
}
