package me.appfriends.androidsample.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import me.appfriends.androidsample.R;
import me.appfriends.sdk.AppFriends;
import me.appfriends.sdk.model.User;
import me.appfriends.ui.base.BaseAdapter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    RecyclerView loginTable;
    SeedUsersAdapter adapter;

    private Subscription loginSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginTable = (RecyclerView) findViewById(R.id.seek_user_table);
        loginTable.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SeedUsersAdapter(this);
        loginTable.setAdapter(adapter);

        String userId = AppFriends.getInstance().currentLoggedInUserId();
        if (userId != null && !userId.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        if (loginSubscription != null && !loginSubscription.isUnsubscribed()) {
            loginSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    private class SeedUsersAdapter extends BaseAdapter<User, SeedUsersAdapter.SeedUserViewHolder> {

        WeakReference<Activity> context;

        SeedUsersAdapter(final Activity context) {

            this.context = new WeakReference<>(context);
            ArrayList<User> usersArrayList = LocalUsersDatabase.sharedInstance().getSeededUsers();
            this.appendItems(usersArrayList);

            this.setOnItemSelectListener(new OnItemSelectListener() {
                @Override
                public void onItemSelected(int position) {
                    final User user = getItem(position);

                    final Toast loadingToast = Toast.makeText(context, "Logging in ...", Toast.LENGTH_LONG);
                    loadingToast.show();

                    if (loginSubscription != null && !loginSubscription.isUnsubscribed()) {
                        loginSubscription.unsubscribe();
                    }

                    loginSubscription = AppFriends.getInstance().login(user.getId(), user.getUserName(), user.getAvatar())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Boolean>() {
                                @Override
                                public void onCompleted() {
                                    // intentionally left empty
                                }

                                @Override
                                public void onError(Throwable e) {
                                    loadingToast.cancel();
                                    Log.e(TAG, e.getMessage());

                                    Toast.makeText(context, "failed to login", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onNext(Boolean loggedIn) {
                                    loadingToast.cancel();
                                    if (loggedIn) {
                                        // TODO: perform login and get the user inside main activity
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);

                                    }
                                }
                            });

                }
            });
        }

        @Override
        public SeedUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(SeedUserViewHolder.RESOURCE_ID, parent, false);
            final SeedUserViewHolder holder = new SeedUserViewHolder(itemView);

            holder.userAvatar = (ImageView) itemView.findViewById(R.id.user_list_avatar);
            holder.userNameLabel = (TextView) itemView.findViewById(R.id.user_item_name_title);

            return holder;
        }

        @Override
        public void onBindViewHolder(SeedUserViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            User item = getItem(position);
            holder.setUserObject(item);
            Glide.with(context.get()).load(item.getAvatar()).into(holder.userAvatar);
        }

        @Override
        public void setOnItemSelectListener(OnItemSelectListener listener) {
            super.setOnItemSelectListener(listener);
        }

        class SeedUserViewHolder extends RecyclerView.ViewHolder {

            private User userModelObject;
            ImageView userAvatar;
            TextView userNameLabel;

            public static final int RESOURCE_ID = R.layout.item_seed_user;

            public SeedUserViewHolder(View itemView) {
                super(itemView);
            }

            public User getUserModelObject() {
                return userModelObject;
            }

            public void setUserObject(User userModelObject) {
                this.userModelObject = userModelObject;
                userNameLabel.setText(userModelObject.getUserName());
            }
        }
    }
}
