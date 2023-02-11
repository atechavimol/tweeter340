package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;


import edu.byu.cs.tweeter.client.model.service.FollowService;

// FollowHandler
public class FollowHandler extends SimpleNotificationHandler {

    public FollowHandler(FollowService.FollowUnfollowObserver observer) {
        super(observer);
    }


//    @Override
//    protected void handleSuccessMessage(SimpleNotificationObserver observer, Bundle data) {
//        observer.handleSuccess();
//        observer.update(false);
//    }
}
