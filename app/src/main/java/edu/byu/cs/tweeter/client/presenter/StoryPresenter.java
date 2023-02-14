package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status>{
    
    public StoryPresenter(View view) {
        super(view);
    }

    public void getUserProfile(String alias) {
        userService.getUserProfile(alias,new GetUserObserver());
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            statusService.getStory(user, PAGE_SIZE, last, new GetStoryObserver());
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

    public class GetStoryObserver implements PagedNotificationObserver {
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
            last = (items.size() > 0) ? (Status) items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems((List<Status>) items);
        }
    }

}
