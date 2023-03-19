//package edu.byu.cs.tweeter.client;
//
//import android.os.Bundle;
//import android.os.Message;
//
//
//import androidx.annotation.NonNull;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.util.concurrent.TimeUnit;
//
//import edu.byu.cs.tweeter.client.model.service.StatusService;
//import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedNotificationHandler;
//import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedNotificationObserver;
//import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
//import edu.byu.cs.tweeter.model.domain.User;
//
//public class GetStoryTest {
//    public class TestHandler extends PagedNotificationHandler {
//        public TestHandler(PagedNotificationObserver observer) {
//            super(observer);
//        }
//
//        @Override
//        protected void handleSuccessMessage(PagedNotificationObserver observer, Bundle data){
//            super.handleSuccessMessage(observer, data);
//        }
//    }
//
//    private User user;
//    private PagedPresenter.GetItemsObserver observerMock;
//    private TestHandler testHandlerMock;
//    private StatusService statusServiceSpy;
//    private int pageSize = 3;
//
//    @BeforeEach
//    public void setup() {
//        user = new User("Amy", "Tech", "@amy", "image");
//        observerMock = Mockito.mock(PagedPresenter.GetItemsObserver.class);
//        testHandlerMock = Mockito.mock(TestHandler.class);
//        statusServiceSpy = Mockito.spy(StatusService.class);
//
//    }
//
//    @Test
//    @DisplayName("postSuccessful")
//    public void testPostStatus_postSuccessful() throws InterruptedException {
//
//        Mockito.doReturn(testHandlerMock).when(statusServiceSpy).getPagedNotificationHandler(observerMock);
//        statusServiceSpy.getStory(user, pageSize, null, observerMock);
//
//        Mockito.verify(testHandlerMock).handleMessage(Mockito.any());
//    }
//}
