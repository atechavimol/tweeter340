package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status>{

    public FeedPresenter(PagedView view) {
        super(view);
    }

    @Override
    protected void getItems(User user, int pageSize, Status last) {
        statusService.getFeed(user, PAGE_SIZE, last, new GetItemsObserver());
    }



}
