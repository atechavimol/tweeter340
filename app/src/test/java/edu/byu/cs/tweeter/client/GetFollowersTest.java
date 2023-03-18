package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.net.TweeterRequestException;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class GetFollowersTest {
    private AuthToken authToken = new AuthToken("newToken");
    private String targetUserAlias = "@amy";
    private int limit = 10;
    private String lastFollowerAlias = null;
    private ServerFacade serverFacade;
    private final String URL = "/follower";

    @BeforeEach
    public void setup() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

    }

    @Test
    public void getFollowersSuccess() {
        FollowerRequest followerRequest = new FollowerRequest(authToken, targetUserAlias, limit, lastFollowerAlias);
        try {
            FollowerResponse followerResponse = serverFacade.getFollowers(followerRequest, URL);
            Assertions.assertTrue(followerResponse.isSuccess());
            Assertions.assertEquals(limit, followerResponse.getFollowers().size());
            Assertions.assertNotNull(followerResponse.isSuccess());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void getFollowersNullTargetUserAlias() {
        FollowerRequest followerRequest = new FollowerRequest(authToken, null, limit, lastFollowerAlias);
        try {
            FollowerResponse followerResponse = serverFacade.getFollowers(followerRequest, URL);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("[Bad Request] Request needs to have a follower alias", e.getMessage());
            Assertions.assertEquals(TweeterRequestException.class, e.getClass());
        }
    }

    @Test
    public void getFollowersNegativeLimit() {
        FollowerRequest followerRequest = new FollowerRequest(authToken, targetUserAlias, -10, lastFollowerAlias);
        try {
            FollowerResponse followerResponse = serverFacade.getFollowers(followerRequest, URL);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("[Bad Request] Request needs to have a positive limit", e.getMessage());
            Assertions.assertEquals(TweeterRequestException.class, e.getClass());
        }
    }
}
