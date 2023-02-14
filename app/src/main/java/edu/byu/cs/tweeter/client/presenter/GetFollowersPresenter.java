package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter {
    private static final int PAGE_SIZE = 10;

    public interface View {
        void displayMessage(String message);

        void startActivity(User user);

        void setLoadingFooter(boolean b);

        void addMoreItems(List<User> followers);
    }

    private View view;
    private FollowService followService;
    private UserService userService;

    private User lastFollower;

    private boolean isLoading = false;

    private boolean hasMorePages;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }


    public GetFollowersPresenter(View view) {
        this.view = view;
        userService = new UserService();
        followService = new FollowService();
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);
            followService.loadMoreFollowers(user, PAGE_SIZE, lastFollower, new GetFollowersObserver());
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
            lastFollower = (items.size() > 0) ? (User) items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems((List<User>) items);
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
