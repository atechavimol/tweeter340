package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public interface UserDAO {
    User login(LoginRequest request);

    User register(RegisterRequest request);

    LogoutResponse logout(LogoutRequest request);

    User getUser(String userAlias);

    void updateFollowingCount(int i, String curUserAlias);

    void updateFollowersCount(int i, String alias);

    int getFollowersCount(String userAlias);

    int getFollowingCount(String userAlias);
}
