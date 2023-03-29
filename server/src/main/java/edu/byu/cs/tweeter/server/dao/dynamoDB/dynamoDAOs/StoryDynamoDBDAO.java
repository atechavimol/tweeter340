package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DataPage;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Follows;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Story;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.UserTable;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StoryDynamoDBDAO implements StoryDAO {
    private static final String TableName = "Story";

    private static final String AliasAttr = "alias";
    private static final String TimestampAttr = "timestamp";
    // DynamoDB client
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    @Override
    public Pair<List<Story>, Boolean> getStory(StoryRequest request) {

        DataPage<Story> page = getPageOfStatuses(request.getTargetUserAlias(), request.getLimit(), request.getLastStatus());
        return new Pair<>(page.getValues(), page.isHasMorePages());
    }

    private DataPage<Story> getPageOfStatuses(String targetUserAlias, int limit, Status lastStatus) {
        DynamoDbTable<Story> table = enhancedClient.table(TableName, TableSchema.fromBean(Story.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(lastStatus != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(AliasAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(TimestampAttr, AttributeValue.builder().n(lastStatus.getTimestamp().toString()).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<Story> result = new DataPage<Story>();

        PageIterable<Story> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<Story> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(status -> result.getValues().add(status));
                });


        return result;

    }

    @Override
    public PostStatusResponse postStatus(Status status) {
        DynamoDbTable<Story> table = enhancedClient.table(TableName, TableSchema.fromBean(Story.class));
//        Key key = Key.builder()
//                .partitionValue(user.getAlias())
//                .build();
        User user = status.getUser();

        Story newStory = new Story();
        newStory.setAlias(user.getAlias());
        newStory.setPost(status.getPost());
        newStory.setTimestamp(status.getTimestamp());
        newStory.setUrls(status.getUrls());
        newStory.setMentions(status.getMentions());
      // newStory.setStatusHash(status.hashCode());
        table.putItem(newStory);
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
