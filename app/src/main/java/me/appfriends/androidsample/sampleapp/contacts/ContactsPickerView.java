package me.appfriends.androidsample.sampleapp.contacts;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by haowang on 11/11/16.
 */

public class ContactsPickerView extends RecyclerView {

    public ContactsPickerView(Context context) {
        super(context);
    }

    public ContactsPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsPickerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
