package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T>  extends Presenter {

    protected static final int PAGE_SIZE = 10;
    protected T last;
    protected boolean isLoading = false;
    protected boolean hasMorePages;

    protected StatusService statusService;
    protected UserService userService;
    protected FollowService followService;

    public interface PagedView extends View {

        void startActivity(User user);

        void setLoadingFooter(boolean b);

        <T> void addMoreItems(List<T> items);
    }

    public PagedPresenter(PagedView view) {
        super(view);
        statusService = new StatusService();
        userService = new UserService();
        followService = new FollowService();

    }

    public void getUserProfile(String alias) {
        userService.getUserProfile(alias, new GetUserObserver() );
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            ((PagedView)view).setLoadingFooter(true);
            getItems(user, PAGE_SIZE, last);
        }
    }


    protected abstract void getItems(User user, int pageSize, T last);

    public class GetUserObserver implements UserTaskObserver {

        @Override
        public void startActivity(User user) {
            ((PagedView)view).startActivity(user);
        }

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }
    }

    public class GetItemsObserver implements PagedNotificationObserver<T> {
        @Override
        public void handleSuccess(List<T> items, Boolean hasMorePages) {
            isLoading = false;
            ((PagedView)view).setLoadingFooter(false);
            last = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            ((PagedView)view).addMoreItems((List<T>) items);
        }

        @Override
        public void displayMessage(String message) {
            isLoading = false;
            ((PagedView)view).setLoadingFooter(false);
            view.displayMessage(message);
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
