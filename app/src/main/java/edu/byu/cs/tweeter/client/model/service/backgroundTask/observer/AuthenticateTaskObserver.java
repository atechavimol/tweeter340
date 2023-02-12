package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticateTaskObserver extends ServiceObserver {
    void startActivity(User registeredUser);
}
