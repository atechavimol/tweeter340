package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingPresenter {
    private static final int PAGE_SIZE = 10;

    public interface View {
        void setLoadingFooter(boolean status);

        void displayMessage(String message);

        void addMoreItems(List<User> followees);

        void startActivity(User user);
    }

    private View view;

    private FollowService followService;
    private UserService userService;

    private User lastFollowee;
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


    private boolean isLoading = false;

    public GetFollowingPresenter(View view) {
        this.view = view;
        followService = new FollowService();
        userService = new UserService();
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            followService.loadMoreItems(user, PAGE_SIZE, lastFollowee, new GetFollowingObserver());
        }
    }

    public void getUserProfile(String alias) {
        userService.getUserProfile(alias, new GetUserObserver());
    }

    public class GetFollowingObserver implements FollowService.Observer {
        @Override
        public <T> void handleSuccess(List<T> items, Boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollowee = (items.size() > 0) ? (User) items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems((List<User>) items);
        }

        @Override
        public void displayMessage(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(message);
        }
//        @Override
//        public void displayError(String message) {
//            isLoading = false;
//            view.setLoadingFooter(false);
//
//            view.displayMessage(message);
//        }
//
//        @Override
//        public void displayException(Exception ex) {
//            isLoading = false;
//            view.setLoadingFooter(false);
//
//            view.displayMessage(ex.getMessage());
//        }
//
//        @Override
//        public void addFollows(List<User> followees, boolean hasMorePages) {
//            isLoading = false;
//            view.setLoadingFooter(false);
//
//            lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
//            setHasMorePages(hasMorePages);
//            view.addMoreItems(followees);
//        }
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
