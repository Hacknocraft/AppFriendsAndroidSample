package me.appfriends.androidsample.sampleapp.dialogs;

import java.util.List;

import me.appfriends.sdk.AppFriends;
import me.appfriends.sdk.DialogService;
import me.appfriends.sdk.model.Dialog;
import me.appfriends.ui.base.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Mike Dai Wang on 2016-11-02.
 */

public class DialogListPresenter extends BasePresenter<DialogListContract.View> implements DialogListContract.Presenter {
    private static final String TAG = DialogListPresenter.class.getSimpleName();

    private final DialogService dialogService;

    public DialogListPresenter() {
        dialogService = AppFriends.getInstance().dialogService();
    }

    @Override
    public void createDialog(String name, List<String> pickedUserIds, String customData, String pushData) {
        Subscription createDialogSubscription = dialogService.createDialog(name, pickedUserIds, customData, pushData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Dialog>() {
                    @Override
                    public void onCompleted() {
                        // intentionally left empty
                    }

                    @Override
                    public void onError(Throwable e) {
                        DialogListContract.View view = getMvpView();

                        if (view != null) {
                            view.onCreateDialogError();
                        }
                    }

                    @Override
                    public void onNext(Dialog dialog) {
                        DialogListContract.View view = getMvpView();

                        if (view != null) {
                            view.onCreateDialogSuccess(dialog);
                        }
                    }
                });

        compositeSubscription.add(createDialogSubscription);
    }
}
