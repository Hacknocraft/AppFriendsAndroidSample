package me.appfriends.androidsample;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import me.appfriends.androidsample.sampleapp.LocalUsersDatabase;
import me.appfriends.sdk.AppFriends;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Mike Dai Wang on 2016-11-02.
 */

public class AppFriendsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LocalUsersDatabase.sharedInstance().loadUsers(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        AppFriends instance = AppFriends.getInstance();
        instance.init(getApplicationContext());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
