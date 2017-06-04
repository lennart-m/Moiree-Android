package de.lennartmeinhardt.android.moiree.transition;

import android.databinding.BindingAdapter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Method;

import de.lennartmeinhardt.android.moiree.R;

/**
 * Provides methods to set color filters on ImageViews.
 * This might be necessary in order to get information about color and mode of a {@link android.graphics.PorterDuffColorFilter}.
 */
public class ColorFilterHelper {

    @BindingAdapter("colorTag")
    public static void setColorTag(View view, int color) {
        view.setTag(R.id.key_porter_duff_color, color);
    }
    public static boolean hasColorTag(View view) {
        return view.getTag(R.id.key_porter_duff_color) != null;
    }
    public static int getColorTag(View view) {
        return (int) view.getTag(R.id.key_porter_duff_color);
    }

    @BindingAdapter("backgroundColor")
    public static void setBackgroundColor(View view, int color) {
        view.setBackgroundColor(color);
    }

    @BindingAdapter("modeTag")
    public static void setModeTag(View view, PorterDuff.Mode mode) {
        view.setTag(R.id.key_porter_duff_mode, mode);
    }
    public static boolean hasModeTag(View view) {
        return view.getTag(R.id.key_porter_duff_mode) != null;
    }
    public static PorterDuff.Mode getModeTag(View view) {
        return (PorterDuff.Mode) view.getTag(R.id.key_porter_duff_mode);
    }

    public static void setColorFilterAndTag(ImageView imageView, int color) {
        imageView.setColorFilter(color);
        setColorTag(imageView, color);
    }

    public static void setColorFilterAndTag(ImageView imageView, int color, PorterDuff.Mode mode) {
        imageView.setColorFilter(color, mode);
        setColorTag(imageView, color);
        setModeTag(imageView, mode);
    }


    private static int getColor(PorterDuffColorFilter colorFilter) {
        try {
            Method m = PorterDuffColorFilter.class.getDeclaredMethod("getColor");
            return (Integer) m.invoke(colorFilter);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static void setColor(PorterDuffColorFilter colorFilter, int color) {
        try {
            Method m = PorterDuffColorFilter.class.getDeclaredMethod("setColor", int.class);
            m.invoke(colorFilter, color);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static PorterDuff.Mode getMode(PorterDuffColorFilter colorFilter) {
        try {
            Method m = PorterDuffColorFilter.class.getDeclaredMethod("getMode");
            return (PorterDuff.Mode) m.invoke(colorFilter);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static void setColor(PorterDuffColorFilter colorFilter, PorterDuff.Mode mode) {
        try {
            Method m = PorterDuffColorFilter.class.getDeclaredMethod("setMode", PorterDuff.Mode.class);
            m.invoke(colorFilter, mode);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    private ColorFilterHelper() {
    }
}
