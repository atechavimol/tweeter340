package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends BackgroundTask {
    private static final String LOG_TAG = "GetUserTask";

    public static final String SUCCESS_KEY = "success";
    public static final String USER_KEY = "user";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * Auth token for logged-in user.
     */
    private AuthToken authToken;
    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private String alias;
    /**
     * Message handler that will receive task results.
     */

    private User user;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
        this.alias = alias;
    }


    @Override
    protected void processTask() {
        user = getUser();
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
    }


    private User getUser() {
        User user = getFakeData().findUserByAlias(alias);
        return user;
    }


}
