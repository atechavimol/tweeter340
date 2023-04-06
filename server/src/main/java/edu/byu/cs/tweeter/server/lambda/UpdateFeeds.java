package edu.byu.cs.tweeter.server.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.GsonBuilder;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs.DynamoDBFactory;
import edu.byu.cs.tweeter.server.lambda.util.Batch;
import edu.byu.cs.tweeter.server.service.StatusService;

public class UpdateFeeds implements RequestHandler<SQSEvent, Void> {
    DynamoDBFactory factory = new DynamoDBFactory();
    StatusService statusService = new StatusService(factory);

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        try {
            for (SQSEvent.SQSMessage msg : event.getRecords()) {
                Batch batch = new GsonBuilder().create().fromJson(msg.getBody(), Batch.class);
                Status status = batch.getStatus();
                List<String> followerAliases = batch.getFollowerAliases();

                updateAllFeeds(status, followerAliases);

            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    private void updateAllFeeds(Status status, List<String> aliases) {
        for(String alias: aliases) {
            updateFeed(status, alias);
        }
    }

    private void updateFeed(Status status, String alias) {
        statusService.updateUserFeed(status, alias);
        System.out.println("Updating feed for: " + alias);
    }
}
