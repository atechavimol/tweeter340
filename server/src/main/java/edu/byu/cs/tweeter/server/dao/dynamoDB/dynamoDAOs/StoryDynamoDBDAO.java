package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class StoryDynamoDBDAO implements StoryDAO {
    @Override
    public StoryResponse getStory(StoryRequest request) {
        assert request != null;
        assert request.getLimit() > 0;
        assert request.getTargetUserAlias() != null;

        request.getLastStatusHash();

        List<Status> allStatuses = getDummyStatuses();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusesIndex = getStatusesStartingIndex(request.getLastStatusHash(), allStatuses);

                for(int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < request.getLimit(); statusesIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusesIndex));
                }

                hasMorePages = statusesIndex< allStatuses.size();
            }
        }
        return new StoryResponse(responseStatuses, hasMorePages);
    }

    @Override
    public PostStatusResponse postStatus(PostStatusRequest request) {
        return new PostStatusResponse();
    }

    private int getStatusesStartingIndex(Integer lastStatusHash, List<Status> allStatuses) {

        int statusesIndex = 0;

        if(lastStatusHash != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatusHash == allStatuses.get(i).hashCode()) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusesIndex = i + 1;
                    break;
                }
            }
        }

        return statusesIndex;
    }

    List<Status> getDummyStatuses() {
        return getFakeData().getFakeStatuses();
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
