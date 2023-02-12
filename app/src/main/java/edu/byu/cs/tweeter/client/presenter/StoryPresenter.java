package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter {
    private static final int PAGE_SIZE = 10;
    public interface View {
        void startActivity(User user);

        void displayMessage(String s);

        void setLoadingFooter(boolean value);

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

    
    public StoryPresenter(View view) {
        this.view = view;
        userService = new UserService();
        statusService = new StatusService();
    }

    public void getUserProfile(String alias) {
        userService.getUserProfile(alias,new GetUserObserver());
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            statusService.getStory(user, PAGE_SIZE, lastStatus, new GetStoryObserver());
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

    public class GetStoryObserver implements StatusService.Observer {
        @Override
        public void displayMessage(String s) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(s);
        }

        @Override
        public <T> void handleSuccess(List<T> items, Boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastStatus = (items.size() > 0) ? (Status) items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems((List<Status>) items);
        }
    }

}
