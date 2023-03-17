package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response{
    private Boolean isFollower;

    public IsFollowerResponse(boolean isFollower) {
        super(true);
        this.isFollower = isFollower;
    }

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public Boolean getIsFollower() {
        return isFollower;
    }
}
