package me.appfriends.androidsample.sampleapp.dialogsettings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.parceler.Parcels;

import java.util.ArrayList;

import me.appfriends.androidsample.R;
import me.appfriends.androidsample.sampleapp.contacts.ContactsPickerActivity;
import me.appfriends.sdk.AppFriends;
import me.appfriends.sdk.DialogService;
import me.appfriends.sdk.model.Dialog;
import me.appfriends.ui.base.BaseActivity;
import me.appfriends.ui.base.DividerItemDecoration;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by bigtom on 16/11/13.
 */

public class DialogSettingsActivity extends BaseActivity implements DialogSettingsAdapter.DialogSettingsAdapterClickListener {
    public static final String TAG = DialogSettingsActivity.class.getSimpleName();

    public static final String EXTRA_DIALOG = TAG + "EXTRA_DIALOG";

    private RecyclerView recyclerView;
    private Toolbar navigationBar;

    private DialogSettingsAdapter adapter;

    private Dialog dialog;

    private DialogService dialogService;

    static public int ACTIVITY_RESULT_CODE_LEFT_DIALOG = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dialog = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_DIALOG));
        setContentView(R.layout.activity_dialog_settings);

        recyclerView = (RecyclerView) findViewById(R.id.dialog_settings_items_view);
        setupRecyclerView();

        navigationBar = (Toolbar) findViewById(R.id.channel_chat_navigation_bar);
        setNavigationBar();

        dialogService = AppFriends.getInstance().dialogService();
    }

    private void setNavigationBar() {
        navigationBar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_left_white_36dp, null));
        navigationBar.setNavigationOnClickListener(onBackNavigationClickListener);
        navigationBar.setTitle(R.string.message_settings);
    }

    private void setupRecyclerView() {
        adapter = new DialogSettingsAdapter(this, this.dialog);
        recyclerView.setAdapter(adapter);

        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_recycler);
        RecyclerView.ItemDecoration dividerDecoration = new DividerItemDecoration(dividerDrawable);
        recyclerView.addItemDecoration(dividerDecoration);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        InputMethodManager manager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null && getCurrentFocus() != null) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onMuteDialog(final boolean mute) {

        if (mute != dialog.muted) {

            final Context context = this;
            dialogService.updateDialog(dialog.id, mute)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onNext(Boolean success) {
                            if (mute) {
                                Toast.makeText(context, "Dialog muted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Dialog unmuted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void leaveConversation() {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Leave this conversation")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        exitConversation();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void exitConversation() {

        if (dialog.type == Dialog.DialogType.GROUP) {

            final KProgressHUD hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();

            final Context context = this;
            dialogService.leaveDialog(dialog.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onNext(Boolean success) {
                            hud.dismiss();
                            setResult(ACTIVITY_RESULT_CODE_LEFT_DIALOG, null);
                            finish();
                        }
                    });

        } else {
            // TODO: delete the private dialog here
            setResult(ACTIVITY_RESULT_CODE_LEFT_DIALOG, null);
            finish();
        }
    }

    @Override
    public void addGroupMembers() {

        Intent intent = new Intent(this, ContactsPickerActivity.class);
        this.startActivityForResult(intent, 0);
    }

    @Override
    public void onDialogNameChange(String newName) {

        if (!newName.equals(dialog.name)) {

            final Context context = this;
            String name = newName.replace("\n", "").replace("\r", "");
            dialogService.updateDialog(dialog.id, name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onNext(Boolean success) {
                            Toast.makeText(context, "Dialog name updated", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == 0 && resultCode == ContactsPickerActivity.ACTIVITY_RESULT_CODE_CONTACT_PICKING) {

            ArrayList<String> pickedUsers = (ArrayList<String>) data.getExtras().get(getString(R.string.EXTRA_CONTACTS_PICK));
            final Context context = this;
            final int count = pickedUsers.size();
            dialogService.addMembersToDialog(dialog.id, pickedUsers)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onNext(Boolean success) {
                            Toast.makeText(context, count + "users added.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
