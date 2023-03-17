package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FeedRequest {
    private AuthToken authToken;
    private String targetUserAlias;
    private int limit;
    private Integer lastStatusHash;

    private FeedRequest(){}

    public FeedRequest(AuthToken authToken, String targetUserAlias, int limit, Integer lastStatusHash) {
        this.authToken = authToken;
        this.targetUserAlias = targetUserAlias;
        this.limit = limit;
        this.lastStatusHash = lastStatusHash;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getTargetUserAlias() {
        return targetUserAlias;
    }

    public void setTargetUserAlias(String targetUserAlias) {
        this.targetUserAlias = targetUserAlias;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLastStatusHash() {
        return lastStatusHash;
    }

    public void setLastStatusHash(int lastStatusHash) {
        this.lastStatusHash = lastStatusHash;
    }
}
