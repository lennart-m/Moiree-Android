package de.lennartmeinhardt.android.moiree.util;

import android.os.Bundle;

public interface BundleIO {

    void storeToBundle(Bundle bundle);

    void loadFromBundle(Bundle bundle);
}
