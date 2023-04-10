package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Feed;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Story;
import edu.byu.cs.tweeter.util.Pair;

public interface FeedDAO {
    Pair<List<Feed>, Boolean> getFeed(FeedRequest request);

    void postStatus(String followerAlias, Status status);

    void addFeedBatch(Status status, List<String> followerAliases);
}
