package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.FragmentUserProvidedImageSetupBinding;
import de.lennartmeinhardt.android.moiree.imaging.UserProvidedImageCreator;
import de.lennartmeinhardt.android.moiree.util.Expandable;
import de.lennartmeinhardt.android.moiree.util.Expandables;

public class UserProvidedImageSetupFragment extends BaseImageCreatorSetupFragment <UserProvidedImageCreator> implements Expandable {

    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final String BACKUP_IMAGE_FILE_NAME = "userProvidedImageBackup.png";

    private FragmentUserProvidedImageSetupBinding binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_provided_image_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding.setUserProvidedImageCreator(getImageCreator());

        initializeHeaderView();
        initializeContentView();

        loadBackupUserProvidedImageIfPresent();
    }

    private void loadBackupUserProvidedImageIfPresent() {
        final File backupFile = getUserProvidedImageBackupFile();
        try {
            FileInputStream in = new FileInputStream(backupFile);
            new ImageLoaderTask(in, /* from backup */ true).execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initializeHeaderView() {
        binding.expandableViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition();
                binding.expandableView.toggleExpanded();
            }
        });
    }

    private void initializeContentView() {
        binding.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewImageClicked();
                new ImageSaverTask().execute();
            }
        });

        binding.loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenImagePickerIntent();
            }
        });

        binding.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteCurrentImageConfirmDialog();
            }
        });

        binding.imagePreview.setColorFilter(Color.BLACK);

        binding.showInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowLoadImageHelp();
            }
        });
    }

    private void openDeleteCurrentImageConfirmDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.title_confirm_delete_user_provided_image)
                .setMessage(R.string.message_confirm_delete_user_provided_image)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCurrentImage();
                    }
                })
                .show();
    }

    private void deleteCurrentImage() {
        File imageFile = getUserProvidedImageBackupFile();
        try {
            imageFile.delete();
            beginMenuBoundsTransition();
            binding.setImageLoaded(false);
            binding.imagePreview.setImageBitmap(null);
            getImageCreator().setUserProvidedImage(null, true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void onShowLoadImageHelp() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.user_provided_image_help_title)
                .setMessage(R.string.user_provided_image_help_message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void onOpenImagePickerIntent() {
        Intent chooseImageIntent = new Intent()
                                    .setType("image/*")
                                    .setAction(Intent.ACTION_GET_CONTENT)
                                    .addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(chooseImageIntent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                InputStream in = getActivity().getContentResolver().openInputStream(uri);
                new ImageLoaderTask(in, /* from backup */ false).execute();
            } catch (FileNotFoundException e) {
                binding.setBusy(false);
                showLoadImageErrorMessage();
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showLoadImageErrorMessage() {
        Toast.makeText(getContext(), R.string.error_msg_load_image, Toast.LENGTH_LONG).show();
    }

    @Override
    protected UserProvidedImageCreator initializeImageCreator() {
        boolean defKeepAspectRatio = getResources().getBoolean(R.bool.user_image_def_keep_aspect_ratio);

        return new UserProvidedImageCreator(defKeepAspectRatio);
    }

    @Override
    public boolean isExpanded() {
        return binding.expandableView.isExpanded();
    }

    @Override
    public void setExpanded(boolean expanded) {
        binding.expandableView.setExpanded(expanded);
    }

    @Override
    public void toggleExpanded() {
        binding.expandableView.toggleExpanded();
    }

    @Override
    public void setOnExpandedStateChangedListener(final OnExpandedStateChangedListener onExpandedStateChangedListener) {
        Expandables.setExpandedListenerForWrappedExpandable(this, binding.expandableView, onExpandedStateChangedListener);
    }

    private void saveBackupUserProvidedImage() {
        final Bitmap userProvidedImage = getImageCreator().getUserProvidedImage();
        final File outFile = getUserProvidedImageBackupFile();

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outFile);
            userProvidedImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream != null)
                    outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File getUserProvidedImageBackupFile() {
        return new File(getContext().getFilesDir(), BACKUP_IMAGE_FILE_NAME);
        // TODO external
    }


    private class ImageSaverTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            saveBackupUserProvidedImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            binding.setBusy(false);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            binding.setBusy(false);
        }
    }

    private class ImageLoaderTask extends AsyncTask<Void, Void, Bitmap> {

        private final boolean fromBackup;
        private final InputStream inputStream;

        public ImageLoaderTask(InputStream inputStream, boolean fromBackup) {
            this.inputStream = inputStream;
            this.fromBackup = fromBackup;
        }

        @Override
        protected void onPreExecute() {
            binding.setBusy(true);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                return BitmapFactory.decodeStream(inputStream);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(getActivity() == null)
                return;

            if(isBitmapValid(bitmap)) {
                getImageCreator().setUserProvidedImage(bitmap, /* recycle */ true);
                binding.imagePreview.setImageBitmap(bitmap);
                beginMenuBoundsTransition();
                binding.setImageLoaded(true);
                // if the image was loaded from backup, do not save if again
                if(fromBackup)
                    binding.setBusy(false);
                else
                    new ImageSaverTask().execute();
            } else {
                binding.setBusy(false);
                if(! fromBackup)
                    showLoadImageErrorMessage();
            }
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            if(getActivity() == null)
                return;

            binding.setBusy(false);
        }

        private boolean isBitmapValid(Bitmap bitmap) {
            return bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0 && bitmap.hasAlpha();
        }
    }
}
