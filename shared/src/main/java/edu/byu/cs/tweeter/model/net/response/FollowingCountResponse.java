package edu.byu.cs.tweeter.model.net.response;

public class FollowingCountResponse extends CountResponse{
    public FollowingCountResponse(int count) {
        super(count);
    }

    public FollowingCountResponse(String message) {
        super(message);
    }
}
