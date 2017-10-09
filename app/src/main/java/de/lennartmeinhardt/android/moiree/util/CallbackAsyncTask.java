package de.lennartmeinhardt.android.moiree.util;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public abstract class CallbackAsyncTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {

    private List<Callback<? super Result>> doneCallbacks;

    private Result lastResult;
    private boolean finished;


    public CallbackAsyncTask() {
        finished = false;
    }


    @Override
    protected void onPostExecute(Result result) {
        finished = true;
        lastResult = result;

        if(doneCallbacks != null) {
            for(Callback<? super Result> doneCallback : doneCallbacks)
                doneCallback.call(result);
        }
    }

    public CallbackAsyncTask<Param, Progress, Result> completed(Callback<? super Result> doneCallback) {
        if(finished)
            doneCallback.call(lastResult);

        if(doneCallbacks == null)
            doneCallbacks = new ArrayList<>();

        doneCallbacks.add(doneCallback);

        return this;
    }


    public interface Callback<Result> {

        void call(Result result);

    }
}
