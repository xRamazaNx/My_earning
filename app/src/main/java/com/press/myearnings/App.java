package com.press.myearnings;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.press.myearnings.model.CopyData;
import com.press.myearnings.setting.Setting;

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    private int countActivity;
    private boolean isLock;

    @Override
    public void onCreate() {
        super.onCreate();
        countActivity = 0;

        registerActivityLifecycleCallbacks(this);
        getIsLock();

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

        if (isLock)
            if (countActivity < 1)
                if (!(activity instanceof LockActivity)) {
                    Intent intent = new Intent(this, LockActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
        ++countActivity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        --countActivity;
        if (activity instanceof Setting) {
            getIsLock();
        }
        if (countActivity < 1)
            CopyData.getCopy().removeCopy();
    }

    private void getIsLock() {
        isLock = !PreferenceManager.getDefaultSharedPreferences(this)
                .getString(LockActivity.USER_PASS, "").equals("");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
