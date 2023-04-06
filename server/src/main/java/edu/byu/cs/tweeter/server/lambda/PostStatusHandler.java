package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.GsonBuilder;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {
    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        StatusService service = new StatusService();

        PostStatusResponse response = service.postStatus(request);

        if(response.isSuccess()) {
            try {
                String messageBody = new GsonBuilder().create().toJson(request.getStatus());
                String queueUrl = "https://sqs.us-west-2.amazonaws.com/012994777076/postStatus";

                SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(messageBody);

                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
            } catch (Exception e) {
                return new PostStatusResponse(e.getMessage());
            }
        }
        return response;
    }
}
