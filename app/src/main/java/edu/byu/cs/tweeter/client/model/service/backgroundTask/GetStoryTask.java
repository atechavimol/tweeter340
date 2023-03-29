package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected PagedResponse processRequest() throws IOException, TweeterRemoteException {
        String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
        Status lastStatus = lastItem == null ? null : lastItem;

        StoryRequest request = new StoryRequest(authToken, targetUserAlias, limit, lastStatus);
        StoryResponse response = getServerFacade().getStory(request, "/story");

        return response;
    }
}
