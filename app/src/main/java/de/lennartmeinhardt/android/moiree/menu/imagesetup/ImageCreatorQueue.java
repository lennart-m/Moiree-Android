package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import de.lennartmeinhardt.android.moiree.imaging.BaseBitmapMoireeImageCreator;

class ImageCreatorQueue {

    private final BaseBitmapMoireeImageCreator imageCreator;

    private final CalculationListener calculationListener;

    private BitmapDrawable bitmapDrawable;

    private ImageCreatorTask calculatingTask;
    private ImageCreatorTask waitingTask;


    ImageCreatorQueue(BaseBitmapMoireeImageCreator imageCreator, CalculationListener calculationListener) {
        this.imageCreator = imageCreator;
        this.calculationListener = calculationListener;
    }


    void recalculateImageForDimensions(Resources resources, int width, int height) {
        ImageCreatorTask imageCreatorTask = new ImageCreatorTask(resources, width, height);
        if(calculatingTask == null)
            imageCreatorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            waitingTask = imageCreatorTask;
    }

    private class ImageCreatorTask extends AsyncTask<Void, Void, BitmapDrawable> {

        private final Resources resources;
        private final int width, height;


        ImageCreatorTask(Resources resources, int width, int height) {
            this.resources = resources;
            this.width = width;
            this.height = height;
        }

        @Override
        protected void onPreExecute() {
            calculatingTask = this;
            waitingTask = null;
            calculationListener.onCalculationStarting();
        }

        @Override
        protected BitmapDrawable doInBackground(Void... params) {
            if(bitmapDrawable == null)
                return imageCreator.createMoireeImageForDimensions(resources, width, height);
            else {
                Bitmap bitmap = bitmapDrawable.getBitmap();
                imageCreator.drawImageToBitmap(bitmap);
                return null;
            }
        }

        @Override
        protected void onPostExecute(BitmapDrawable result) {
            if(result != null)
                bitmapDrawable = result;

            calculationListener.onCalculationSuccessful(result, waitingTask != null);

            calculatingTask = null;
            callWaitingTaskIfPresent();
        }

        @Override
        protected void onCancelled(BitmapDrawable result) {
            calculationListener.onCalculationCancelled(waitingTask != null);

            calculatingTask = null;
            callWaitingTaskIfPresent();
        }

        private void callWaitingTaskIfPresent() {
            if(waitingTask != null)
                waitingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    interface CalculationListener {

        void onCalculationStarting();

        void onCalculationSuccessful(@Nullable BitmapDrawable moireeImage, boolean willContinueCalculating);

        void onCalculationCancelled(boolean willContinueCalculating);
    }
}
