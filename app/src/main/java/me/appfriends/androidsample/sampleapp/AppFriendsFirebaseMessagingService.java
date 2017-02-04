package me.appfriends.androidsample.sampleapp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by haowang on 2/1/17.
 */

public class AppFriendsFirebaseMessagingService extends FirebaseMessagingService {

    final static String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
    }
}
