package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingPresenter extends PagedPresenter<User> {

    public GetFollowingPresenter(View view) {
        super(view);
    }

    @Override
    protected void getItems(User user, int pageSize, User last) {
        followService.loadMoreFollowing(user, PAGE_SIZE, last, new GetItemsObserver());
    }


}
