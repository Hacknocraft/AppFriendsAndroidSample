package me.appfriends.androidsample.sampleapp.receipts;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import me.appfriends.androidsample.R;
import me.appfriends.sdk.AppFriends;
import me.appfriends.ui.base.BaseActivity;

/**
 * Created by haowang on 3/1/17.
 */

public class MessageReceiptsActivity extends BaseActivity implements MessageReceiptsContract.View {

    public static final String EXTRA_MESSAGE_ID = "EXTRA_MESSAGE_ID";
    public static final String EXTRA_DIALOG_MEMBERS = "EXTRA_DIALOG_MEMBERS";

    private String messageTempID;

    private Toolbar navigationBar;
    private RecyclerView receiptTable;
    private MessageReceiptsAdapter messageReceiptsAdapter;

    private ProgressDialog progressDialog;
    private MessageReceiptsPresenter presenter;
    private ArrayList<String> dialogMembers;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.messageTempID = getIntent().getStringExtra(EXTRA_MESSAGE_ID);
        this.dialogMembers = getIntent().getStringArrayListExtra(EXTRA_DIALOG_MEMBERS);
        setContentView(R.layout.activity_receipts);

        navigationBar = (Toolbar) findViewById(R.id.message_detail_navigation_bar);
        setNavigationBar();

        receiptTable = (RecyclerView) findViewById(R.id.receipt_table);

        messageReceiptsAdapter = new MessageReceiptsAdapter();
        receiptTable.setLayoutManager(new LinearLayoutManager(this));
        receiptTable.setAdapter(messageReceiptsAdapter);

        progressDialog = ProgressDialog.show(this, null, getString(me.appfriends.ui.R.string.af_please_wait), true);
        presenter = new MessageReceiptsPresenter();
        presenter.attachView(this);
        presenter.loadReceipts(this.messageTempID);
    }

    @Override
    protected void onDestroy() {

        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroy();
    }

    private void setNavigationBar() {
        navigationBar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.af_ic_back, null));
        navigationBar.setNavigationOnClickListener(onBackNavigationClickListener);
        navigationBar.setTitle(R.string.message_receipts);
    }

    @Override
    public void onReceiptsLoaded(List<String> users) {

        progressDialog.dismiss();
        ArrayList<String> notSeenMembers = new ArrayList<>();
        notSeenMembers.addAll(this.dialogMembers);
        String currentUserID = AppFriends.getInstance().currentLoggedInUserId();
        if (currentUserID != null) {
            notSeenMembers.remove(currentUserID);
        }
        notSeenMembers.removeAll(users);
        messageReceiptsAdapter.addSection(new MessageReceiptsAdapter
                .ReceiptSection(R.drawable.ic_check_green_24dp, getString(R.string.seen), users));
        messageReceiptsAdapter.addSection(new MessageReceiptsAdapter
                .ReceiptSection(R.drawable.ic_clear_red_24dp, getString(R.string.not_seen), notSeenMembers));
        messageReceiptsAdapter.notifyDataSetChanged();
    }
}
