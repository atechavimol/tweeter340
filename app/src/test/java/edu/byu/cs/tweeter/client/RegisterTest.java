package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;

import edu.byu.cs.tweeter.client.model.net.TweeterRequestException;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class RegisterTest {
    private String firstName = "Amy";
    private String lastName = "Tech";
    private String username = "@amy";
    private String password = "password";
    private String imageBytesBase64 = "image";
    private ServerFacade serverFacade;
    private final String URL = "/register";


    @BeforeEach
    public void setup() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

    }

    @Test
    public void registerSuccess() {
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, username, password, imageBytesBase64);
        try {
            RegisterResponse registerResponse = serverFacade.register(registerRequest, URL);
            Assertions.assertTrue(registerResponse.isSuccess());
            Assertions.assertNotNull(registerResponse.getAuthToken());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void registerNullUsername() {
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, null, password, imageBytesBase64);
        try {
            RegisterResponse registerResponse = serverFacade.register(registerRequest, URL);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("[Bad Request] Missing a username", e.getMessage());
            Assertions.assertEquals(TweeterRequestException.class, e.getClass());
        }
    }

    @Test
    public void registerNullPassword() {
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, username, null, imageBytesBase64);
        try {
            RegisterResponse registerResponse = serverFacade.register(registerRequest, URL);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("[Bad Request] Missing a password", e.getMessage());
            Assertions.assertEquals(TweeterRequestException.class, e.getClass());
        }
    }
}
