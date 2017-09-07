package me.appfriends.androidsample.sampleapp.dialogs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import me.appfriends.androidsample.R;
import me.appfriends.androidsample.sampleapp.LocalUsersDatabase;
import me.appfriends.androidsample.sampleapp.chat.ChatActivity;
import me.appfriends.androidsample.sampleapp.contacts.ContactsPickerActivity;
import me.appfriends.sdk.model.Dialog;
import me.appfriends.sdk.model.User;
import me.appfriends.ui.dialog.DialogActivity;
import me.appfriends.ui.dialoglist.DialogListRecyclerView;
import me.appfriends.ui.dialoglist.OnDialogClickListener;

/**
 * Created by Mike Dai Wang on 2016-11-02.
 */

public class DialogListFragment extends Fragment implements DialogListContract.View {
    public static final int REQUEST_CODE_DIALOG_STARTER = 0;

    private ProgressDialog progress;
    private DialogListPresenter presenter;

    public static DialogListFragment createInstance() {
        return new DialogListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dialog_list, container, false);

        final DialogListRecyclerView recyclerView
                = (DialogListRecyclerView) view.findViewById(R.id.dialogs_list_recycler_view);

        recyclerView.setOnDialogClickListener(new OnDialogClickListener<Dialog>() {
            @Override
            public void onDialogClick(Dialog dialog) {
                onDialogClicked(dialog);
            }

            @Override
            public void onDialogLongClick(Dialog dialog) {

            }
        });

        final FloatingActionButton fabButton = (FloatingActionButton)  view.findViewById(R.id.dialog_starter_button);

        fabButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onCreateDialogClicked();
            }
        });

        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        presenter = new DialogListPresenter();
        presenter.attachView(this);

        return view;
    }

    protected void onDialogClicked(Dialog dialog) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(DialogActivity.EXTRA_DIALOG_ID, dialog.getId());
        getActivity().startActivity(intent);
    }

    protected void onCreateDialogClicked() {
        Intent intent = new Intent(getActivity(), ContactsPickerActivity.class);
        this.startActivityForResult(intent, REQUEST_CODE_DIALOG_STARTER);
//        Intent intent = new Intent(getActivity(), DialogListActivity.class);
//        this.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == REQUEST_CODE_DIALOG_STARTER
                && resultCode == ContactsPickerActivity.ACTIVITY_RESULT_CODE_CONTACT_PICKING) {

            ArrayList<String> pickedUsers = (ArrayList<String>) data.getExtras().get(getString(R.string.EXTRA_CONTACTS_PICK));
            if (pickedUsers != null && !pickedUsers.isEmpty()) {

                progress.show();
                progress.setContentView(me.appfriends.ui.R.layout.af_progressdialog);

                if (pickedUsers.size() == 1) {
                    // individual conversation, use username
                    String userID = pickedUsers.get(0);
                    User user = LocalUsersDatabase.sharedInstance().getUserWithID(userID);
                    presenter.createDialog(user.getUserName(), pickedUsers, null, null);
                } else {
                    // group conversation, use a default name
                    presenter.createDialog("", pickedUsers, "test custom data", "test push data");
                }
            }
        }
    }

    @Override
    public void onCreateDialogSuccess(Dialog dialog) {
        if (progress != null) {
            progress.dismiss();
        }

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(DialogActivity.EXTRA_DIALOG_ID, dialog.getId());
        intent.putExtra(DialogActivity.EXTRA_SHOW_KEYBOARD_ON_LOAD, true);
        getActivity().startActivity(intent);
    }

    @Override
    public void onCreateDialogError() {
        if (progress != null) {
            progress.dismiss();
        }
        Toast.makeText(getActivity(), "Failed to create dialog", Toast.LENGTH_SHORT).show();
    }
}
