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
import me.appfriends.ui.chat.ChatPresenter;
import me.appfriends.ui.dialog.DialogActivity;
import me.appfriends.ui.dialog.DialogContract;
import me.appfriends.ui.dialog.DialogPresenter;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static me.appfriends.androidsample.sampleapp.contacts.ContactsPickerActivity.EXTRA_EXCLUDE_USERS;

/**
 * Created by bigtom on 16/11/13.
 */

public class DialogSettingsActivity extends BaseActivity
        implements DialogSettingsAdapter.DialogSettingsAdapterClickListener, DialogContract.View {
    public static final String TAG = DialogSettingsActivity.class.getSimpleName();

    public static final String EXTRA_DIALOG = "EXTRA_DIALOG";

    private RecyclerView recyclerView;
    private Toolbar navigationBar;

    private DialogSettingsAdapter adapter;

    private Dialog dialog;

    private DialogService dialogService;

    static public int ACTIVITY_RESULT_CODE_LEFT_DIALOG = 1;

    private DialogPresenter presenter;

    KProgressHUD hud;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dialog = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_DIALOG));
        setContentView(R.layout.activity_dialog_settings);

        recyclerView = (RecyclerView) findViewById(R.id.dialog_settings_items_view);
        setupRecyclerView();

        navigationBar = (Toolbar) findViewById(R.id.channel_chat_navigation_bar);
        setNavigationBar();

        dialogService = AppFriends.getInstance().dialogService();

        // listen to dialog changes
        presenter = new DialogPresenter();
        presenter.attachView(this);
        presenter.loadDialog(this.dialog.id);
    }

    public void onDestroy() {
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroy();
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

            hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();

        }

        presenter.leaveDialog(dialog.id, dialog.type);
    }

    @Override
    public void addGroupMembers() {

        Intent intent = new Intent(this, ContactsPickerActivity.class);
        if (dialog != null && dialog.memberIds != null) {
            intent.putStringArrayListExtra(EXTRA_EXCLUDE_USERS, new ArrayList(dialog.memberIds));
        }
        this.startActivityForResult(intent, ContactsPickerActivity.ACTIVITY_RESULT_CODE_CONTACT_PICKING);
    }

    @Override
    public void onDialogNameChange(String newName) {

        if (!newName.equals(dialog.name)) {

            final Context context = this;
            String name = newName.replace("\n", "").replace("\r", "");
            presenter.updateDialogName(dialog.id, name);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == ContactsPickerActivity.ACTIVITY_RESULT_CODE_CONTACT_PICKING) {

            ArrayList<String> pickedUsers = (ArrayList<String>) data.getExtras().get(getString(R.string.EXTRA_CONTACTS_PICK));
            presenter.addMembersToDialog(dialog.id, pickedUsers);
        }
    }

    @Override
    public void onDialogUpdated(Dialog updatedDialog) {
        this.dialog = updatedDialog;
        adapter.updateDialog(dialog);
    }

    @Override
    public void onDialogLoadingError(Throwable e) {
        Toast.makeText(this, "Failed to load dialog", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogMuted() {

    }

    @Override
    public void onDialogMutingError(Throwable e) {
        adapter.notifyDataSetChanged(); // revert the switch back
    }

    @Override
    public void onDialogMembersChanged() {
        Toast.makeText(this, "users added.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogMembersChangeError(Throwable e) {
        Toast.makeText(this, "failed to add users.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLeaveDialog() {
        if (hud != null) {
            hud.dismiss();
        }
        setResult(ACTIVITY_RESULT_CODE_LEFT_DIALOG, null);
        finish();
    }

    @Override
    public void onLeaveDialogError(Throwable e) {
        Toast.makeText(this, "failed to leave dialog.", Toast.LENGTH_SHORT).show();
        if (hud != null) {
            hud.dismiss();
        }
    }

    @Override
    public void onDialogNameChanged() {
        Toast.makeText(this, "Dialog name updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNameChangeError(Throwable e) {

        new AlertDialog.Builder(this)
                .setTitle(getString(me.appfriends.ui.R.string.error_title))
                .setMessage("Failed to change dialog name")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
