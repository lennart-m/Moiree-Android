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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.lennartmeinhardt.android.moiree.imaging.BaseBitmapMoireeImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.CheckerboardImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.ImageCreatorHolder;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class MoireeImageFragment extends Fragment implements ImageCreatorHolder {

    private static final String KEY_IMAGE_CREATOR_CLASS = "moireeImageCreator:class";
    private static final String FILE_NAME = "moireeImageBackup.png";

    private MoireeImageCreator moireeImageCreator;

    private SharedPreferences preferences;
    private SharedPreferences preferencesForBackup;

    private Drawable moireeImage;

    private View rootView;

    private boolean reusedSavedImage;

    private ImageCreatorTask calculatingTask;
    private ImageCreatorTask nextTaskToCalculate;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(savedInstanceState != null)
            moireeImageCreator = loadImageCreatorFromBundle(savedInstanceState, null);
        else
            moireeImageCreator = loadImageCreatorFromPreferences(preferences, null);

        if(moireeImageCreator == null) {
            int defSquareSize = getResources().getDimensionPixelSize(R.dimen.checkerboard_image_default_square_size);
            moireeImageCreator = new CheckerboardImageCreator(defSquareSize);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preferencesForBackup = getContext().getSharedPreferences("imageCreatorBackup", Context.MODE_PRIVATE);
        MoireeImageCreator imageCreatorForBackup = loadImageCreatorFromPreferences(preferencesForBackup, null);

        reusedSavedImage = false;
        if(imageCreatorForBackup != null
                && imageCreatorForBackup.equals(moireeImageCreator)
                && imageCreatorForBackup instanceof BaseBitmapMoireeImageCreator) {
            Bitmap bitmap = loadImageFromInternalStorage();
            if(bitmap != null) {
                moireeImage = ((BaseBitmapMoireeImageCreator) moireeImageCreator).createDrawableFromBitmap(getResources(), bitmap);
                reusedSavedImage = true;
            }
        }

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16)
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if(reusedSavedImage)
                    notifyActivity();
                else
                    recreateImageInBackground();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_creator, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        rootView.setAlpha(0);
    }

    private static void storeImageCreatorToPreferences(SharedPreferences.Editor preferencesEditor, MoireeImageCreator imageCreator) {
        preferencesEditor.putString(KEY_IMAGE_CREATOR_CLASS, imageCreator.getClass().getName());

        if(imageCreator instanceof PreferenceIO)
            ((PreferenceIO) imageCreator).storeToPreferences(preferencesEditor);
    }

    private static void storeImageCreatorToBundle(Bundle bundle, MoireeImageCreator imageCreator) {
        bundle.putString(KEY_IMAGE_CREATOR_CLASS, imageCreator.getClass().getName());

        if(imageCreator instanceof BundleIO)
            ((BundleIO) imageCreator).storeToBundle(bundle);
    }

    private static MoireeImageCreator loadImageCreatorFromPreferences(SharedPreferences preferences, MoireeImageCreator defValue) {
        String className = preferences.getString(KEY_IMAGE_CREATOR_CLASS, null);
        MoireeImageCreator imageCreator;
        try {
            Class<?> clazz = Class.forName(className);
            imageCreator = (MoireeImageCreator) clazz.newInstance();
        } catch(Exception e) {
            imageCreator = defValue;
        }

        if(imageCreator instanceof PreferenceIO)
            ((PreferenceIO) imageCreator).loadFromPreferences(preferences);

        return imageCreator;
    }

    private static MoireeImageCreator loadImageCreatorFromBundle(Bundle bundle, MoireeImageCreator defValue) {
        String className = bundle.getString(KEY_IMAGE_CREATOR_CLASS, null);
        MoireeImageCreator imageCreator;
        try {
            Class<?> clazz = Class.forName(className);
            imageCreator = (MoireeImageCreator) clazz.newInstance();
        } catch(Exception e) {
            imageCreator = defValue;
        }

        if(imageCreator instanceof PreferenceIO)
            ((BundleIO) imageCreator).loadFromBundle(bundle);

        return imageCreator;
    }

    private Bitmap loadImageFromInternalStorage() {
        File imageFile = getInternalImageFile();
        return BitmapFactory.decodeFile(imageFile.getPath());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if((!reusedSavedImage) && moireeImageCreator instanceof BaseBitmapMoireeImageCreator) {
            Bitmap bitmap = ((BaseBitmapMoireeImageCreator) moireeImageCreator).getBitmapFromDrawable(moireeImage);
            saveImageInInternalStorage(bitmap);
            SharedPreferences.Editor backupPrefsEditor = preferencesForBackup.edit();
            storeImageCreatorToPreferences(backupPrefsEditor, moireeImageCreator);
            backupPrefsEditor.apply();
        }

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        storeImageCreatorToPreferences(preferencesEditor, moireeImageCreator);
        preferencesEditor.apply();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        storeImageCreatorToBundle(outState, moireeImageCreator);
    }

    private static Bitmap getAsArgb8888(Bitmap bitmap, boolean recycleOld) {
        if(bitmap.getConfig() == Bitmap.Config.ARGB_8888)
            return bitmap;
        else {
            Bitmap configCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            if(recycleOld)
                bitmap.recycle();
            return configCopy;
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

    private void notifyActivity() {
        // check if the activity is null as it may have been closed before some calculation was finished
        if(getActivity() != null)
            ((MainActivity) getActivity()).onMoireeImageCreated(moireeImage);
    }

    private void recreateImageInBackground() {
        // if something is calculating right now, cancel it and queue a new task to start
        if(calculatingTask != null) {
            nextTaskToCalculate = new ImageCreatorTask();
            calculatingTask.cancel(true);
        } else {
            new ImageCreatorTask().execute();
        }
    }


    private class ImageCreatorTask extends AsyncTask<Void, Void, Drawable> {

        private MoireeImageCreator imageCreatorToUse;

        private int width, height;
        private long t;

        @Override
        protected void onPreExecute() {
            rootView.animate().alpha(1).start();

            calculatingTask = this;
            nextTaskToCalculate = null;

            imageCreatorToUse = moireeImageCreator;
            MainActivity mainActivity = (MainActivity) getActivity();
            width = mainActivity.getMoireeImageWidth();
            height = mainActivity.getMoireeImageHeight();
            mainActivity.onPreCreateMoireeImage();

            t = t();
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            return imageCreatorToUse.createMoireeImageForDimensions(getResources(), width, height);
        }

        private long t() {
            return System.currentTimeMillis();
        }

        private long d(long t) {
            return t() - t;
        }

        @Override
        protected void onCancelled(Drawable drawable) {
            calculatingTask = null;
            if(nextTaskToCalculate != null)
                nextTaskToCalculate.execute();
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            calculatingTask = null;
            System.out.println("created image in " + d(t) + " ms");
            moireeImage = drawable;
            rootView.animate().alpha(0).start();
            notifyActivity();
            reusedSavedImage = false;
        }
    }

    @Override
    public MoireeImageCreator getImageCreator() {
        return moireeImageCreator;
    }

    public boolean isCalculating() {
        return calculatingTask != null;
    }
}
