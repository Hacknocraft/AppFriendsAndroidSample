package me.appfriends.androidsample.sampleapp.contacts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import me.appfriends.androidsample.R;
import me.appfriends.ui.models.UserModel;

/**
 * Created by haowang on 11/12/16.
 */

public class ContactsSearchInputView extends TokenCompleteTextView<UserModel> {

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
    public void addObject(UserModel object, CharSequence sourceText) {
        super.addObject(object, sourceText);
    }

    @Override
    public void addObject(UserModel object) {
        this.deleteText();
        super.addObject(object);
    }

    public ContactsSearchInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(UserModel userModel) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        view.setText(userModel.getName());

        return view;
    }

    @Override
    protected UserModel defaultObject(String completionText) {

        ArrayAdapter adapter = (ArrayAdapter) getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {

            UserModel userModel = (UserModel) adapter.getItem(i);
            if (userModel.getName().toLowerCase().contains(completionText.toLowerCase())) {
                return userModel;
            }
        }

        return new UserModel("", completionText);
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
