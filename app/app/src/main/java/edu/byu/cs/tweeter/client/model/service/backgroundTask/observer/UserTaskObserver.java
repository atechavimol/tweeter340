package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import edu.byu.cs.tweeter.model.domain.User;

public interface UserTaskObserver extends ServiceObserver {
    void startActivity(User registeredUser);
}
