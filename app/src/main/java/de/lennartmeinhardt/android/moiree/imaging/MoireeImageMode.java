package de.lennartmeinhardt.android.moiree.imaging;

// TODO kann in das MoireeImageFragment rein.
public enum MoireeImageMode {

    CHECKERBOARD {
        @Override
        public MoireeImageCreator getImageCreatorInstance() {
            return new CheckerboardImageCreator();
        }
    },
    RANDOM_PIXELS {
        @Override
        public MoireeImageCreator getImageCreatorInstance() {
            return new RandomPixelsImageCreator();
        }
    };

    public static final MoireeImageMode DEFAULT = CHECKERBOARD;

    public abstract MoireeImageCreator getImageCreatorInstance();

    public static MoireeImageMode valueOfOr(String name, MoireeImageMode defValue) {
        try {
            return valueOf(name);
        } catch(Exception e) {
            return DEFAULT;
        }
    }

    public static MoireeImageMode valueOfOrDefault(String name) {
        return valueOfOr(name, DEFAULT);
    }
}
