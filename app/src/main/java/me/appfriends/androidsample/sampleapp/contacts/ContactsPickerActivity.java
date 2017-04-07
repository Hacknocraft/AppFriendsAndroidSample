package me.appfriends.androidsample.sampleapp.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import me.appfriends.androidsample.R;
import me.appfriends.androidsample.sampleapp.LocalUsersDatabase;
import me.appfriends.ui.models.UserModel;

public class ContactsPickerActivity extends AppCompatActivity
        implements TokenCompleteTextView.TokenListener<UserModel>,
        ContactsSearchInputView.ContactsSearchInputViewListener,
        ContactsAdapter.ContactsAdapterListener {


    RecyclerView recyclerView;

    private List<UserModel> usersList;
    private ContactsAdapter adapter;
    private ContactsSearchInputView contactsSearchInputView;

    public static final int ACTIVITY_RESULT_CODE_CONTACT_PICKING = 0;
    public static final String EXTRA_EXCLUDE_USERS = "EXTRA_EXCLUDE_USERS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_picker);

        ImageButton finishButton = (ImageButton) findViewById(R.id.contacts_picker_finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishWithPickResults();
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.contacts_picker_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        List<UserModel> allUsers = LocalUsersDatabase.sharedInstance().getSeededUsers();
        ArrayList<UserModel> includedUsers = new ArrayList();
        includedUsers.addAll(allUsers);
        List<String> excludedUsers = getIntent().getStringArrayListExtra(EXTRA_EXCLUDE_USERS);
        if (excludedUsers != null) {
            for (UserModel user : allUsers) {
                if (excludedUsers.contains(user.id)) {
                    includedUsers.remove(user);
                }
            }
        }

        usersList = includedUsers;

        contactsSearchInputView = (ContactsSearchInputView) findViewById(R.id.contact_picker_search_field);
        contactsSearchInputView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersList.toArray()));
        contactsSearchInputView.setTokenListener(this);
        contactsSearchInputView.filterListener = this;
        contactsSearchInputView.addTextChangedListener(new SearchTextWatch());

        recyclerView = (RecyclerView) findViewById(R.id.contacts_picker_recycler_view);

        setupRecyclerView();
    }

    private void setupRecyclerView() {

        adapter = new ContactsAdapter(this);
        adapter.addUsers(usersList);
        adapter.adapterListener = this;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void finishWithPickResults() {

        if (adapter.selectedUserIDs.size() > 0) {

            Intent resultData = new Intent();
            resultData.putExtra(getString(R.string.EXTRA_CONTACTS_PICK), adapter.selectedUserIDs);
            setResult(ACTIVITY_RESULT_CODE_CONTACT_PICKING, resultData);
            finish();
        }
    }

    @Override
    public void onTokenAdded(UserModel token) {

        adapter.edit().replaceAll(usersList).commit();
        adapter.selectUser(token);
    }

    @Override
    public void onTokenRemoved(UserModel token) {

        adapter.edit().replaceAll(usersList).commit();
        adapter.deselectUser(token);
    }

    @Override
    public void currentTokenTextChanged(CharSequence text) {

        if (text.length() > 0) {
            final List<UserModel> filteredUserModelList = filter(usersList, text.toString());
            adapter.edit().replaceAll(filteredUserModelList).commit();
        } else {
            adapter.edit().replaceAll(usersList).commit();
        }
        recyclerView.scrollToPosition(0);
    }

    private static List<UserModel> filter(List<UserModel> userModels, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<UserModel> filteredUserModelList = new ArrayList<>();
        for (UserModel userModel : userModels) {
            final String userName = userModel.getName().toLowerCase();
            if (userName.contains(lowerCaseQuery)) {
                filteredUserModelList.add(userModel);
            }
        }
        return filteredUserModelList;
    }

    @Override
    public void selectedUser(UserModel userModel) {
        contactsSearchInputView.addObject(userModel);
    }

    @Override
    public void deselectedUser(UserModel userModel) {
        contactsSearchInputView.removeObject(userModel);
    }

    private class SearchTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() == 0) {
                adapter.edit().replaceAll(usersList).commit();
                recyclerView.scrollToPosition(0);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
