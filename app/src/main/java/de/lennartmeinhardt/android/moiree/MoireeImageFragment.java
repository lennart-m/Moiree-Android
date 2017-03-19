package de.lennartmeinhardt.android.moiree;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.BitmapCompat;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.lennartmeinhardt.android.moiree.imaging.BaseBitmapMoireeImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.CheckerboardImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageMode;
import de.lennartmeinhardt.android.moiree.util.ImageCreatorHolder;

public class MoireeImageFragment extends Fragment implements ImageCreatorHolder {

    private static final String KEY_IMAGE_MODE_NAME = "moireeImageMode";
    private static final String FILE_NAME = "moireeImageBackup.png";

    private MoireeImageCreator moireeImageCreator;

    private SharedPreferences preferences;

    private Drawable moireeImage;

    private boolean shouldSaveImage;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO initialize moiree image creator from preferences
        MoireeImageMode imageMode = MoireeImageMode.valueOfOrDefault(preferences.getString(KEY_IMAGE_MODE_NAME, null));
        moireeImageCreator = imageMode.getImageCreatorInstance(getActivity());
        moireeImageCreator = new CheckerboardImageCreator(getResources());

        if(savedInstanceState != null)
            moireeImageCreator.loadFromBundle(savedInstanceState, "");
        else
            moireeImageCreator.loadFromPreferences(preferences);

//		moireeImageCreator = new RandomPixelsImageCreator(
//				getResources().getDimensionPixelSize(R.dimen.random_pixels_image_default_square_size),
//				getResources().getInteger(R.integer.random_pixels_image_default_density_percents) / 100f);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View root = ((MainActivity) getActivity()).getRootView();
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT < 16)
                    root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    root.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                initializeMoireeImage();
            }
        });
    }

    private void initializeMoireeImage() {
        // TODO aufteilen. bild kann in oncreate geladen werden. wenn es danach (onActivityCreated) immernoch nicht existiert führe das inBackground aus

        // try to retrieve image from internal storage
        if(moireeImageCreator instanceof BaseBitmapMoireeImageCreator) {
            Bitmap bitmap = loadImageFromInternalStorage((BaseBitmapMoireeImageCreator) moireeImageCreator);
            if(bitmap != null) {
                Drawable image = ((BaseBitmapMoireeImageCreator) moireeImageCreator).createDrawableFromBitmap(getResources(), bitmap);
                setMoireeImage(image);
                shouldSaveImage = false;
                return;
            }
        }

        // image has not been retrieved from internal storage. create a new one
        shouldSaveImage = true;
        recreateImageInBackground();
    }

    private Bitmap loadImageFromInternalStorage(BaseBitmapMoireeImageCreator bitmapMoireeImageCreator) {
        File imageFile = getInternalImageFile();
        return BitmapFactory.decodeFile(imageFile.getPath());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(shouldSaveImage && moireeImageCreator instanceof BaseBitmapMoireeImageCreator) {
            Bitmap bitmap = ((BaseBitmapMoireeImageCreator) moireeImageCreator).getBitmapFromDrawable(moireeImage);
            saveImageInInternalStorage(bitmap);
        }

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        moireeImageCreator.storeToPreferences(preferencesEditor);
        preferencesEditor.apply();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        moireeImageCreator.storeToBundle(outState, "");
    }

    private static Bitmap getAsArgb8888(Bitmap bitmap, boolean recycleOld) {
        if(bitmap.getConfig() == Bitmap.Config.ARGB_8888)
            return bitmap;
        else {
            // for older versions than android 4.4 a copy has to be created
//            if (Build.VERSION.SDK_INT < 19) {
                Bitmap configCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                if(recycleOld)
                    bitmap.recycle();
                return configCopy;
//            } else {
//                // for newer versions the config can be changed here
//                bitmap.setConfig(Bitmap.Config.ARGB_8888);
//                return bitmap;
//            }
        }
    }

    private void saveImageInInternalStorage(Bitmap bitmap) {
        bitmap = getAsArgb8888(bitmap, true);

        FileOutputStream outputStream = null;
        File outFile = getInternalImageFile();

        try {
            outputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
            }
        }

    }

    private File getInternalImageFile() {
        return new File(getActivity().getFilesDir(), FILE_NAME);
    }

    public void setImageCreatorAndRecreateImage(MoireeImageCreator moireeImageCreator) {
        this.moireeImageCreator = moireeImageCreator;
        recreateImageInBackground();
    }

    private void setMoireeImage(Drawable image) {
        if(this.moireeImage != image) {
            this.moireeImage = image;
            // something about the image has changed. it should be saved when this fragment is destroyed
            shouldSaveImage = true;
        }

        // TODO firstStart muss angegeben werden damit die activity nicht jedes mal wieder neu animiert
        if(getActivity() != null)
            ((MainActivity) getActivity()).onMoireeImageCreated(image);
    }

    private void recreateImageInBackground() {
        new ImageCreatorTask().execute();
    }


    private class ImageCreatorTask extends AsyncTask<Void, Void, Drawable> {

        private int width, height;
        private long t;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity mainActivity = (MainActivity) getActivity();
            width = mainActivity.getMoireeImageWidth();
            height = mainActivity.getMoireeImageHeight();
            mainActivity.onPreCreateMoireeImage();

            t = t();
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            return moireeImageCreator.createMoireeImageForDimensions(getResources(), width, height);
        }

        private long t() {
            return System.currentTimeMillis();
        }

        private long d(long t) {
            return t() - t;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            System.out.println("created image in " + d(t) + " ms");
            setMoireeImage(drawable);
        }
    }

    @Override
    public MoireeImageCreator getImageCreator() {
        return moireeImageCreator;
    }
}
