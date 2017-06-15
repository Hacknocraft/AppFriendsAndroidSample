package me.appfriends.androidsample.sampleapp.dialogsettings;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.appfriends.androidsample.R;
import me.appfriends.androidsample.sampleapp.LocalUsersDatabase;
import me.appfriends.sdk.model.Dialog;
import me.appfriends.ui.base.BaseAdapter;
import me.appfriends.ui.models.UserModel;

/**
 * Created by bigtom on 16/11/13.
 */

public class DialogSettingsAdapter extends BaseAdapter<Dialog, DialogSettingsAdapter.DialogSettingsHolder> {

    public static final int GROUP_NAME_EDIT_NAME_TYPE = 0;
    public static final int MUTE_CONVERSATION_ITEM_TYPE = 1;
    public static final int ADD_GROUP_MEMBER_ITEM_TYPE = 2;
    public static final int LEAVE_CONVERSATION_ITEM_TYPE = 3;
    public static final int GROUP_MEMBER_ITEM_TYPE = 4;

    private Dialog dialog;
    private DialogSettingsAdapterClickListener dialogSettingsAdapterClickListener;

    public DialogSettingsAdapter(DialogSettingsAdapterClickListener listener, Dialog dialog) {
        super();

        dialogSettingsAdapterClickListener = listener;
        this.dialog = dialog;
    }

    public interface DialogSettingsAdapterClickListener {
        void onMuteDialog(boolean mute);
        void leaveConversation();
        void addGroupMembers();
        void onDialogNameChange(String newName);
    }

    public void updateDialog(Dialog updatedDialog) {
        if (updatedDialog != null) {
            this.dialog = updatedDialog;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public DialogSettingsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view;
        switch (viewType) {
            case GROUP_NAME_EDIT_NAME_TYPE:
                view = layoutInflater.inflate(R.layout.item_dialog_settings_edit_name, parent, false);
                break;
            case ADD_GROUP_MEMBER_ITEM_TYPE:
                view = layoutInflater.inflate(R.layout.item_dialog_settings_group_members, parent, false);
                break;
            case MUTE_CONVERSATION_ITEM_TYPE:
                view = layoutInflater.inflate(R.layout.item_dialog_settings_mute, parent, false);
                break;
            case LEAVE_CONVERSATION_ITEM_TYPE:
                view = layoutInflater.inflate(R.layout.item_dialog_settings_leave, parent, false);
                break;
            case GROUP_MEMBER_ITEM_TYPE:
                view = layoutInflater.inflate(R.layout.item_user_list, parent, false);
                break;
            default:
                return null;
        }

        final DialogSettingsHolder holder = new DialogSettingsHolder(view, dialogSettingsAdapterClickListener);

        return holder;
    }

    @Override
    public int getItemCount() {
        if (this.dialog.type == Dialog.DialogType.GROUP) {
            return 4 + dialog.memberIds.size();
        } else {
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(final DialogSettingsHolder holder, int position) {

        if (holder.groupNameText != null) {
            holder.groupNameText.setText(dialog.name);
        }

        if (holder.muteSwitch != null) {
            holder.muteSwitch.setChecked(dialog.muted);
        }

        if (holder.userNameTitle != null && holder.userNameSubtitle != null) {

            String userID = dialog.memberIds.get(position - 3);
            UserModel user = LocalUsersDatabase.sharedInstance().getUserWithID(userID);
            if (user != null) {
                holder.userNameTitle.setText(user.getName());
            } else {
                holder.userNameTitle.setText("unknown user");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (this.dialog.type == Dialog.DialogType.GROUP) {

            if (position == 0) {
                return GROUP_NAME_EDIT_NAME_TYPE;
            } else if (position == 1) {
                return MUTE_CONVERSATION_ITEM_TYPE;
            } else if (position == 2) {
                return ADD_GROUP_MEMBER_ITEM_TYPE;
            } else if (position == getItemCount() - 1) {
                return LEAVE_CONVERSATION_ITEM_TYPE;
            } else {
                return GROUP_MEMBER_ITEM_TYPE;
            }
        } else {

            if (position == 0) {
                return MUTE_CONVERSATION_ITEM_TYPE;
            } else {
                return LEAVE_CONVERSATION_ITEM_TYPE;
            }
        }
    }

    public static class DialogSettingsHolder extends RecyclerView.ViewHolder {
        public Context context;

        private SwitchCompat muteSwitch;
        private Button leaveConversationButton;
        private RelativeLayout addGroupMembers;
        private TextView groupNameText;
        private TextView userNameTitle;
        private TextView userNameSubtitle;

        private DialogSettingsAdapterClickListener dialogSettingsAdapterClickListener;

        public DialogSettingsHolder(View itemView, DialogSettingsAdapterClickListener listener) {
            super(itemView);

            dialogSettingsAdapterClickListener = listener;

            context = itemView.getContext();

            groupNameText = (TextView) itemView.findViewById(R.id.dialog_setting_dialog_name_text);
            if (groupNameText != null) {
                groupNameText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogNamePopup(groupNameText.getText().toString());
                    }
                });
            }

            muteSwitch = (SwitchCompat) itemView.findViewById(R.id.mute_conversation_button);
            if (muteSwitch != null) {
                muteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (dialogSettingsAdapterClickListener != null) {
                            dialogSettingsAdapterClickListener.onMuteDialog(b);
                        }
                    }
                });
            }

            leaveConversationButton = (Button) itemView.findViewById(R.id.leave_conversation);
            if (leaveConversationButton != null) {
                leaveConversationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogSettingsAdapterClickListener != null) {
                            dialogSettingsAdapterClickListener.leaveConversation();
                        }
                    }
                });
            }

            addGroupMembers = (RelativeLayout) itemView.findViewById(R.id.group_members_add);
            if (addGroupMembers != null) {
                addGroupMembers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogSettingsAdapterClickListener != null) {
                            dialogSettingsAdapterClickListener.addGroupMembers();
                        }
                    }
                });
            }

            userNameTitle = (TextView) itemView.findViewById(R.id.user_item_name_title);
            userNameSubtitle = (TextView) itemView.findViewById(R.id.user_list_item_subtitle);
        }

        private void showDialogNamePopup(final String originName) {
            Context context = itemView.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_modify_group_name, null);
            final EditText groupNameEdit = (EditText) view.findViewById(R.id.dialog_setting_dialog_name_edit);
            groupNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                    if (keyCode == EditorInfo.IME_ACTION_DONE || keyCode == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        InputMethodManager manager = (InputMethodManager) textView.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (manager != null) {
                            manager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        }

                        return true;
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Modify Group Name")
                    .setView(view)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String s = groupNameEdit.getText().toString();
                            if (!s.isEmpty() && !s.equals(originName)) {
                                changeDialogName(s);
                            }
                        }
                    })
                    .create();
            alertDialog.show();
        }

        private void changeDialogName(String dialogName) {
            if (dialogSettingsAdapterClickListener != null) {
                dialogSettingsAdapterClickListener.onDialogNameChange(dialogName);
            }
        }
    }

}