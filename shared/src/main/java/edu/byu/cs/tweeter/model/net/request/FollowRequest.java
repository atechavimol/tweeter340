package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest {
    private User followee;

    private FollowRequest() {}

    public FollowRequest(User followee) {
        setFollowee(followee);
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }
}
