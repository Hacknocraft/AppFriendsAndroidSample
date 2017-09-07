package me.appfriends.androidsample.sampleapp.contacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.appfriends.androidsample.R;
import me.appfriends.sdk.model.User;

/**
 * Created by haowang on 11/12/16.
 */

class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.UserHolder> {

    ContactsAdapterListener adapterListener;
    ArrayList<String> selectedUserIDs;

    private final LayoutInflater inflater;
    private final SortedList<User> sortedList;
    private final Comparator<User> comparator;

    private interface Action {
        void perform(SortedList<User> list);
    }

    ContactsAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.comparator = ALPHABETICAL_COMPARATOR;
        this.sortedList = new SortedList<>(User.class, new SortedList.Callback<User>() {
            @Override
            public int compare(User a, User b) {
                return ContactsAdapter.this.comparator.compare(a, b);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(User oldItem, User newItem) {
                return ContactsAdapter.this.areItemContentsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(User item1, User item2) {
                return ContactsAdapter.this.areItemsTheSame(item1, item2);
            }
        });

        selectedUserIDs = new ArrayList<>();
    }

    private User getItem(int position) {
        return sortedList.get(position);
    }

    private User getLastItem() {
        return sortedList.get(sortedList.size() - 1);
    }


    @Override
    public ContactsAdapter.UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = inflater.inflate(UserHolder.RESOURCE_ID, parent, false);

        return new UserHolder(itemView);
    }

    private boolean areItemsTheSame(User user1, User user2) {
        return user1.equals(user2);
    }

    private boolean areItemContentsTheSame(User oldItem, User newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public final void onBindViewHolder(UserHolder holder, int position) {
        final User item = sortedList.get(position);
        holder.bind(item);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, final int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        final User currentUser = getItem(position);
        holder.setUser(currentUser);

        // selection logic
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedUserIDs.contains(currentUser.getId())) {
                    deselectUser(currentUser);
                } else {
                    selectUser(currentUser);
                }
            }
        });

        // selection indicator
        holder.selectionIndicator.setSelected(this.selectedUserIDs.contains(currentUser.getId()));

        // set the section title
        if (position == 0) {
            holder.sectionHeader.setVisibility(View.VISIBLE);
        } else {
            User previousUserModel = getItem(position - 1);
            if (previousUserModel.getUserName().substring(0, 1)
                    .equalsIgnoreCase(currentUser.getUserName().substring(0, 1))) {
                holder.sectionHeader.setVisibility(View.INVISIBLE);
            } else {
                holder.sectionHeader.setVisibility(View.VISIBLE);
            }
        }

        // hide divider unless it's the last row of the section
        if (currentUser == getLastItem()) {
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            User nextUserModel = getItem(position + 1);
            if (nextUserModel.getUserName().substring(0, 1)
                    .equalsIgnoreCase(currentUser.getUserName().substring(0, 1))) {
                holder.divider.setVisibility(View.INVISIBLE);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
            }
        }
    }

    void selectUser(User user) {
        if (!selectedUserIDs.contains(user.getId())) {
            this.selectedUserIDs.add(user.getId());
            notifyItemChanged(positionForUser(user));
            if (this.adapterListener != null) {
                this.adapterListener.selectedUser(user);
            }
        }
    }

    void deselectUser(User user) {
        if (selectedUserIDs.contains(user.getId())) {
            this.selectedUserIDs.remove(user.getId());
            notifyItemChanged(positionForUser(user));
            if (this.adapterListener != null) {
                this.adapterListener.deselectedUser(user);
            }
        }
    }

    void addUsers(List<User> users) {
        this.edit().add(users).commit();
    }

    private int positionForUser(User user) {
        return this.sortedList.indexOf(user);
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    Editor edit() {
        return new EditorImpl();
    }

    private List<User> filter(Filter filter) {
        final List<User> list = new ArrayList<>();
        for (int i = 0, count = sortedList.size(); i < count; i++) {
            final User item = sortedList.get(i);
            if (filter.test(item)) {
                list.add(item);
            }
        }
        return list;
    }

    User filterOne(Filter filter) {
        for (int i = 0, count = sortedList.size(); i < count; i++) {
            final User item = sortedList.get(i);
            if (filter.test(item)) {
                return item;
            }
        }
        return null;
    }

    static class UserHolder extends RecyclerView.ViewHolder {

        private static final int RESOURCE_ID = R.layout.item_user_selection;

        private User currentItem;

        // View cache
        private TextView sectionHeader;
        private TextView userName;
        private TextView userSubtitle;
        private ImageView selectionIndicator;
        private View divider;

        private UserHolder(View itemView) {
            super(itemView);

            sectionHeader = (TextView) itemView.findViewById(R.id.user_list_section_title);
            userName = (TextView) itemView.findViewById(R.id.user_item_name_title);
            userSubtitle = (TextView) itemView.findViewById(R.id.user_list_item_subtitle);
            selectionIndicator = (ImageView) itemView.findViewById(R.id.user_list_item_check_indicator);
            divider = itemView.findViewById(R.id.user_list_item_divider);
        }

        private void bind(User item) {
            currentItem = item;
        }

        public void setUser(@NonNull User user) {

            sectionHeader.setText(user.getUserName().substring(0, 1).toUpperCase());
            userName.setText(user.getUserName());
        }
    }

    interface Editor {
        Editor add(User item);
        Editor add(List<User> items);
        Editor replaceAll(List<User> items);
        void commit();
    }

    interface Filter {
        boolean test(User item);
    }

    interface ContactsAdapterListener {
        void selectedUser(User user);
        void deselectedUser(User user);
    }

    private class EditorImpl implements Editor {

        private final List<Action> actions = new ArrayList<>();

        @Override
        public Editor add(final User item) {
            actions.add(new ContactsAdapter.Action() {
                @Override
                public void perform(SortedList<User> list) {
                    sortedList.add(item);
                }
            });
            return this;
        }

        @Override
        public Editor add(final List<User> items) {
            actions.add(new ContactsAdapter.Action() {
                @Override
                public void perform(SortedList<User> list) {
                    Collections.sort(items, comparator);
                    sortedList.addAll(items);
                }
            });
            return this;
        }


        @Override
        public Editor replaceAll(final List<User> items) {
            actions.add(new ContactsAdapter.Action() {
                @Override
                public void perform(SortedList<User> list) {
                    final List<User> itemsToRemove = filter(new Filter() {
                        @Override
                        public boolean test(User item) {
                            return !items.contains(item);
                        }
                    });
                    for (int i = itemsToRemove.size() - 1; i >= 0; i--) {
                        final User item = itemsToRemove.get(i);
                        sortedList.remove(item);
                    }
                    sortedList.addAll(items);
                }
            });
            return this;
        }

        @Override
        public void commit() {
            sortedList.beginBatchedUpdates();
            for (ContactsAdapter.Action action : actions) {
                action.perform(sortedList);
            }
            sortedList.endBatchedUpdates();
            actions.clear();
        }
    }

    public static final Comparator<User> ALPHABETICAL_COMPARATOR = new Comparator<User>() {
        @Override
        public int compare(User a, User b) {
            return a.getUserName().compareToIgnoreCase(b.getUserName());
        }
    };
}
