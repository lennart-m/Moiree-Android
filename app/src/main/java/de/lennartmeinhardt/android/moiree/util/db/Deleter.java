package de.lennartmeinhardt.android.moiree.util.db;

import java.util.List;

public interface Deleter <T> {

    void deleteAll(List<T> object);

}
