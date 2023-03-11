package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthenticateTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.client.presenter.AuthenticatePresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;

public class UserService{

    public void getUserProfile(String alias, UserTaskObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                alias, new GetUserHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);
        observer.displayMessage("Getting user's profile...");
    }
    public void loginUser(String alias, String password, UserTaskObserver observer) {
        LoginTask loginTask = new LoginTask(alias, password, new AuthenticateTaskHandler(observer));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void registerUser(String firstName, String lastName, String alias, String password, String imageBytesBase64, AuthenticatePresenter.UserObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageBytesBase64, new AuthenticateTaskHandler(observer));
        BackgroundTaskUtils.runTask(registerTask);
    }

    public void logoutUser(MainPresenter.LogoutObserver logoutObserver) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new SimpleNotificationHandler(logoutObserver) );
        BackgroundTaskUtils.runTask(logoutTask);
    }

}
