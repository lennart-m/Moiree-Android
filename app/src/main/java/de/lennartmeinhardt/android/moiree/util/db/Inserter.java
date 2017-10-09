package de.lennartmeinhardt.android.moiree.util.db;

import java.util.List;

public interface Inserter <T> {

    void insertAll(List<T> object);

}
