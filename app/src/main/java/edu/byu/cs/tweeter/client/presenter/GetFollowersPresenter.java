package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter extends PagedPresenter<User>{

    public GetFollowersPresenter(View view) {
        super(view);
    }

    @Override
    protected void getItems(User user, int pageSize, User last) {
        followService.loadMoreFollowers(user, PAGE_SIZE, last, new GetFollowersObserver());
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



}
