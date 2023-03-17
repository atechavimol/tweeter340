package edu.byu.cs.tweeter.model.net.response;


public class FollowersCountResponse extends CountResponse{
    public FollowersCountResponse(int count) {
        super(count);
    }
    public FollowersCountResponse(String message) {
        super(message);
    }
}
