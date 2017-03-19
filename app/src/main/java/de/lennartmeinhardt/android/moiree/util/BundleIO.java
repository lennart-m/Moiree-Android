package de.lennartmeinhardt.android.moiree.util;

import android.os.Bundle;

// TODO weg
public interface BundleIO {

    void storeToBundle(Bundle bundle, String prefix);

    void loadFromBundle(Bundle bundle, String prefix);
}
