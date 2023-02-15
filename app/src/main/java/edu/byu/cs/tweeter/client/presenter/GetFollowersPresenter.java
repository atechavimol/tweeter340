package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter extends PagedPresenter<User>{

    public GetFollowersPresenter(View view) {
        super(view);
    }

    @Override
    protected void getItems(User user, int pageSize, User last) {
        followService.loadMoreFollowers(user, PAGE_SIZE, last, new GetItemsObserver());
    }

}
