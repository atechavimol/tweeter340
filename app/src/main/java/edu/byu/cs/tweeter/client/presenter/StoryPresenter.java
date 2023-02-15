package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status>{
    
    public StoryPresenter(PagedView view) {
        super(view);
    }
    @Override
    protected void getItems(User user, int pageSize, Status last) {
        statusService.getStory(user, PAGE_SIZE, last, new GetItemsObserver());
    }


}
