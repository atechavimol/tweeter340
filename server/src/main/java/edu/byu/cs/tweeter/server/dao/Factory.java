package edu.byu.cs.tweeter.server.dao;

public interface Factory {
    public AuthtokenDAO getAuthtokenDAO();
    public FeedDAO getFeedDAO();
    public FollowsDAO getFollowsDAO();
    public StoryDAO getStoryDAO();
    public UserDAO getUserDAO();

}
