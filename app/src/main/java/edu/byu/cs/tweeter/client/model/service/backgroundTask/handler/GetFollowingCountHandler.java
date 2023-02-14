package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CountTaskObserver;

public class GetFollowingCountHandler extends BackgroundTaskHandler<CountTaskObserver> {

    public GetFollowingCountHandler(CountTaskObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(CountTaskObserver observer, Bundle data) {
        int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.setFollowing(count);
    }

}
