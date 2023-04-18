package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DataPage;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Feed;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Story;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.UserTable;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FeedDynamoDBDAO implements FeedDAO {

    private static final String TableName = "Feed";

    private static final String FollowerAttr = "followerAlias";
    private static final String TimestampAttr = "timestamp";

    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();


    @Override
    public Pair<List<Feed>, Boolean> getFeed(FeedRequest request) {
        DataPage<Feed> page = getPageOfStatuses(request.getTargetUserAlias(), request.getLimit(), request.getLastStatus());
        return new Pair<>(page.getValues(), page.isHasMorePages());
    }

    private DataPage<Feed> getPageOfStatuses(String targetUserAlias, int limit, Status lastStatus) {
        DynamoDbTable<Feed> table = enhancedClient.table(TableName, TableSchema.fromBean(Feed.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(lastStatus != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(TimestampAttr, AttributeValue.builder().n(lastStatus.getTimestamp().toString()).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.scanIndexForward(false).build();

        DataPage<Feed> result = new DataPage<Feed>();

        PageIterable<Feed> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<Feed> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(status -> result.getValues().add(status));
                });


        return result;
    }

    @Override
    public void postStatus(String followerAlias, Status status) {
        DynamoDbTable<Feed> table = enhancedClient.table(TableName, TableSchema.fromBean(Feed.class));

        User follower = status.getUser();

        Feed newFeed = new Feed();
        newFeed.setFollowerAlias(followerAlias);
        newFeed.setFolloweeAlias(follower.getAlias());
        newFeed.setPost(status.getPost());
        newFeed.setTimestamp(status.getTimestamp());
        newFeed.setUrls(status.getUrls());
        newFeed.setMentions(status.getMentions());

        table.putItem(newFeed);
    }

    @Override
    public void addFeedBatch(Status status, List<String> followerAliases) {
        List<Feed> feeds = new ArrayList<>();

        for(String alias:followerAliases) {
            Feed feed = new Feed();
            feed.setFollowerAlias(alias);
            feed.setFolloweeAlias(status.getUser().getAlias());
            feed.setTimestamp(status.getTimestamp());
            feed.setMentions(status.getMentions());
            feed.setPost(status.getPost());
            feed.setUrls(status.getUrls());

            feeds.add(feed);
        }

        if(feeds.size() > 0) {
            writeChunkOfFeeds(feeds);
        }

    }

    private void writeChunkOfFeeds(List<Feed> feeds) {
        if(feeds.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<Feed> table = enhancedClient.table(TableName, TableSchema.fromBean(Feed.class));
        WriteBatch.Builder<Feed> writeBuilder = WriteBatch.builder(Feed.class).mappedTableResource(table);
        for (Feed item : feeds) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfFeeds(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


}