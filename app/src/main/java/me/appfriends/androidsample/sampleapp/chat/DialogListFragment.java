package me.appfriends.androidsample.sampleapp.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.appfriends.androidsample.R;
import me.appfriends.androidsample.sampleapp.LocalUsersDatabase;
import me.appfriends.androidsample.sampleapp.contacts.ContactsPickerActivity;
import me.appfriends.sdk.model.Dialog;
import me.appfriends.ui.base.BaseFragment;
import me.appfriends.ui.dialog.DialogActivity;
import me.appfriends.ui.dialoglist.DialogListAdapter;
import me.appfriends.ui.models.UserModel;

/**
 * Created by Mike Dai Wang on 2016-11-02.
 */

public class DialogListFragment extends BaseFragment implements DialogListContract.View {
    RecyclerView recyclerView;

    private DialogListAdapter adapter;
    private DialogListPresenter presenter;
    private ProgressDialog progress;

    public static DialogListFragment createInstance() {
        return new DialogListFragment();
    }

    public static final int REQUEST_CODE_DIALOG_STARTER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dialog_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.dialogs_list_recycler_view);

        setupRecyclerView();
        FloatingActionButton fabButton = (FloatingActionButton)  view.findViewById(R.id.dialog_starter_button);
        fabButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchDialogStarter();
            }
        });

        progress = new ProgressDialog(getContext());
        progress.setCancelable(false);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        presenter = new DialogListPresenter();
        presenter.attachView(this);
        presenter.monitorDialogs();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new DialogsAdapter(getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroyView();
    }

    private void launchDialogStarter() {

        Intent intent = new Intent(getActivity(), ContactsPickerActivity.class);
        this.startActivityForResult(intent, REQUEST_CODE_DIALOG_STARTER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == REQUEST_CODE_DIALOG_STARTER
                && resultCode == ContactsPickerActivity.ACTIVITY_RESULT_CODE_CONTACT_PICKING) {

            ArrayList<String> pickedUsers = (ArrayList<String>) data.getExtras().get(getString(R.string.EXTRA_CONTACTS_PICK));
            final Context context = getContext();
            if (pickedUsers != null && pickedUsers.size() > 0) {

                progress.show();
                progress.setContentView(me.appfriends.ui.R.layout.progressdialog);

                if (pickedUsers.size() == 1) {
                    // individual conversation, use username
                    String userID = pickedUsers.get(0);
                    UserModel user = LocalUsersDatabase.sharedInstance().getUserWithID(userID);
                    presenter.createDialog(user.getName(), pickedUsers, null, null);
                } else {
                    // group conversation, use a default name
                    presenter.createDialog("", pickedUsers, "test custom data", "test push data");
                }
            }
        }
    }

    @Override
    public void onDialogsLoaded(List<Dialog> dialogs) {
        adapter.swapItems(dialogs);
    }

    @Override
    public void onCreateDialogSuccess(Dialog dialog) {
        if (progress != null) {
            progress.dismiss();
        }

        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(DialogActivity.EXTRA_DIALOG_ID, dialog.id);
        intent.putExtra(DialogActivity.EXTRA_SHOW_KEYBOARD_ON_LOAD, true);
        getActivity().startActivity(intent);
    }

    @Override
    public void onCreateDialogError() {
        if (progress != null) {
            progress.dismiss();
        }
        Toast.makeText(getContext(), "Failed to create dialog", Toast.LENGTH_SHORT).show();
    }

    class DialogsAdapter extends DialogListAdapter {

        public DialogsAdapter(Context context) {
            super(context);
        }

        @Override
        public void dialogSelected(Dialog dialog) {

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(DialogActivity.EXTRA_DIALOG_ID, dialog.id);
            context.startActivity(intent);
        }
    }
}
