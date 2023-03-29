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

        //TODO make real datetime

        AuthtokenTable newAuthtoken = new AuthtokenTable();
        newAuthtoken.setAuthtoken(uuid.toString());
        newAuthtoken.setTimeout("TIMEOUT");
        newAuthtoken.setUserAlias(userAlias);

        table.putItem(newAuthtoken);

        return new AuthToken(newAuthtoken.getAuthtoken(), newAuthtoken.getTimeout());
    }

    @Override
    public void expireToken(AuthToken authToken) {
        DynamoDbTable<AuthtokenTable> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenTable.class));

        Key key = Key.builder()
                .partitionValue(authToken.getToken()).sortValue(authToken.getDatetime())
                .build();

        table.deleteItem(key);

    }

    @Override
    public String validateToken(AuthToken authToken) {
        DynamoDbTable<AuthtokenTable> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthtokenTable.class));

        Key key = Key.builder()
                .partitionValue(authToken.getToken()).sortValue(authToken.getDatetime())
                .build();

        AuthtokenTable foundAuthtoken = table.getItem(key);

        if(foundAuthtoken == null){
            return null;
        } else if(!isValid(foundAuthtoken)) {
            //expireToken(authToken);
            return null;
        } else {
            return foundAuthtoken.getUserAlias();
        }

    }

    private Boolean isValid(AuthtokenTable authtoken){
        // TODO validate authtoken timout
        return true;
    }


}
