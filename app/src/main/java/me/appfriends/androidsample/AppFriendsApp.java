package me.appfriends.androidsample;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import me.appfriends.androidsample.sampleapp.LocalUsersDatabase;
import me.appfriends.sdk.AppFriends;

/**
 * Created by Mike Dai Wang on 2016-11-02.
 */

public class AppFriendsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LocalUsersDatabase.sharedInstance().loadUsers(this);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
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