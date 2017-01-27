package me.appfriends.androidsample.sampleapp.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.parceler.Parcels;

import me.appfriends.androidsample.sampleapp.dialogsettings.DialogSettingsActivity;
import me.appfriends.sdk.AppFriends;
import me.appfriends.sdk.model.Dialog;
import me.appfriends.sdk.model.Message;
import me.appfriends.ui.dialog.DialogActivity;
import rx.Subscriber;

import static me.appfriends.androidsample.sampleapp.dialogsettings.DialogSettingsActivity.EXTRA_DIALOG;
import static me.appfriends.androidsample.sampleapp.dialogsettings.DialogSettingsActivity.TAG;

/**
 * Created by haowang on 12/4/16.
 */

public class ChatActivity extends DialogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatView.updateAdapter(new MessagesAdapter(this));

        AppFriends.getInstance().chatService().monitorMessageSendStatus()
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
    public void goToSettings(final Dialog dialog) {
        super.goToSettings(dialog);

        Intent intent = new Intent(this, DialogSettingsActivity.class);
        intent.putExtra(EXTRA_DIALOG, Parcels.wrap(dialog));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 0 && resultCode == DialogSettingsActivity.ACTIVITY_RESULT_CODE_LEFT_DIALOG) {

            finish();
        }
    }
}
