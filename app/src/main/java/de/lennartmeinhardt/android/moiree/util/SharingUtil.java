package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.lennartmeinhardt.android.moiree.R;

public class SharingUtil {

    private static final String FILE_NAME = "moiree.jpg";


    public static void saveAndShareImage(Context context, Bitmap image) {
        File imageFile = saveImageToShare(context, image);
        if(imageFile != null)
            shareImage(context, imageFile);
    }

    private static File saveImageToShare(Context context, Bitmap image) {
        File outFile = getSharedImageFile(context);

        if(outFile == null)
            return null;

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return outFile;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(outputStream != null)
                    outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void shareImage(Context context, File imageFile) {
        context.startActivity(getShareImageIntent(context, imageFile));
    }

    private static File getSharedImageFile(Context context) {
        File rootDir = context.getFilesDir();
        File imagesDir = new File(rootDir, "images/");

        if(imagesDir.exists() || imagesDir.mkdirs()) {
            return new File(imagesDir, FILE_NAME);
        } else
            return null;
    }

    private static void addSharedFileToIntent(Intent intent, Uri fileUri, String mimeType) {
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setType(mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    private static Intent getShareImageIntent(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context.getApplicationContext(), context.getApplicationContext().getPackageName() + ".fileprovider", file);

        String title = context.getString(R.string.shared_image_title);

        Intent intent;
        intent = new Intent(Intent.ACTION_SEND);
        addSharedFileToIntent(intent, fileUri, "image/jpeg");
        intent.putExtra(Intent.EXTRA_TEXT, title);
        return intent;
    }


    private SharingUtil() {
    }
}
