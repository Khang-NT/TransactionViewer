package com.github.khangnt.transactionviewer;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */
public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG)
            setUpStrictMode();
    }

    private void setUpStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }
}
