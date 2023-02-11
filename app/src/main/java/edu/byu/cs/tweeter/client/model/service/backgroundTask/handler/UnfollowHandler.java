package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;


import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;

public class UnfollowHandler extends SimpleNotificationHandler{

    public UnfollowHandler(FollowService.FollowUnfollowObserver observer) {
        super(observer);
    }


}
