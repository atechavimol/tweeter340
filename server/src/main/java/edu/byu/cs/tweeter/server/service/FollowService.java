package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
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
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Follows;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service{
    private AuthtokenDAO authtokenDAO;
    //private FeedDAO feedDAO;
    private FollowsDAO followsDAO;
    //private StoryDAO storyDAO;
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
        //this.feedDAO = factory.getFeedDAO();
        this.followsDAO = factory.getFollowsDAO();
        //this.storyDAO = factory.getStoryDAO();
        this.userDAO = factory.getUserDAO();
    }


    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        try {
            authtokenDAO.validateToken(request.getAuthToken());
            Pair<List<Follows>, Boolean> result = followsDAO.getFollowees(request);

            List<User> users = new ArrayList<>();
            for(Follows follow: result.getFirst()) {
                User user = userDAO.getUser(follow.getFolloweeAlias());
                users.add(user);
            }

            return new FollowingResponse(users, result.getSecond());
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return new FollowingResponse(e.getMessage());
        }


    }

    public FollowerResponse getFollowers(FollowerRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }

        try {
            authtokenDAO.validateToken(request.getAuthToken());
            Pair<List<Follows>, Boolean> result = followsDAO.getFollowers(request.getTargetUserAlias(), request.getLimit(),
                    request.getLastFollowerAlias());

            List<User> users = new ArrayList<>();
            for(Follows follow: result.getFirst()) {
                User user = userDAO.getUser(follow.getFollowerAlias());
                users.add(user);
            }
            return new FollowerResponse(users, result.getSecond());
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return new FollowerResponse(e.getMessage());
        }

    }

    public List<String> getAllFollowerAliases(String userAlias) {
        Pair<List<Follows>, Boolean> result = followsDAO.getFollowers(userAlias, Integer.MAX_VALUE,
                null);

        List<String> users = new ArrayList<>();

        for(Follows follow: result.getFirst()) {
            users.add(follow.getFollowerAlias());
        }
        return users;

    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }

        try {
            authtokenDAO.validateToken(request.getAuthToken());
            Boolean isFollower = followsDAO.isFollower(request.getFollowerAlias(), request.getFolloweeAlias());
            return new IsFollowerResponse(isFollower);
        } catch (Exception e) {
            return new IsFollowerResponse(e.getMessage());
        }


    }

    public FollowResponse follow(FollowRequest request) {
        if(request.getFollowee() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }

        try{
            String curUserAlias = authtokenDAO.validateToken(request.getAuthToken());

            followsDAO.follow(curUserAlias, request.getFollowee().getAlias());
            userDAO.updateFollowingCount(1, curUserAlias);
            userDAO.updateFollowersCount(1, request.getFollowee().getAlias());
            return new FollowResponse();

        } catch (Exception e){
            return new FollowResponse(e.getMessage());
        }

    }


    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getFollowee() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }

        try {
            String curUserAlias = authtokenDAO.validateToken((request.getAuthToken()));

            followsDAO.unfollow(curUserAlias, request.getFollowee().getAlias());
            userDAO.updateFollowingCount(-1, curUserAlias);
            userDAO.updateFollowersCount(-1, request.getFollowee().getAlias());

            return new UnfollowResponse();
        } catch (Exception e) {
            return new UnfollowResponse(e.getMessage());
        }



    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }

        try {
            authtokenDAO.validateToken(request.getAuthToken());
            int followersCount = userDAO.getFollowersCount(request.getTargetUserAlias());
            return new FollowersCountResponse(followersCount);
        } catch (Exception e) {
            return new FollowersCountResponse(e.getMessage());
        }

    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }

        try {
           authtokenDAO.validateToken(request.getAuthToken());
            int followingCount = userDAO.getFollowingCount(request.getTargetUserAlias());
            return new FollowingCountResponse(followingCount);
        } catch (Exception e) {
            return new FollowingCountResponse(e.getMessage());
        }

    }



}
