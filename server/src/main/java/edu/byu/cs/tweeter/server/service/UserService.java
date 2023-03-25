package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.Factory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService extends Service{
    private AuthtokenDAO authtokenDAO;
    private FeedDAO feedDAO;
    private FollowsDAO followsDAO;
    private StoryDAO storyDAO;
    private UserDAO userDAO;

    public UserService(){
        super();
        initDAOs();
    }

    public UserService(Factory factory) {
        super(factory);
        initDAOs();
    }

    private void initDAOs() {
        this.authtokenDAO = factory.getAuthtokenDAO();
        this.feedDAO = factory.getFeedDAO();
        this.followsDAO = factory.getFollowsDAO();
        this.storyDAO = factory.getStoryDAO();
        this.userDAO = factory.getUserDAO();
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        return userDAO.login(request);

    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        return userDAO.register(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing authtoken");
        }
        return userDAO.logout(request);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request must have alias");
        }
        return userDAO.getUser(request);
    }
}
