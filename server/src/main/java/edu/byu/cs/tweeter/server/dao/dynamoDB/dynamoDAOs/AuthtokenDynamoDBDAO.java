package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.AuthtokenTable;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.UserTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class AuthtokenDynamoDBDAO implements AuthtokenDAO {
    private static final String TableName = "Authtoken";

    private static final long timeOutPeriod = 60 * 100;


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
    public AuthToken insertToken(String userAlias) {
        UUID uuid = UUID.randomUUID();

        DynamoDbTable<AuthtokenTable> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenTable.class));

        Long timeout = System.currentTimeMillis() + (1000 * timeOutPeriod);

        AuthtokenTable newAuthtoken = new AuthtokenTable();
        newAuthtoken.setAuthtoken(uuid.toString());
        newAuthtoken.setTimeout(timeout);
        newAuthtoken.setUserAlias(userAlias);

        table.putItem(newAuthtoken);

        return new AuthToken(newAuthtoken.getAuthtoken(), newAuthtoken.getTimeout().toString());
    }

    @Override
    public void expireToken(AuthToken authToken) {
        DynamoDbTable<AuthtokenTable> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenTable.class));

        Key key = Key.builder()
                .partitionValue(authToken.getToken()).sortValue(Long.parseLong(authToken.getDatetime()))
                .build();

        AuthtokenTable authtoken = table.deleteItem(key);
    }

    @Override
    public String validateToken(AuthToken authToken) {
        DynamoDbTable<AuthtokenTable> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenTable.class));

        Key key = Key.builder()
                .partitionValue(authToken.getToken()).sortValue(Long.parseLong(authToken.getDatetime()))
                .build();

        AuthtokenTable foundAuthtoken = table.getItem(key);

        if(foundAuthtoken == null){
            throw new RuntimeException("[Bad Request] The session has expired. Logout and log back in to renew the session");
        } else if(!isValid(foundAuthtoken)) {
            expireToken(authToken);
            throw new RuntimeException("[Bad Request] The session has expired. Logout and log back in to renew the session");
        } else {
            return foundAuthtoken.getUserAlias();
        }

    }

    private Boolean isValid(AuthtokenTable authtoken){
        return authtoken.getTimeout() > System.currentTimeMillis();
    }


}
