package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status>{

    public FeedPresenter(View view) {
        super(view);
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);

            statusService.getFeed(user, PAGE_SIZE, last, new GetFeedObserver());
        }
    }

    public void getUserProfile(String alias) {
        userService.getUserProfile(alias, new FeedPresenter.GetUserObserver() );
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

            last = (items.size() > 0) ? (Status) items.get(items.size() - 1) : null;
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
