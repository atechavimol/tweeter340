package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DataPage;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Follows;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.UserTable;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
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

public class FollowsDynamoDBDAO implements FollowsDAO {
    private static final String TableName = "Follows";
    public static final String IndexName = "followeeAlias-followerAlias-index";

    private static final String FollowerAttr = "followerAlias";
    private static final String FolloweeAttr = "followeeAlias";

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
    public Pair<List<Follows>, Boolean> getFollowees(FollowingRequest request) {

        DataPage<Follows> page = getPageOfFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());

        return new Pair<>(page.getValues(), page.isHasMorePages());
    }

    public DataPage<Follows> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias ) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<Follows> result = new DataPage<Follows>();

        PageIterable<Follows> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });


        return result;
    }


    @Override
    public Pair<List<Follows>, Boolean> getFollowers(String targetUserAlias, int limit, String lastFollowerAlias) {
        DataPage<Follows> page = getPageOfFollowers(targetUserAlias, limit, lastFollowerAlias);

        return new Pair<>(page.getValues(), page.isHasMorePages());
    }

    private DataPage<Follows> getPageOfFollowers(String targetUserAlias, int limit, String lastFollowerAlias) {
        DynamoDbIndex<Follows> index = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class)).index(IndexName);
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(isNonEmptyString(lastFollowerAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(lastFollowerAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<Follows> result = new DataPage<Follows>();

        SdkIterable<Page<Follows>> sdkIterable = index.query(request);
        PageIterable<Follows> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follows -> result.getValues().add(follows));
                });

        return result;
    }

    @Override
    public Boolean isFollower(String followerAlias, String followeeAlias) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(followerAlias).sortValue(followeeAlias)
                .build();

        Follows follows = table.getItem(key);

        return follows != null;
    }

    @Override
    public void follow(String followerAlias, String followeeAlias) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(followerAlias).sortValue(followeeAlias)
                .build();

        Follows follows = table.getItem(key);

        if(follows != null) {
            throw new RuntimeException("[Server Error] Follower/followee relationship already exists");
        }

        Follows newFollows = new Follows();
        newFollows.setFollowerAlias(followerAlias);
        newFollows.setFolloweeAlias(followeeAlias);
        table.putItem(newFollows);

    }

    @Override
    public void unfollow(String followerAlias, String followeeAlias) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(followerAlias).sortValue(followeeAlias)
                .build();

        Follows follows = table.deleteItem(key);

        if(follows == null) {
            throw new RuntimeException("[Server Error] Follower/Followee relationship does not exist");
        }

    }

    @Override
    public void addFollowersBatch(List<String> followersAliases, String followTarget) {
        List<Follows> batchToWrite = new ArrayList<>();
        for (String alias : followersAliases) {
            Follows follows = new Follows();
            follows.setFollowerAlias(alias);
            follows.setFolloweeAlias(followTarget);

            batchToWrite.add(follows);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfFollows(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfFollows(batchToWrite);
        }
    }

    private void writeChunkOfFollows(List<Follows> followsList) {
        if(followsList.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        WriteBatch.Builder<Follows> writeBuilder = WriteBatch.builder(Follows.class).mappedTableResource(table);
        for (Follows follows : followsList) {
            writeBuilder.addPutItem(builder -> builder.item(follows));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfFollows(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }



}
