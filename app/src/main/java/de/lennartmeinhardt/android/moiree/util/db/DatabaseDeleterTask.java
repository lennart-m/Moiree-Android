package de.lennartmeinhardt.android.moiree.util.db;

import java.util.Arrays;
import java.util.List;

import de.lennartmeinhardt.android.moiree.util.CallbackAsyncTask;

public class DatabaseDeleterTask<T> extends CallbackAsyncTask<T, Void, List<T>> {

    private final Deleter<T> deleter;

    public DatabaseDeleterTask(Deleter<T> deleter) {
        this.deleter = deleter;
    }


    @Override
    protected List<T> doInBackground(T... ts) {
        List<T> tsAsList = Arrays.asList(ts);
        deleter.deleteAll(tsAsList);
        return tsAsList;
    }
}
