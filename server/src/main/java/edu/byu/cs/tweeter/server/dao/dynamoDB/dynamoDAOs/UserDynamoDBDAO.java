package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

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
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserDynamoDBDAO implements UserDAO {
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new LoginResponse(user, authToken);
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new RegisterResponse(user, authToken);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse(true);
    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        User user = getFakeData().findUserByAlias(request.getAlias());
        return new GetUserResponse(user);
    }

    User getDummyUser() {
        return getFakeData().getFirstUser();
    }
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
