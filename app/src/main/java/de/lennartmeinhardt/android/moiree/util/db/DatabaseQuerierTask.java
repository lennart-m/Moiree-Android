package de.lennartmeinhardt.android.moiree.util.db;

import java.util.List;

import de.lennartmeinhardt.android.moiree.util.CallbackAsyncTask;

public class DatabaseQuerierTask<T> extends CallbackAsyncTask<Void, Void, List<T>> {

    private final Querier<T> querier;

    public DatabaseQuerierTask(Querier<T> querier) {
        this.querier = querier;
    }


    @Override
    protected List<T> doInBackground(Void... voids) {
        return querier.queryAll();
    }
}
