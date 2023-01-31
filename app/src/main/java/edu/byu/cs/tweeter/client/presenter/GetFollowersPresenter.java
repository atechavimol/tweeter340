package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
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

    public class GetFollowersObserver implements FollowService.Observer {

        @Override
        public void displayError(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(message);

        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(ex.getMessage());

        }

        @Override
        public void addFollows(List<User> followers, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems(followers);
        }
    }
    public class GetUserObserver implements UserService.Observer {

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
