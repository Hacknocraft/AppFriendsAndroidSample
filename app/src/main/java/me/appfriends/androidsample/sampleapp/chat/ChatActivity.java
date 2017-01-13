package me.appfriends.androidsample.sampleapp.chat;

import android.content.Intent;
import android.os.Bundle;

import org.parceler.Parcels;

import me.appfriends.androidsample.sampleapp.dialogsettings.DialogSettingsActivity;
import me.appfriends.ui.dialog.DialogActivity;

/**
 * Created by haowang on 12/4/16.
 */

public class ChatActivity extends DialogActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ability to hide settings icon
        // settingsButton.setVisibility(View.GONE);
    }

    @Override
    public void goToSettings() {
        super.goToSettings();

        Intent intent = new Intent(this, DialogSettingsActivity.class);
        intent.putExtra("dialog", Parcels.wrap(this.dialog));
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
