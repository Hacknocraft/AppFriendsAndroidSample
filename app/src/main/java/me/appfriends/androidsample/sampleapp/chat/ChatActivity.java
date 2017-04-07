package me.appfriends.androidsample.sampleapp.chat;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import me.appfriends.androidsample.sampleapp.dialogsettings.DialogSettingsActivity;
import me.appfriends.androidsample.sampleapp.receipts.MessageReceiptsActivity;
import me.appfriends.sdk.AppFriends;
import me.appfriends.sdk.model.Dialog;
import me.appfriends.sdk.model.Message;
import me.appfriends.ui.dialog.DialogActivity;

import static me.appfriends.androidsample.sampleapp.receipts.MessageReceiptsActivity.EXTRA_DIALOG_MEMBERS;
import static me.appfriends.androidsample.sampleapp.receipts.MessageReceiptsActivity.EXTRA_MESSAGE_ID;

/**
 * Created by haowang on 12/4/16.
 */

public class ChatActivity extends DialogActivity {

    public static final int REQUEST_CODE_DIALOG_SETTINGS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatView.updateAdapter(new MessagesAdapter(this));
    }

    @Override
    public String subTitleText() {
        return "subtitle";
    }

    @Override
    public void goToSettings(final Dialog dialog) {
        super.goToSettings(dialog);

        Intent intent = new Intent(this, DialogSettingsActivity.class);
        intent.putExtra(DialogActivity.EXTRA_DIALOG_ID, dialog.id);
        startActivityForResult(intent, REQUEST_CODE_DIALOG_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_DIALOG_SETTINGS
                && resultCode == DialogSettingsActivity.ACTIVITY_RESULT_CODE_LEFT_DIALOG) {
            finish();
        }
    }

    @Override
    public void onTextMessageTapped(Message message) {

        // just show receipts view for messages sent by the current user
        String currentUserID = AppFriends.getInstance().currentLoggedInUserId();
        if (message != null && dialog != null && message.senderId.equals(currentUserID)) {
            Intent intent = new Intent(this, MessageReceiptsActivity.class);
            intent.putExtra(EXTRA_MESSAGE_ID, message.tempId);
            ArrayList<String> members = new ArrayList();
            members.addAll(dialog.memberIds);
            intent.putStringArrayListExtra(EXTRA_DIALOG_MEMBERS, members);
            intent.putExtra(DialogActivity.EXTRA_DIALOG_ID, dialog.id);
            startActivity(intent);
        }
    }
}
