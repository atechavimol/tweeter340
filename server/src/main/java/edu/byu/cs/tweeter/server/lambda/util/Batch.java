package edu.byu.cs.tweeter.server.lambda.util;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class Batch {
    private Status status;
    private List<String> followerAliases;

    private Batch(){}

    public Batch(Status status, List<String> followerAliases) {
        this.status = status;
        this.followerAliases = followerAliases;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getFollowerAliases() {
        return followerAliases;
    }

    public void setFollowerAliases(List<String> followerAliases) {
        this.followerAliases = followerAliases;
    }
}
