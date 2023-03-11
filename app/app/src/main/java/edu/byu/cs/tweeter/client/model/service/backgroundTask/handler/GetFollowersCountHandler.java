package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

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
