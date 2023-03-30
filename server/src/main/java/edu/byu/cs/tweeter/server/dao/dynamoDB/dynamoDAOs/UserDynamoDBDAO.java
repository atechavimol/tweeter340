package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.Follows;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.UserTable;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

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
            user = new UserTable();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAlias(username);
            user.setImageUrl(image);
            user.setPassword(hashedPassword);
            user.setFollowersCount(0);
            user.setFollowingCount(0);
            table.putItem(user);
        }

        return new User(user.getFirstName(), user.getLastName(), user.getAlias(), user.getImageUrl());
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        //TODO THIS feels wrong
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

}
