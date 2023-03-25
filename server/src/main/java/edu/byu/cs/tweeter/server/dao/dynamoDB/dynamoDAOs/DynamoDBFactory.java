package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.Factory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class DynamoDBFactory implements Factory {
    @Override
    public AuthtokenDAO getAuthtokenDAO() {
        return new AuthtokenDynamoDBDAO();
    }

    @Override
    public FeedDAO getFeedDAO() {
        return new FeedDynamoDBDAO();
    }

    @Override
    public FollowsDAO getFollowsDAO() {
        return new FollowsDynamoDBDAO();
    }

    @Override
    public StoryDAO getStoryDAO() {
        return new StoryDynamoDBDAO();
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDynamoDBDAO();
    }
}
