package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CheckFollowerObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;

public class IsFollowerHandler extends BackgroundTaskHandler<CheckFollowerObserver> {

    public IsFollowerHandler(MainPresenter.IsFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(CheckFollowerObserver observer, Bundle data) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.setFollowButton(isFollower);
    }

}
