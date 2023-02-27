package edu.byu.cs.tweeter.client.presenter;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainPresenterUnitTest {
    private MainPresenter.MainView mockView;
    private StatusService mockStatusService;
    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup() {
        // Create mocks
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));

        Mockito.doReturn(mockStatusService).when(mainPresenterSpy).getStatusService();
    }

    @Test
    @DisplayName("postSuccessful")
    public void testPostStatus_postSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.PostStatusObserver observer = invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
                Status status = invocation.getArgument(0, Status.class);

                assert status != null;

                observer.handleSuccess();
                return null;
            }
        };

        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());

        mainPresenterSpy.postStatus("Test Post");

        verify(mockView).cancelPostingToast();
        verify(mockView).displayMessage("Successfully Posted!");
    }

    @Test
    @DisplayName("postFailedWithMessage")
    public void testLogout_postFailedWithMessage() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.PostStatusObserver observer = invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
                Status status = invocation.getArgument(0, Status.class);

                assert status != null;

                String message = "ERROR MESSAGE";

                observer.displayMessage("Failed with error: " + message);
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());

        mainPresenterSpy.postStatus("Test Post");
        Mockito.verify(mockView).displayMessage(ArgumentMatchers.startsWith("Failed with error:"));
    }

    @Test
    @DisplayName("pastFailedWithException")
    public void testLogout_postFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.PostStatusObserver observer = invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
                Status status = invocation.getArgument(0, Status.class);

                assert status != null;

                String message = "EXCEPTION MESSAGE";

                observer.displayMessage("Failed with exception:" + message);
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());

        mainPresenterSpy.postStatus("Test Post");
        Mockito.verify(mockView).displayMessage(ArgumentMatchers.startsWith("Failed with exception:"));
    }




}
//public class MainPresenterUnitTest {
//
//    private MainPresenter.MainView mockView;
//    private UserService mockUserService;
//    private Cache mockCache;
//    private  MainPresenter mainPresenterSpy;
//
//    @BeforeEach
//    public void setup() {
//        // Create mocks
//        mockView = Mockito.mock(MainPresenter.MainView.class);
//        mockUserService = Mockito.mock(UserService.class);
//        mockCache = Mockito.mock(Cache.class);
//
//        mainPresenterSpy = Mockito.spy( new MainPresenter(mockView));
//        Mockito.doReturn(mockUserService).when(mainPresenterSpy).getUserService();
//
//        Cache.setInstance(mockCache);
//    }
//
//    @Test
//    @DisplayName("logoutSuccessful")
//    public void testLogout_logoutSuccessful(){
//
//        Answer<Void> answer = new Answer<>() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//               MainPresenter.LogoutObserver observer = invocation.getArgument(0, MainPresenter.LogoutObserver.class);
//               observer.handleSuccess();
//               return null;
//            }
//        };
//
//        Mockito.doAnswer(answer).when(mockUserService).logoutUser(Mockito.any());
//        mainPresenterSpy.logoutUser();
//
//
//
//        Mockito.verify(mockView).logout();
//        Mockito.verify(mockCache).clearCache();
//    }
//
//    @Test
//    @DisplayName("logoutFailedWithMessage")
//    public void testLogout_logoutFailedWithMessage() {
//
//    }}
//


