package me.appfriends.androidsample.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import me.appfriends.ui.base.BaseAdapter;
import me.appfriends.ui.models.UserModel;
import rx.Subscriber;

public class LoginActivity extends AppCompatActivity {

    RecyclerView loginTable;
    SeedUsersAdapter adapter;

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

    private class SeedUsersAdapter extends BaseAdapter<UserModel, SeedUsersAdapter.SeedUserViewHolder> {

        WeakReference<Activity> context;

        SeedUsersAdapter(final Activity context) {

            this.context = new WeakReference<>(context);
            ArrayList<UserModel> usersArrayList = LocalUsersDatabase.sharedInstance().getSeededUsers();
            this.appendItems(usersArrayList);

            this.setOnItemSelectListener(new OnItemSelectListener() {
                @Override
                public void onItemSelected(int position) {
                    final UserModel userModel = getItem(position);

                    final Toast loadingToast = Toast.makeText(context, "Logging in ...", Toast.LENGTH_LONG);
                    loadingToast.show();

                    AppFriends.getInstance().login(userModel.id, userModel.getName())
                            .subscribe(new Subscriber<Boolean>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    loadingToast.cancel();
                                    e.printStackTrace();
                                    try {
                                        Toast.makeText(context, "failed to login", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                @Override
                                public void onNext(Boolean loggedIn) {
                                    loadingToast.cancel();
                                    if (loggedIn) {
                                        // TODO: perform login and get the userModel inside main activity
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

            UserModel userModel = getItem(position);
            holder.setUserModelObject(userModel);
            Glide.with(context.get()).load(userModel.getAvatar()).into(holder.userAvatar);
        }

        @Override
        public void setOnItemSelectListener(OnItemSelectListener listener) {
            super.setOnItemSelectListener(listener);
        }

        class SeedUserViewHolder extends RecyclerView.ViewHolder {

            private UserModel userModelObject;
            ImageView userAvatar;
            TextView userNameLabel;

            public static final int RESOURCE_ID = R.layout.item_seed_user;

            public SeedUserViewHolder(View itemView) {
                super(itemView);
            }

            public UserModel getUserModelObject() {
                return userModelObject;
            }

            public void setUserModelObject(UserModel userModelObject) {
                this.userModelObject = userModelObject;
                userNameLabel.setText(userModelObject.getName());
            }
        }
    }
}
