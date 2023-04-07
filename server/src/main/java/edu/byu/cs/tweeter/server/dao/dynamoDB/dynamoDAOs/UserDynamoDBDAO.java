package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.UserTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class UserDynamoDBDAO implements UserDAO {

    private static final String TableName = "User";
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
    public User login(String username, String hashedPassword) {
        DynamoDbTable<UserTable> table = enhancedClient.table(TableName, TableSchema.fromBean(UserTable.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        UserTable user = table.getItem(key);
        if(user == null) {
            return null;
        }

        if(!user.getPassword().equals(hashedPassword)) {
            return null;
        }

        return new User(user.getFirstName(), user.getLastName(), user.getAlias(), user.getImageUrl());
    }


    @Override
    public User register(String firstName, String lastName, String username, String image, String hashedPassword) {
        DynamoDbTable<UserTable> table = enhancedClient.table(TableName, TableSchema.fromBean(UserTable.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        UserTable user = table.getItem(key);
        if(user != null) {
            return null;

        } else {
            String imageUrl = uploadtoS3(image, username);

            user = new UserTable();

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAlias(username);
            user.setImageUrl(imageUrl);
            user.setPassword(hashedPassword);
            user.setFollowersCount(0);
            user.setFollowingCount(0);

            table.putItem(user);
        }

        return new User(user.getFirstName(), user.getLastName(), user.getAlias(), user.getImageUrl());
    }

    private String uploadtoS3(String imageString, String alias) {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-west-2")
                .build();

        byte[] byteArray = Base64.getDecoder().decode(imageString);

        ObjectMetadata data = new ObjectMetadata();

        data.setContentLength(byteArray.length);

        data.setContentType("image/jpeg");

        PutObjectRequest request = new PutObjectRequest("tweeter-user-photo", alias, new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);

        s3.putObject(request);

        String link = "https://tweeter-user-photo.s3.us-west-2.amazonaws.com/" + alias;

        return link;
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse(true);
    }

    @Override
    public User getUser(String userAlias) {
        DynamoDbTable<UserTable> table = enhancedClient.table(TableName, TableSchema.fromBean(UserTable.class));
        Key key = Key.builder()
                .partitionValue(userAlias)
                .build();

        UserTable user = table.getItem(key);

        if(user == null) {
            throw new NullPointerException("[Bad Request] User does not exist");
        }

        User foundUser = new User(user.getFirstName(), user.getLastName(), user.getAlias(), user.getImageUrl());
        return foundUser;
    }

    @Override
    public void updateFollowingCount(int i, String curUserAlias) {
        DynamoDbTable<UserTable> table = enhancedClient.table(TableName, TableSchema.fromBean(UserTable.class));
        Key key = Key.builder()
                .partitionValue(curUserAlias)
                .build();

        UserTable user = table.getItem(key);

        if(user == null) {
            throw new NullPointerException("[Bad Request] User does not exist");
        }

        user.setFollowingCount(user.getFollowingCount() + i);
        table.updateItem(user);

    }

    @Override
    public void updateFollowersCount(int i, String alias) {
        DynamoDbTable<UserTable> table = enhancedClient.table(TableName, TableSchema.fromBean(UserTable.class));
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        UserTable user = table.getItem(key);

        if(user == null){
            throw new NullPointerException("[Bad Request] User does not exist");
        }

        user.setFollowersCount(user.getFollowersCount() + i);
        table.updateItem(user);

    }

    @Override
    public int getFollowersCount(String userAlias) {
        DynamoDbTable<UserTable> table = enhancedClient.table(TableName, TableSchema.fromBean(UserTable.class));
        Key key = Key.builder()
                .partitionValue(userAlias)
                .build();

        UserTable user = table.getItem(key);
        if(user == null) {
            throw new NullPointerException("[Bad Request] User does not exist");
        }
        return user.getFollowersCount();
    }

    @Override
    public int getFollowingCount(String userAlias) {
        DynamoDbTable<UserTable> table = enhancedClient.table(TableName, TableSchema.fromBean(UserTable.class));
        Key key = Key.builder()
                .partitionValue(userAlias)
                .build();

        UserTable user = table.getItem(key);
        if(user == null) {
            throw new NullPointerException("[Bad Request] User does not exist");
        }
        return user.getFollowingCount();
    }

    @Override
    public void addUserBatch(List<UserTable> users) {
        List<UserTable> batchToWrite = new ArrayList<>();
        for (UserTable u : users) {
            batchToWrite.add(u);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUsers(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUsers(batchToWrite);
        }
    }

    private void writeChunkOfUsers(List<UserTable> users) {
        if(users.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<UserTable> table = enhancedClient.table(TableName, TableSchema.fromBean(UserTable.class));
        WriteBatch.Builder<UserTable> writeBuilder = WriteBatch.builder(UserTable.class).mappedTableResource(table);
        for (UserTable item : users) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUsers(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
