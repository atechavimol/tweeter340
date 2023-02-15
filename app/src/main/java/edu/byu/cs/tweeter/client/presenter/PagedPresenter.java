package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T>  {
    protected static final int PAGE_SIZE = 10;
    protected T last;
    protected boolean isLoading = false;
    protected boolean hasMorePages;
    protected StatusService statusService;
    protected UserService userService;
    protected FollowService followService;
    protected View view;

    public PagedPresenter(View view) {
        statusService = new StatusService();
        userService = new UserService();
        followService = new FollowService();
        this.view = view;
    }


    public interface View {

        void startActivity(User user);

        void displayMessage(String s);

        void setLoadingFooter(boolean b);

        <T> void addMoreItems(List<T> items);
    }

    public void getUserProfile(String alias) {
        userService.getUserProfile(alias, new GetUserObserver() );
    }
    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            getItems(user, PAGE_SIZE, last);
        }
    }


    protected abstract void getItems(User user, int pageSize, T last);

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



}
