package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter {
    private static final int PAGE_SIZE = 10;

    public interface View {

        void startActivity(User user);

        void displayMessage(String s);

        void setLoadingFooter(boolean b);

        void addMoreItems(List<Status> statuses);
    }

    private View view;
    private UserService userService;
    private StatusService statusService;


    private Status lastStatus;

    private boolean hasMorePages;
    private boolean isLoading = false;

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public FeedPresenter(View view) {
        this.view = view;
        userService = new UserService();
        statusService = new StatusService();
    }

    public void getUserProfile(String alias) {
        userService.getUserProfile(alias, new GetUserObserver() );
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);

            statusService.getFeed(user, PAGE_SIZE, lastStatus, new GetFeedObserver());
        }
    }

    public class GetUserObserver implements UserTaskObserver {

        @Override
        public void startActivity(User user) {
            view.startActivity(user);
        }

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }
    }

    public class GetFeedObserver implements PagedNotificationObserver {
        @Override
        public <T> void handleSuccess(List<T> items, Boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);

            lastStatus = (items.size() > 0) ? (Status) items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems((List<Status>) items);
        }

        @Override
        public void displayMessage(String message) {
            isLoading = false;
            view.setLoadingFooter(false);

            view.displayMessage(message);
        }

    }


}
