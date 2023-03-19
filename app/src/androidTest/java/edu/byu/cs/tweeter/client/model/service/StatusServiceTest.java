package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusServiceTest {
    private User currentUser;
    private AuthToken currAuthToken;
    private StatusService statusServiceSpy;
    private StatusServiceObserver statusServiceObserver;

    private CountDownLatch countDownLatch;

    @BeforeEach
    public void setup() {
        currentUser = new User("FirstName", "LastName", null);
        currAuthToken = new AuthToken();

        statusServiceSpy = Mockito.spy(StatusService.class);

        // Setup an observer for the FollowService
        statusServiceObserver = new StatusServiceObserver();

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private class StatusServiceObserver implements PagedNotificationObserver{

        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleSuccess(List statuses, Boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.statuses = statuses;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void displayMessage(String message) {
            this.success = false;
            this.message = message;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }


        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }
    }

    @Test
    public void testGetStory_validRequest_correctResponse() throws InterruptedException {
        statusServiceSpy.getStory(currentUser, 5, null, statusServiceObserver);
        awaitCountDownLatch();

        Assertions.assertTrue(statusServiceObserver.isSuccess());
        Assertions.assertNull(statusServiceObserver.getMessage());
        Assertions.assertEquals(5, statusServiceObserver.getStatuses().size());
        Assertions.assertTrue(statusServiceObserver.getHasMorePages());
        Assertions.assertNull(statusServiceObserver.getException());
    }


}
