package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginFragment;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class PostStatusTest {
    private MainPresenter mainPresenter;
    private LoginPresenter loginPresenter;

    private MainActivity mainActivityMock;
    private LoginFragment loginFragmentMock;

    private CountDownLatch countDownLatch;

    private ServerFacade serverFacade;

    private String userAlias;
    private String userPassword;

    private String post;
    private String mention;
    private String url;


    @BeforeEach
    public void setup() {
        mainActivityMock = Mockito.mock(MainActivity.class);
        loginFragmentMock = Mockito.mock(LoginFragment.class);

        mainPresenter = new MainPresenter(mainActivityMock);
        loginPresenter = new LoginPresenter(loginFragmentMock);

        serverFacade = new ServerFacade();

        mention = "@amy";
        url = "https://www.byu.edu";
        post = "4c! " + mention + " " + url;

        userAlias = "@kym";
        userPassword = "password";

        resetCountDownLatch();
    }


    @Test
    public void testPostStatus_validRequest_correctResponse() throws InterruptedException, IOException, TweeterRemoteException {

        Answer countDown = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();
                return null;
            }
        };

        Mockito.doAnswer(countDown).when(loginFragmentMock).startActivity((User) Mockito.any());
        Mockito.doAnswer(countDown).when(mainActivityMock).displayMessage(Mockito.anyString());


        loginPresenter.loginUser(userAlias, userPassword);
        awaitCountDownLatch();

        resetCountDownLatch();
        Long startTime = System.currentTimeMillis();

        mainPresenter.postStatus(post);
        awaitCountDownLatch();

        Mockito.verify(mainActivityMock).displayMessage("Successfully Posted!");

        StoryResponse response = serverFacade.getStory(new StoryRequest(Cache.getInstance().getCurrUserAuthToken(), userAlias, 1, null),
                         "/story");
        Status status = (Status) response.getItems().get(0);


        Assertions.assertEquals(post, status.getPost());
        Assertions.assertEquals(userAlias, status.getUser().getAlias());
        Assertions.assertEquals(1, status.getMentions().size());
        Assertions.assertEquals(mention, status.getMentions().get(0));
        Assertions.assertEquals(1, status.getUrls().size());
        Assertions.assertEquals(url, status.getUrls().get(0));
        Assertions.assertTrue(status.getTimestamp() >= startTime);
        Assertions.assertTrue(status.getTimestamp() < System.currentTimeMillis());

    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }
}
