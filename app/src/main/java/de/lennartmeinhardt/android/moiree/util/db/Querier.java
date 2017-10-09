package de.lennartmeinhardt.android.moiree.util.db;

import java.util.List;

public interface Querier <T> {

    List<T> queryAll();

}
