package me.appfriends.androidsample.sampleapp.contacts;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import me.appfriends.androidsample.R;
import me.appfriends.sdk.model.User;

/**
 * Created by haowang on 11/12/16.
 */

public class ContactsSearchInputView extends TokenCompleteTextView<User> {

    public ContactsSearchInputViewListener filterListener;

    public interface ContactsSearchInputViewListener {

        void currentTokenTextChanged(CharSequence text);
    }

    public ContactsSearchInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ContactsSearchInputView(Context context) {
        super(context);
    }

    @Override
    public void addObject(User object, CharSequence sourceText) {
        super.addObject(object, sourceText);
    }

    @Override
    public void addObject(User object) {
        this.deleteText();
        super.addObject(object);
    }

    public ContactsSearchInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(User userModel) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        view.setText(userModel.getUserName());

        return view;
    }

    @Override
    protected User defaultObject(String completionText) {

        ArrayAdapter adapter = (ArrayAdapter) getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {

            User userModel = (User) adapter.getItem(i);
            if (userModel.getUserName().toLowerCase().contains(completionText.toLowerCase())) {
                return userModel;
            }
        }

        User user = new User() {
            private String userName;
            private String avatar;

            @Override
            public String getId() {
                return "";
            }

            @Override
            public String getUserName() {
                return userName == null ? " " : userName;
            }

            @Override
            public String getAvatar() {
                return avatar;
            }

            @Override
            public void setUserName(String userName) {
                this.userName = userName;
            }

            @Override
            public void setAvatar(String avatarUrl) {
                this.avatar = avatarUrl;
            }
        };
        user.setUserName(completionText);

        return user;
    }

    public void deleteText() {
        Editable text = getText();
        text.replace(text.length() - currentCompletionText().length(), text.length(), "");
    }

    @Override
    protected void performFiltering(@NonNull CharSequence text, int start, int end, int keyCode) {
        super.performFiltering(text, start, end, keyCode);

        CharSequence query = text.subSequence(start, end);
        if (filterListener != null) {
            filterListener.currentTokenTextChanged(query);
        }
    }
}
