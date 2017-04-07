package me.appfriends.androidsample.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import me.appfriends.androidsample.R;
import me.appfriends.androidsample.sampleapp.chat.ChatActivity;
import me.appfriends.androidsample.sampleapp.chat.DialogListFragment;
import me.appfriends.sdk.AppFriends;
import me.appfriends.sdk.model.Message;
import me.appfriends.ui.base.BaseActivity;
import me.appfriends.ui.dialog.DialogActivity;
import rx.CompletableSubscriber;
import rx.Subscriber;
import rx.Subscription;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int FRAGMENT_RETAIN_OFFSET_LIMIT = 3;

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;

    Subscription messageSendMonitorSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupToolbar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        registerPushToken();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (messageSendMonitorSubscription != null && !messageSendMonitorSubscription.isUnsubscribed()) {
            messageSendMonitorSubscription.unsubscribe();
        }

        messageSendMonitorSubscription = AppFriends.getInstance().chatService().monitorMessageSendStatus()
                .subscribe(new Subscriber<Message.Type>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Message.Type type) {
                        // react to different message type here
                        Log.d(TAG, "Message sent type: " + type.toString());
                    }
                });
    }

    @Override
    public void onStop() {
        if (messageSendMonitorSubscription != null && !messageSendMonitorSubscription.isUnsubscribed()) {
            messageSendMonitorSubscription.unsubscribe();
            messageSendMonitorSubscription = null;
        }

        super.onStop();
    }

    private void registerPushToken() {
        String userId = AppFriends.getInstance().currentLoggedInUserId();
        if (userId != null && !userId.isEmpty()) {
            AppFriends.getInstance().pushService().updatePushToken(userId,
                    FirebaseInstanceId.getInstance().getToken())
                    .subscribe(new CompletableSubscriber() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "push token registered");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, e.getMessage());
                        }

                        @Override
                        public void onSubscribe(Subscription d) {

                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        AppFriends.getInstance().logout();
    }

    private void setupToolbar(Toolbar toolbar) {
        toolbar.setTitle("AppFriends");
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(getString(R.string.tab_empty), EmptyFragment.createInstance());
        adapter.addFragment(getString(R.string.tab_dialogs_list), DialogListFragment.createInstance());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(FRAGMENT_RETAIN_OFFSET_LIMIT);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<String> titles = new ArrayList<>();
        private List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public void addFragment(String name, Fragment fragment) {
            titles.add(name);
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("dialog_id")) {
                String dialogID = extras.getString("dialog_id");
                Intent chatIntent = new Intent(this, ChatActivity.class);
                chatIntent.putExtra(DialogActivity.EXTRA_DIALOG_ID, dialogID);
                startActivity(chatIntent);
            }

            if (extras.containsKey("push_data")) {
                String pushData = extras.getString("push_data");
                Log.d(TAG, "push data: " + pushData);
            }
        }
    }
}
