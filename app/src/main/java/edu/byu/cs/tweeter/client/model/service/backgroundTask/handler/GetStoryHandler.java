package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Message handler (i.e., observer) for GetStoryTask.
 */
public class GetStoryHandler extends PagedNotificationHandler {

    public GetStoryHandler(StatusService.Observer observer) {
        super(observer);
    }

//    @Override
//    public void handleMessage(@NonNull Message msg) {
//
//        boolean success = msg.getData().getBoolean(GetStoryTask.SUCCESS_KEY);
//        if (success) {
//            List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.ITEMS_KEY);
//            boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);
//            observer.addItems(statuses, hasMorePages);
//        } else if (msg.getData().containsKey(GetStoryTask.MESSAGE_KEY)) {
//            String message = msg.getData().getString(GetStoryTask.MESSAGE_KEY);
//            observer.displayMessage("Failed to get story: " + message);
//        } else if (msg.getData().containsKey(GetStoryTask.EXCEPTION_KEY)) {
//            Exception ex = (Exception) msg.getData().getSerializable(GetStoryTask.EXCEPTION_KEY);
//            observer.displayMessage("Failed to get story because of exception: " + ex.getMessage());
//        }
//    }
}
