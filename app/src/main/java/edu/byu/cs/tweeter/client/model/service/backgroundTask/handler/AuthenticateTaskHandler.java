package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticateTaskHandler extends BackgroundTaskHandler<UserTaskObserver> {

    public AuthenticateTaskHandler(UserTaskObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserTaskObserver observer, Bundle data) {
        User user = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.displayMessage("Hello " + Cache.getInstance().getCurrUser().getName());
        observer.startActivity(user);
    }
}
