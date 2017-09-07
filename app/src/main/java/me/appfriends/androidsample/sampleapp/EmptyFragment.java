package me.appfriends.androidsample.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.appfriends.androidsample.R;
import me.appfriends.sdk.AppFriends;
import me.appfriends.sdk.model.User;
import me.appfriends.ui.ConversationsActivity;

/**
 * Created by Mike Dai Wang on 2016-11-02.
 */

public class EmptyFragment extends Fragment {
    public static EmptyFragment createInstance() {
        return new EmptyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_empty, container, false);

        ImageView userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
        TextView userNameLabel = (TextView) view.findViewById(R.id.user_name);
        Button logoutButton = (Button) view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        Button popoutButton = (Button) view.findViewById(R.id.btn_popout);
        popoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ConversationsActivity.class);
                startActivity(intent);
            }
        });

        String currentUserID = AppFriends.getInstance().currentLoggedInUserId();
        User currentUser = LocalUsersDatabase.sharedInstance().getUserWithID(currentUserID);
        userNameLabel.setText(currentUser.getUserName());
        if (currentUser.getAvatar() != null) {
            Glide.with(getContext()).load(currentUser.getAvatar()).into(userAvatar);
        }

        return view;
    }

    private void logout() {
        AppFriends.getInstance().logOut();
        getActivity().finish();
    }
}
