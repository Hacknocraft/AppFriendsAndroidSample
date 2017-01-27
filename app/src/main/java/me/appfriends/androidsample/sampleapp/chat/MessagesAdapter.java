package me.appfriends.androidsample.sampleapp.chat;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.appfriends.ui.chat.ChatMessagesAdapter;

/**
 * Created by haowang on 1/25/17.
 */

public class MessagesAdapter extends ChatMessagesAdapter {

    public MessagesAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindCellViewHolder(ChatMessagesAdapter.CellViewHolder viewHolder, int position) {
        super.bindCellViewHolder(viewHolder, position);

        // user initial
        if (viewHolder.initialLabel != null) {
            String initials = getInitials(viewHolder.message.senderName);
            if (initials != null) {
                viewHolder.initialLabel.setText(initials);
            } else if (viewHolder.message.senderName.length() >= 2) {
                viewHolder.initialLabel.setText(viewHolder.message.senderName.substring(0, 2).toUpperCase());
            } else {
                viewHolder.initialLabel.setText("");
            }
        }
    }

    private String getInitials(String fullName) {

        if (fullName.length() <= 0) {
            return null;
        }

        Pattern p = Pattern.compile("((^| )[A-Za-z])");
        Matcher m = p.matcher(fullName);
        String initials="";
        while(m.find()){
            initials += m.group().trim();
        }
        return initials.toUpperCase();
    }
}
