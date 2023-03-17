package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class FeedResponse extends PagedResponse {
    private List<Status> statuses;

    public FeedResponse(List<Status> statuses, boolean hasMorePages) {
        super(true, hasMorePages);
        this.statuses = statuses;
    }

    public FeedResponse(String message) {
        super(false, message, false);
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    @Override
    public List getItems() {
        return getStatuses();
    }
}
