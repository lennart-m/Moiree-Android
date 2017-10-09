package de.lennartmeinhardt.android.moiree.util.db;

import java.util.Arrays;
import java.util.List;

import de.lennartmeinhardt.android.moiree.util.CallbackAsyncTask;

public class DatabaseInserterTask <T> extends CallbackAsyncTask<T, Void, List<T>> {

    private final Inserter<T> inserter;

    public DatabaseInserterTask(Inserter<T> inserter) {
        this.inserter = inserter;
    }


    @Override
    protected List<T> doInBackground(T... ts) {
        List<T> tsAsList = Arrays.asList(ts);
        inserter.insertAll(tsAsList);
        return tsAsList;
    }
}
