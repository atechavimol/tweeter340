package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface PagedNotificationObserver extends ServiceObserver{

    <T> void handleSuccess(List<T> items, Boolean hasMorePages);
}
