package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter extends PagedPresenter<User>{

    public GetFollowersPresenter(View view) {
        super(view);
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);
            followService.loadMoreFollowers(user, PAGE_SIZE, last, new GetFollowersObserver());
        }
    }

    public void getUserProfile(String alias) {
        userService.getUserProfile(alias, new GetUserObserver());
    }

    public class GetFollowersObserver implements PagedNotificationObserver {
        @Override
        public <T> void handleSuccess(List<T> items, Boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            last = (items.size() > 0) ? (User) items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems((List<T>) items);
        }

        @Override
        public void displayMessage(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(message);
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





}
