package me.appfriends.androidsample.sampleapp.receipts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import me.appfriends.androidsample.R;
import me.appfriends.androidsample.sampleapp.LocalUsersDatabase;
import me.appfriends.ui.models.UserModel;

/**
 * Created by haowang on 3/1/17.
 */

public class MessageReceiptsAdapter extends SectionedRecyclerViewAdapter {

    static class ReceiptSection extends StatelessSection {

        private String sectionTitle;
        private int sectionIcon;
        private ArrayList<String> usersList;

        public ReceiptSection(int sectionIcon, String sectionTitle, List<String> userList) {
            super(R.layout.section_header_receipt, R.layout.item_user_list);
            this.sectionTitle = sectionTitle;
            this.sectionIcon = sectionIcon;
            this.usersList = new ArrayList<>(userList);
        }

        @Override
        public int getContentItemsTotal() {
            return usersList.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ReceiptUserView(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ReceiptUserView itemHolder = (ReceiptUserView) holder;
            String userID = this.usersList.get(position);
            UserModel user = LocalUsersDatabase.sharedInstance().getUserWithID(userID);
            itemHolder.userTitle.setText(user.getName());
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            final ReceiptHeaderView itemHolder = (ReceiptHeaderView) holder;
            itemHolder.icon.setImageResource(this.sectionIcon);
            itemHolder.headerTitle.setText(this.sectionTitle);
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            ReceiptHeaderView headerView = new ReceiptHeaderView(view);
            return headerView;
        }
    }

    static class ReceiptHeaderView extends RecyclerView.ViewHolder {

        private final ImageView icon;
        private final TextView headerTitle;

        public ReceiptHeaderView(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.section_icon);
            headerTitle = (TextView) itemView.findViewById(R.id.receipt_header_title);
        }
    }

    static class ReceiptUserView extends RecyclerView.ViewHolder {

        private final TextView userTitle;
        private final TextView userSubtitle;

        public ReceiptUserView(View itemView) {
            super(itemView);
            userTitle = (TextView) itemView.findViewById(R.id.user_item_name_title);
            userSubtitle = (TextView) itemView.findViewById(R.id.user_list_item_subtitle);
        }
    }
}