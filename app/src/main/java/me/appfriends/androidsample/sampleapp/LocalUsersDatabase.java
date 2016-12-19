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
import me.appfriends.ui.models.UserModel;

/**
 * Created by haowang on 12/3/16.
 */

public class LocalUsersDatabase {

    private JSONArray users;
    private Context appContext;
    private static LocalUsersDatabase instance = null;

    public static LocalUsersDatabase sharedInstance() {
        if (instance == null) {
            instance = new LocalUsersDatabase();
        }
        return instance;
    }

    public void loadUsers(Context context) {

        instance.appContext = context;
        try {
            InputStream inStream = appContext.getResources().openRawResource(R.raw.user_seeds);
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

        ArrayList<UserModel> usersArrayList = new ArrayList<>();
        for (int i = 0; i < users.length(); i++) {
            try {
                JSONObject userObject = users.getJSONObject(i);
                JSONObject nameObject = userObject.getJSONObject("name");
                JSONObject loginObject = userObject.getJSONObject("login");
                JSONObject pictureObject = userObject.getJSONObject("picture");
                String userID = loginObject.getString("md5");
                String userName = nameObject.getString("first") + " " + nameObject.getString("last");
                UserModel userModel = new UserModel(userID, userName);
                userModel.avatar = pictureObject.getString("thumbnail");
                usersArrayList.add(userModel);
            } catch (JSONException exception) {
                Log.d("LoginActivity", exception.getMessage());
            }
        }

        return usersArrayList;
    }

    public UserModel getUserWithID(String searchUserID) {

        UserModel foundUserModel = null;
        for (int i = 0; i < users.length(); i++) {
            try {
                JSONObject userObject = users.getJSONObject(i);
                JSONObject nameObject = userObject.getJSONObject("name");
                JSONObject loginObject = userObject.getJSONObject("login");
                JSONObject pictureObject = userObject.getJSONObject("picture");
                String userID = loginObject.getString("md5");
                String userName = nameObject.getString("first") + " " + nameObject.getString("last");
                UserModel userModel = new UserModel(userID, userName);
                userModel.avatar = pictureObject.getString("thumbnail");
                if (userID.equals(searchUserID)) {
                    foundUserModel = userModel;
                }
            } catch (JSONException exception) {
                Log.d("LoginActivity", exception.getMessage());
            }
        }

        return foundUserModel;
    }
}
