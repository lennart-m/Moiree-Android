//package de.lennartmeinhardt.android.moiree.menu;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.preference.PreferenceManager;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Checkable;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.TextView;
//
//import de.lennartmeinhardt.android.moiree.MainActivity;
//import de.lennartmeinhardt.android.moiree.MoireeColors;
//import de.lennartmeinhardt.android.moiree.MoireeView;
//import de.lennartmeinhardt.android.moiree.R;
//import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
//
//public class AvailableImageTypeListItem extends LinearLayout implements Checkable {
//
//    private AvailableImageCreator availableImageCreator;
//    private MoireeImageCreator imageCreator;
//
//    private TextView nameView;
//    private ImageButton editButton;
//    private MoireeView imagePreview;
//    private RadioButton checkButton;
//
//    public AvailableImageTypeListItem(Context context) {
//        super(context);
//        inflateUI(context);
//    }
//
//    public AvailableImageTypeListItem(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        inflateUI(context);
//    }
//
//    private void inflateUI(Context context) {
//        LayoutInflater.from(context).inflate(R.layout.list_item_image_creator, this);
//
//        nameView = (TextView) findViewById(R.id.image_creator_name);
//        editButton = (ImageButton) findViewById(R.id.image_creator_edit_button);
//        imagePreview = (MoireeView) findViewById(R.id.image_creator_preview);
//        checkButton = (RadioButton) findViewById(R.id.image_creator_check_button);
//
//        MoireeColors moireeColors = ((MainActivity) getContext()).getMoireeColors();
//        imagePreview.setForegroundColor(moireeColors.getForegroundColor());
//        imagePreview.setBackgroundColor(moireeColors.getBackgroundColor());
//    }
//
//    public void setImageCreatorHolder(final AvailableImageCreator availableImageCreator) {
//        this.availableImageCreator = availableImageCreator;
//        imageCreator = availableImageCreator.createMoireeImageCreator();
//        nameView.setText(availableImageCreator.getNameResourceId());
//        System.out.println("text set");
////        editButton.setVisibility(availableImageCreator.isEditable() ? View.VISIBLE : View.GONE);
//        editButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                ((MainActivity) getContext()).openSubMenu(availableImageCreator.createEditFragment(), "editImageCreator");
//            }
//        });
//    }
//
//    public void reloadFromPreferences() {
//        imageCreator.loadFromPreferences(PreferenceManager.getDefaultSharedPreferences(getContext()));
//        Bitmap image = imageCreator.createImageForTargetDimensions(imagePreview.getWidth(), imagePreview.getHeight());
//        imagePreview.setImageToDraw(image);
//        // TODO: bei änderung (und nur dann) soll das bild neu erzeugt werden
//    }
//
//    @Override
//    public void toggle() {
//        checkButton.toggle();
//    }
//
//    @Override
//    public boolean isChecked() {
//        return checkButton.isChecked();
//    }
//
//    @Override
//    public void setChecked(boolean checked) {
//        checkButton.setChecked(checked);
//    }
//}
