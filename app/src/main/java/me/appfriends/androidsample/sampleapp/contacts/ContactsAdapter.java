package me.appfriends.androidsample.sampleapp.contacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.appfriends.androidsample.R;
import me.appfriends.ui.base.BaseSortedListAdapter;
import me.appfriends.ui.models.UserModel;

import static me.appfriends.ui.models.UserModel.ALPHABETICAL_COMPARATOR;

/**
 * Created by haowang on 11/12/16.
 */

public class ContactsAdapter extends BaseSortedListAdapter<UserModel> {

    public ContactsAdapterListener adapterListener;
    public ArrayList<String> selectedUserIDs;

    public ContactsAdapter(Context context) {
        super(context, UserModel.class, ALPHABETICAL_COMPARATOR);
        selectedUserIDs = new ArrayList<String>();
    }

    @Override
    protected ViewHolder<? extends UserModel> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {

        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(ContactsAdapter.UserHolder.RESOURCE_ID, parent, false);
        final UserHolder holder = new ContactsAdapter.UserHolder(itemView);

        return holder;
    }

    @Override
    protected boolean areItemsTheSame(UserModel userModel1, UserModel userModel2) {
        return userModel1.equals(userModel2);
    }

    @Override
    protected boolean areItemContentsTheSame(UserModel oldItem, UserModel newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder<? extends UserModel> holder, final int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        UserHolder viewHolder = (UserHolder) holder;
        final UserModel currentUserModel = getItem(position);
        viewHolder.setUser(currentUserModel);

        // selection logic
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedUserIDs.contains(currentUserModel.id)) {
                    deselectUser(currentUserModel);
                } else {
                    selectUser(currentUserModel);
                }
            }
        });

        // selection indicator
        viewHolder.selectionIndicator.setSelected(this.selectedUserIDs.contains(currentUserModel.id));

        // set the section title
        if (position == 0) {
            viewHolder.sectionHeader.setVisibility(View.VISIBLE);
        } else {
            UserModel previousUserModel = getItem(position - 1);
            if (previousUserModel.capitalizedNameSection().equals(currentUserModel.capitalizedNameSection())) {
                viewHolder.sectionHeader.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.sectionHeader.setVisibility(View.VISIBLE);
            }
        }

        // hide divider unless it's the last row of the section
        if (currentUserModel == getLastItem()) {
            viewHolder.divider.setVisibility(View.VISIBLE);
        } else {
            UserModel nextUserModel = getItem(position + 1);
            if (nextUserModel.capitalizedNameSection().equals(currentUserModel.capitalizedNameSection())) {
                viewHolder.divider.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.divider.setVisibility(View.VISIBLE);
            }
        }
    }

    public void selectUser(UserModel userModel) {
        if (!selectedUserIDs.contains(userModel.id)) {
            this.selectedUserIDs.add(userModel.id);
            notifyItemChanged(positionForUser(userModel));
            if (this.adapterListener != null) {
                this.adapterListener.selectedUser(userModel);
            }
        }
    }

    public void deselectUser(UserModel userModel) {
        if (selectedUserIDs.contains(userModel.id)) {
            this.selectedUserIDs.remove(userModel.id);
            notifyItemChanged(positionForUser(userModel));
            if (this.adapterListener != null) {
                this.adapterListener.deselectedUser(userModel);
            }
        }
    }

    public void addUsers(List<UserModel> userModels) {
        this.edit().add(userModels).commit();
    }

    public int positionForUser(UserModel userModel) {
        return this.getSortedList().indexOf(userModel);
    }

    public interface ContactsAdapterListener {
        public void selectedUser(UserModel userModel);
        public void deselectedUser(UserModel userModel);
    }

    public static class UserHolder extends ViewHolder<UserModel> {

        public static final int RESOURCE_ID = R.layout.item_user_selection;

        // View cache
        protected TextView sectionHeader;
        protected TextView userName;
        protected TextView userSubtitle;
        protected ImageView selectionIndicator;
        protected View divider;

        public UserHolder(View itemView) {
            super(itemView);

            sectionHeader = (TextView) itemView.findViewById(R.id.user_list_section_title);
            userName = (TextView) itemView.findViewById(R.id.user_item_name_title);
            userSubtitle = (TextView) itemView.findViewById(R.id.user_list_item_subtitle);
            selectionIndicator = (ImageView) itemView.findViewById(R.id.user_list_item_check_indicator);
            divider = itemView.findViewById(R.id.user_list_item_divider);
        }

        public void setUser(@NonNull UserModel userModel) {

            sectionHeader.setText(userModel.capitalizedNameSection());
            userName.setText(userModel.getName());
        }
    }

}
