package me.appfriends.androidsample;

import android.app.Application;

import me.appfriends.androidsample.sampleapp.LocalUsersDatabase;
import me.appfriends.sdk.AppFriends;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Mike Dai Wang on 2016-11-02.
 */

public class AppFriendsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LocalUsersDatabase.sharedInstance().loadUsers(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        AppFriends instance = AppFriends.getInstance();
        instance.init(getApplicationContext());
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            // crash reporting
        }
    }
}
