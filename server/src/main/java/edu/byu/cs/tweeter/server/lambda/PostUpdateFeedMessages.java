package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs.DynamoDBFactory;
import edu.byu.cs.tweeter.server.lambda.util.Batch;
import edu.byu.cs.tweeter.server.service.FollowService;

public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        DynamoDBFactory factory = new DynamoDBFactory();
        FollowService followService = new FollowService(factory);

        try {
            for (SQSEvent.SQSMessage msg : event.getRecords()) {
                Status newStatus = new GsonBuilder().create().fromJson(msg.getBody(), Status.class);
                List<String> followerAliases = followService.getAllFollowerAliases(newStatus.getUser().getAlias());

                sendBatchesToQueue(newStatus, followerAliases, 25);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return null;
    }

    private void sendBatchesToQueue(Status status, List<String> followerAliases, int limit) {
        List<String> aliases = new ArrayList<>();
        for(int i = 0; i < followerAliases.size(); i++) {
            aliases.add(followerAliases.get(i));

            if(aliases.size() == limit || i == followerAliases.size() - 1) {
                String message = createMessage(status, aliases);
                sendMessage(message);
                aliases = new ArrayList<>();
            }
        }
    }

    private String createMessage(Status status, List<String> aliases) {
        Batch batch = new Batch(status, aliases);
        String message = new GsonBuilder().create().toJson(batch);

        return message;
    }

    private void sendMessage(String message) {
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/012994777076/updateFeed";

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(message);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
        System.out.println("Sent message: " + message);

    }


}

