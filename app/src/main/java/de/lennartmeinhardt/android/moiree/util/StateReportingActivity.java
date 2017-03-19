package de.lennartmeinhardt.android.moiree.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

// TODO kann potenziell weg. bringt anscheinend eh nix
public class StateReportingActivity extends AppCompatActivity {

    private final List<ActivityStateListener> listeners = new ArrayList<>();

    private ActivityState activityState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setState(ActivityState.CREATING);
        super.onCreate(savedInstanceState);
        System.out.println("a: onCreate");
    }

    @Override
    protected void onResume() {
        setState(ActivityState.RESUMING);
        super.onResume();
        System.out.println("a: onResume");
    }

    @Override
    protected void onPause() {
        setState(ActivityState.PAUSING);
        super.onPause();
        System.out.println("a: onPause");
    }

    @Override
    protected void onDestroy() {
        setState(ActivityState.DESTROYING);
        super.onDestroy();
        System.out.println("a: onDestroy");
    }

    @Override
    protected void onStart() {
        setState(ActivityState.STARTING);
        super.onStart();
        System.out.println("a: onStart");
    }

    @Override
    protected void onStop() {
        setState(ActivityState.STOPPING);
        super.onStop();
        System.out.println("a: onStop");
    }

    public ActivityState getActivityState() {
        return this.activityState;
    }

    private void setState(ActivityState activityState) {
        this.activityState = activityState;
        for(ActivityStateListener listener : listeners)
            listener.onActivityStateUpdated(activityState);
    }


    public enum ActivityState {
        CREATING,
        STARTING,
        STOPPING,
        RESUMING,
        PAUSING,
        DESTROYING
    }

    public interface ActivityStateListener {
        void onActivityStateUpdated(ActivityState newState);
    }
}
