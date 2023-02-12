package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateTaskObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticateTaskHandler extends BackgroundTaskHandler<AuthenticateTaskObserver> {

    public AuthenticateTaskHandler(AuthenticateTaskObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(AuthenticateTaskObserver observer, Bundle data) {
        User registeredUser = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(registeredUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.displayMessage("Hello " + Cache.getInstance().getCurrUser().getName());
        observer.startActivity(registeredUser);
    }
}
