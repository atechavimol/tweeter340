package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class GetUserResponse extends Response {
    private User user;

    public GetUserResponse(User user) {
        super(true);
        setUser(user);
    }

    public GetUserResponse(String message) {
        super(false, message);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
