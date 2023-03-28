package edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs;

import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.AuthtokenTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class AuthtokenDynamoDBDAO implements AuthtokenDAO {
    private static final String TableName = "Authtoken";
    public static final String IndexName = "followeeAlias-followerAlias-index";

    private static final String AuthtokenAttr = "authtoken";


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
}
