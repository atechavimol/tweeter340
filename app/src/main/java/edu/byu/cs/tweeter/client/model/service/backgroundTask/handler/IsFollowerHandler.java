package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowerObserver;

public class IsFollowerHandler extends BackgroundTaskHandler<IsFollowerObserver> {

    public IsFollowerHandler(FollowService.isFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(IsFollowerObserver observer, Bundle data) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.setFollowButton(isFollower);
    }

}
