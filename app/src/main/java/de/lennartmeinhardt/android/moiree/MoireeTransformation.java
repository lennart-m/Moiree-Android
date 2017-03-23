package de.lennartmeinhardt.android.moiree;

import java.util.ArrayList;
import java.util.List;

public class MoireeTransformation {

	public static final float ID_ROTATION = 0;
	public static final float ID_TRANSLATION_X = 0;
	public static final float ID_TRANSLATION_Y = 0;
	public static final float ID_SCALING_X = 1;
	public static final float ID_SCALING_Y = 1;
	public static final float ID_COMMON_SCALING = 1;
	public static final boolean ID_USE_COMMON_SCALING = true;

	private float rotation;
	private float translationX, translationY;
	private float scalingX, scalingY, commonScaling;
	private boolean useCommonScaling;

	private final List<OnTransformationChangedListener> onTransformationChangedListeners = new ArrayList<>();

	
	public float getRotation() {
		return rotation;
	}
	public void setRotation(float rotation) {
		if(this.rotation != rotation) {
			this.rotation = rotation;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onRotationChanged(rotation);
		}
	}

	public float getTranslationX() {
		return translationX;
	}
	public void setTranslationX(float translationX) {
		if(this.translationX != translationX) {
			this.translationX = translationX;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onTranslationXChanged(translationX);
		}
	}

	public float getTranslationY() {
		return translationY;
	}
	public void setTranslationY(float translationY) {
		if(this.translationY != translationY) {
			this.translationY = translationY;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onTranslationYChanged(translationY);
		}
	}

	public float getScalingX() {
		return scalingX;
	}
	public void setScalingX(float scalingX) {
		if(this.scalingX != scalingX) {
			this.scalingX = scalingX;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onScalingXChanged(scalingX);
		}
	}

	public float getScalingY() {
		return scalingY;
	}
	public void setScalingY(float scalingY) {
		if(this.scalingY != scalingY) {
			this.scalingY = scalingY;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onScalingYChanged(scalingY);
		}
	}

	public float getCommonScaling() {
		return commonScaling;
	}
	public void setCommonScaling(float commonScaling) {
		if(this.commonScaling != commonScaling) {
			this.commonScaling = commonScaling;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onCommonScalingChanged(commonScaling);
		}
	}

	public boolean isUseCommonScaling() {
		return useCommonScaling;
	}
	public void setUseCommonScaling(boolean useCommonScaling) {
		if(this.useCommonScaling != useCommonScaling) {
			this.useCommonScaling = useCommonScaling;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onUseCommonScalingChanged(useCommonScaling);
		}
	}
	
	public float getEffectiveScalingX() {
		return isUseCommonScaling() ? commonScaling : scalingX;
	}
	
	public float getEffectiveScalingY() {
		return isUseCommonScaling() ? commonScaling : scalingY;
	}


	public void addOnTransformationChangedListener(OnTransformationChangedListener listener) {
		onTransformationChangedListeners.add(listener);
	}
	public void addAndFireOnTransformationChangedListener(OnTransformationChangedListener listener) {
		addOnTransformationChangedListener(listener);
		listener.onRotationChanged(rotation);
		listener.onCommonScalingChanged(commonScaling);
		listener.onUseCommonScalingChanged(useCommonScaling);
		listener.onScalingXChanged(scalingX);
		listener.onScalingYChanged(scalingY);
		listener.onTranslationXChanged(translationX);
		listener.onTranslationYChanged(translationY);
	}
	public void removeOnTransformationChangedListener(OnTransformationChangedListener listener) {
		onTransformationChangedListeners.remove(listener);
	}
	
	public void setToIdentity() {
		setCommonScalingToIdentity();
		setScalingXToIdentity();
		setScalingYToIdentity();
//		setUseCommonScalingToIdentity();
		setRotationToIdentity();
		setTranslationXToIdentity();
		setTranslationYToIdentity();
	}

	public void setToTransformation(MoireeTransformation transformation) {
		setCommonScaling(transformation.getCommonScaling());
		setScalingX(transformation.getScalingX());
		setScalingY(transformation.getScalingY());
		setUseCommonScaling(transformation.isUseCommonScaling());
		setRotation(transformation.getRotation());
		setTranslationX(transformation.getTranslationX());
		setTranslationY(transformation.getTranslationY());
	}

	public void setRotationToIdentity() {
		setRotation(ID_ROTATION);
	}
	public void setScalingXToIdentity() {
		setScalingX(ID_SCALING_X);
	}
	public void setScalingYToIdentity() {
		setScalingY(ID_SCALING_Y);
	}
	public void setCommonScalingToIdentity() {
		setCommonScaling(ID_COMMON_SCALING);
	}
	public void setUseCommonScalingToIdentity() {
		setUseCommonScaling(ID_USE_COMMON_SCALING);
	}
	public void setTranslationXToIdentity() {
		setTranslationX(ID_TRANSLATION_X);
	}
	public void setTranslationYToIdentity() {
		setTranslationY(ID_TRANSLATION_Y);
	}


	public interface OnTransformationChangedListener {
		void onRotationChanged(float newRotation);

		void onCommonScalingChanged(float newCommonScaling);
		void onScalingXChanged(float newScalingX);
		void onScalingYChanged(float newScalingY);
		void onUseCommonScalingChanged(boolean newUseCommonScaling);

		void onTranslationXChanged(float newTranslationX);
		void onTranslationYChanged(float newTranslationY);


		class Adapter implements OnTransformationChangedListener {
			@Override
			public void onRotationChanged(float newRotation) {
			}

			@Override
			public void onCommonScalingChanged(float newCommonScaling) {
			}

			@Override
			public void onScalingXChanged(float newScalingX) {
			}

			@Override
			public void onScalingYChanged(float newScalingY) {
			}

			@Override
			public void onUseCommonScalingChanged(boolean newUseCommonScaling) {
			}

			@Override
			public void onTranslationXChanged(float newTranslationX) {
			}

			@Override
			public void onTranslationYChanged(float newTranslationY) {
			}
		}
	}
}
