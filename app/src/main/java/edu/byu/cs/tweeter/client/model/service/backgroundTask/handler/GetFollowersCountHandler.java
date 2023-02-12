package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CountTaskObserver;

public class GetFollowersCountHandler extends BackgroundTaskHandler<CountTaskObserver>{

    public GetFollowersCountHandler(CountTaskObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(CountTaskObserver observer, Bundle data) {
        int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
        observer.setFollowers(count);
    }


}
