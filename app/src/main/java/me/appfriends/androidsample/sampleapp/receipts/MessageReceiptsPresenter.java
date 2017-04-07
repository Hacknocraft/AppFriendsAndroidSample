package me.appfriends.androidsample.sampleapp.receipts;

import java.util.List;

import me.appfriends.sdk.AppFriends;
import me.appfriends.sdk.DialogService;
import me.appfriends.ui.base.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by haowang on 3/1/17.
 */

public class MessageReceiptsPresenter extends BasePresenter<MessageReceiptsContract.View>
        implements MessageReceiptsContract.Presenter {

    private final DialogService dialogService;

    public MessageReceiptsPresenter() {
        dialogService = AppFriends.getInstance().dialogService();
    }

    @Override
    public void loadReceipts(String messageTempID) {

        Subscription receiptSubscription = AppFriends.getInstance().chatService().getReadReceipts(messageTempID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<String> users) {
                        MessageReceiptsContract.View view = getMvpView();
                        if (view != null) {
                            view.onReceiptsLoaded(users);
                        }
                    }
                });
        compositeSubscription.add(receiptSubscription);
    }
}
