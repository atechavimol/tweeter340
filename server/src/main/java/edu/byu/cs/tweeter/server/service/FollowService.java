package edu.byu.cs.tweeter.server.service;

import java.util.Random;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.Factory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service{
    private AuthtokenDAO authtokenDAO;
    private FeedDAO feedDAO;
    private FollowsDAO followsDAO;
    private StoryDAO storyDAO;
    private UserDAO userDAO;

    public FollowService() {
        super();
        initDAOs();
    }

    public FollowService(Factory factory){
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


    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return followsDAO.getFollowees(request);
    }

    public FollowerResponse getFollowers(FollowerRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return followsDAO.getFollowers(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }

        return  followsDAO.isFollower(request);
    }

    public FollowResponse follow(FollowRequest request) {
        if(request.getFollowee() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        return followsDAO.follow(request);
    }


    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getFollowee() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        return followsDAO.unfollow(request);
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }
        return followsDAO.getFollowersCount(request);
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }
        return followsDAO.getFollowingCount(request);
    }
}
