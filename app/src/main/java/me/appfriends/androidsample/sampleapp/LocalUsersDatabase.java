package me.appfriends.androidsample.sampleapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import me.appfriends.androidsample.R;
import me.appfriends.sdk.model.User;

/**
 * Created by haowang on 12/3/16.
 */

public class LocalUsersDatabase {

    private JSONArray users;
    private static LocalUsersDatabase instance = null;
    private static Object mutex = new Object();

    public static LocalUsersDatabase sharedInstance() {
        synchronized (mutex) {
            if (instance == null) {
                instance = new LocalUsersDatabase();
            }
        }

        return instance;
    }

    public void loadUsers(Context context) {
        try {
            InputStream inStream = context.getResources().openRawResource(R.raw.user_seeds);
            int size = inStream.available();
            byte[] buffer = new byte[size];
            inStream.read(buffer);
            inStream.close();
            users = new JSONArray(new String(buffer, "UTF-8"));
        } catch (IOException ioException) {
            Log.d("LoginActivity", ioException.getMessage());
            users = null;
        } catch (JSONException jsonException) {
            Log.d("LoginActivity", jsonException.getMessage());
            users = null;
        }
    }

    public ArrayList getSeededUsers() {

        ArrayList<User> usersArrayList = new ArrayList<>();
        for (int i = 0; i < users.length(); i++) {
            try {
                JSONObject userObject = users.getJSONObject(i);
                JSONObject nameObject = userObject.getJSONObject("name");
                JSONObject loginObject = userObject.getJSONObject("login");
                JSONObject pictureObject = userObject.getJSONObject("picture");
                final String userID = loginObject.getString("md5");
                String userName = nameObject.getString("first") + " " + nameObject.getString("last");
                User user = new User() {
                    private String userName;
                    private String avatar;

                    @Override
                    public String getId() {
                        return userID;
                    }

                    @Override
                    public String getUserName() {
                        return userName == null ? " " : userName;
                    }

                    @Override
                    public String getAvatar() {
                        return avatar;
                    }

                    @Override
                    public void setUserName(String userName) {
                        this.userName = userName;
                    }

                    @Override
                    public void setAvatar(String avatarUrl) {
                        this.avatar = avatarUrl;
                    }
                };
                user.setUserName(userName);
                user.setAvatar(pictureObject.getString("thumbnail"));
                usersArrayList.add(user);
            } catch (JSONException exception) {
                Log.d("LoginActivity", exception.getMessage());
            }
        }

        return usersArrayList;
    }

    public User getUserWithID(String searchUserID) {

        User foundUserModel = null;
        for (int i = 0; i < users.length(); i++) {
            try {
                JSONObject userObject = users.getJSONObject(i);
                JSONObject nameObject = userObject.getJSONObject("name");
                JSONObject loginObject = userObject.getJSONObject("login");
                JSONObject pictureObject = userObject.getJSONObject("picture");
                final String userID = loginObject.getString("md5");
                String userName = nameObject.getString("first") + " " + nameObject.getString("last");
                User user = new User() {
                    private String userName;
                    private String avatar;

                    @Override
                    public String getId() {
                        return userID;
                    }

                    @Override
                    public String getUserName() {
                        return userName == null ? " " : userName;
                    }

                    @Override
                    public String getAvatar() {
                        return avatar;
                    }

                    @Override
                    public void setUserName(String userName) {
                        this.userName = userName;
                    }

                    @Override
                    public void setAvatar(String avatarUrl) {
                        this.avatar = avatarUrl;
                    }
                };
                user.setUserName(userName);
                user.setAvatar(pictureObject.getString("thumbnail"));
                if (userID.equals(searchUserID)) {
                    foundUserModel = user;
                }
            } catch (JSONException exception) {
                Log.d("LoginActivity", exception.getMessage());
            }
        }

        return foundUserModel;
    }
}
