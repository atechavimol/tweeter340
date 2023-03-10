package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;

public class PagedNotificationHandler<T> extends BackgroundTaskHandler<PagedNotificationObserver> {

    public PagedNotificationHandler(PagedNotificationObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedNotificationObserver observer, Bundle data) {
        List<T> items = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.handleSuccess(items, hasMorePages);
    }
}
