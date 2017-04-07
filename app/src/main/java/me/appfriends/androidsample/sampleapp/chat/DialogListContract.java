package me.appfriends.androidsample.sampleapp.chat;

import java.util.List;

import me.appfriends.sdk.model.Dialog;
import me.appfriends.ui.base.BaseContract;

/**
 * Created by Mike Dai Wang on 2016-11-02.
 */

public interface DialogListContract {
    interface View extends BaseContract.MvpView {
        void onDialogsLoaded(List<Dialog> dialogs);
        void onCreateDialogSuccess(Dialog dialog);
        void onCreateDialogError();
    }

    interface Presenter {
        void monitorDialogs();
        void createDialog(String name, List<String> pickedUserIds, String customData, String pushData);
    }
}
