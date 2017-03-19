package de.lennartmeinhardt.android.moiree.imaging;

import android.content.Context;

// TODO kann in das MoireeImageFragment rein.
public enum MoireeImageMode {

    CHECKERBOARD {
        @Override
        public MoireeImageCreator getImageCreatorInstance(Context context) {
            return new CheckerboardImageCreator(context.getResources());
        }
    },
    RANDOM_PIXELS {
        @Override
        public MoireeImageCreator getImageCreatorInstance(Context context) {
            return new RandomPixelsImageCreator(context.getResources());
        }
    };

    public static final MoireeImageMode DEFAULT = CHECKERBOARD;

    public abstract MoireeImageCreator getImageCreatorInstance(Context context);


    public static MoireeImageMode valueOfOrDefault(String name) {
        try {
            return valueOf(name);
        } catch(Exception e) {
            return DEFAULT;
        }
    }
}
