package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.net.TweeterRequestException;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;

public class GetCountTest {
    private AuthToken authToken = new AuthToken("newToken");
    private String targetUserAlias = "@amy";
    private ServerFacade serverFacade;
    private final String URL = "/followerscount";
    private final int COUNT = 20;

    @BeforeEach
    public void setup() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }
    }

    @Test
    public void getFollowersCountSuccess() {
        FollowersCountRequest followersCountRequest = new FollowersCountRequest(authToken, targetUserAlias);
        try {
            FollowersCountResponse followersCountResponse = serverFacade.getFollowersCount(followersCountRequest, URL);
            Assertions.assertTrue(followersCountResponse.isSuccess());
            Assertions.assertEquals(COUNT, followersCountResponse.getCount());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void getFollowersCountNullTargetUserAlias() {
        FollowersCountRequest followersCountRequest = new FollowersCountRequest(authToken, null);
        try {
            FollowersCountResponse followersCountResponse = serverFacade.getFollowersCount(followersCountRequest, URL);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("[Bad Request] Request needs to have a target user alias", e.getMessage());
            Assertions.assertEquals(TweeterRequestException.class, e.getClass());
        }
    }

}
