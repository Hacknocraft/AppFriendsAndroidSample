package me.appfriends.androidsample.sampleapp.receipts;

import java.util.List;

import me.appfriends.ui.base.BaseContract;

/**
 * Created by haowang on 3/1/17.
 */

public interface MessageReceiptsContract {

    interface View extends BaseContract.MvpView {
        void onReceiptsLoaded(List<String> users);
    }

    interface Presenter {
        void loadReceipts(String messageTempID);
    }
}
