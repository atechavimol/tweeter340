package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterHandler implements RequestHandler<RegisterRequest, RegisterResponse> {
    @Override
    public RegisterResponse handleRequest(RegisterRequest registerRequest, Context context) {
        UserService userService = new UserService();
        return  userService.register(registerRequest);
    }
}