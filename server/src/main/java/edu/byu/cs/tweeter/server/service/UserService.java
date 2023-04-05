package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        User user = userDAO.login(request.getUsername(), hashPassword(request.getPassword()));

        if(user == null) {
            return new LoginResponse("Invalid credentials");
        }

        AuthToken authToken = authtokenDAO.insertToken(request.getUsername());
        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        try {
            User user = userDAO.register(request.getFirstName(), request.getLastName(), request.getUsername(),
                    request.getImage(), hashPassword(request.getPassword()));

            if(user == null){
                return new RegisterResponse("Alias already taken");
            }

            AuthToken authToken = authtokenDAO.insertToken(request.getUsername());
            return new RegisterResponse(user, authToken);
        } catch (Exception e) {
            return  new RegisterResponse(e.getMessage());
        }


    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing authtoken");
        }

        authtokenDAO.expireToken(request.getAuthToken());
        return userDAO.logout(request);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request must have alias");
        }

        try {
            authtokenDAO.validateToken(request.getAuthToken());
            User user = userDAO.getUser(request.getAlias());
            return new GetUserResponse(user);
        } catch (Exception e) {
            return new GetUserResponse(e.getMessage());
        }
    }

    private static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }
}
