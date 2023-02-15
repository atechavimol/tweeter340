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
    @Override
    protected void getItems(User user, int pageSize, Status last) {
        statusService.getStory(user, PAGE_SIZE, last, new GetItemsObserver());
    }


}
