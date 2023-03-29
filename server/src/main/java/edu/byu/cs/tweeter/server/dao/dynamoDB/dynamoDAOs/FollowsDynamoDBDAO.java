package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.DataPage;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Follows;
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
        List<Follows> followsList = page.getValues();
        boolean hasMorePages = page.isHasMorePages();


        return new Pair<>(followsList, hasMorePages);
        //return new FollowingResponse(convertFollowsToFolloweeUsers(followsList), hasMorePages);
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
    public Pair<List<Follows>, Boolean> getFollowers(FollowerRequest request) {
        DataPage<Follows> page = getPageOfFollowers(request.getTargetUserAlias(), request.getLimit(), request.getLastFollowerAlias());
        List<Follows> followsList = page.getValues();
        boolean hasMorePages = page.isHasMorePages();


        return new Pair<>(followsList, hasMorePages);
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
       // Boolean isFollower = new Random().nextInt() > 0;

        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(followerAlias).sortValue(followeeAlias)
                .build();

        Follows follows = table.getItem(key);

        return follows != null;
    }

    @Override
    public Boolean follow(String followerAlias, String followeeAlias) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(followerAlias).sortValue(followeeAlias)
                .build();

        Follows follows = table.getItem(key);
        if(follows != null) {
            follows.setFollowerAlias(followerAlias);
            follows.setFolloweeAlias(followeeAlias);
            table.updateItem(follows);

        } else {
            Follows newFollows = new Follows();
            newFollows.setFollowerAlias(followerAlias);
            newFollows.setFolloweeAlias(followeeAlias);

            table.putItem(newFollows);
        }

        return true;
    }

    @Override
    public Boolean unfollow(String followerAlias, String followeeAlias) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(followerAlias).sortValue(followeeAlias)
                .build();

        table.deleteItem(key);
        return true;
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {
        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

}
