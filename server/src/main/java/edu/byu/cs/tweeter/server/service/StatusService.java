package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.Factory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Feed;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Follows;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Story;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService extends Service{
    private AuthtokenDAO authtokenDAO;
    private FeedDAO feedDAO;
    private FollowsDAO followsDAO;
    private StoryDAO storyDAO;
    private UserDAO userDAO;

    public StatusService() {
        super();
        initDAOs();
    }

    public StatusService(Factory factory) {
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

    public StoryResponse getStory(StoryRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        Pair<List<Story>, Boolean> result = storyDAO.getStory(request);
        List<Status> statuses = new ArrayList<>();
        for(Story story: result.getFirst()){

            Status status = new Status(story.getPost(), userDAO.getUser(story.getAlias()),
                    story.getTimestamp(), story.getUrls(), story.getMentions());
            statuses.add(status);
        }
        return new StoryResponse(statuses, result.getSecond());
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        Pair<List<Feed>, Boolean> result = feedDAO.getFeed(request);

        List<Status> statuses = new ArrayList<>();
        for(Feed feed: result.getFirst()) {
            Status status = new Status(feed.getPost(), userDAO.getUser(feed.getFolloweeAlias()), feed.getTimestamp(),
                                        feed.getUrls(), feed.getMentions()) ;
            statuses.add(status);
        }
        return new FeedResponse(statuses, result.getSecond());
    }



    public PostStatusResponse postStatus(PostStatusRequest request) {
        if(request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }

        String curUserAlias = authtokenDAO.validateToken(request.getAuthToken());

        //post to user's own story
        storyDAO.postStatus(request.getStatus());


        //get list of my followers
        Pair<List<Follows>, Boolean> page = followsDAO.getFollowers(curUserAlias, Integer.MAX_VALUE, null);

        //update my followers feed with new post
        for(Follows follow: page.getFirst()) {
            String followerAlias = follow.getFollowerAlias();
            feedDAO.postStatus(followerAlias, request.getStatus());
        }

        return new PostStatusResponse();
    }
}
