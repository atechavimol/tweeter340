package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends BackgroundTask {
    private static final String LOG_TAG = "GetFollowersCountTask";

    public static final String SUCCESS_KEY = "success";
    public static final String COUNT_KEY = "count";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * The user whose follower count is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private User targetUser;


    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.targetUser = targetUser;
    }


    @Override
    protected void processTask() {

    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putInt(COUNT_KEY, 20);
    }


}
