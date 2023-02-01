package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {

    public interface Observer {
        void startActivity(User user);

        void displayMessage(String s);
    }

    public void getUserProfile(String alias, Observer observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                alias, new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);

        observer.displayMessage("Getting user's profile...");
    }
    public void loginUser(String alias, String password, Observer observer) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }
    /**
     * Message handler (i.e., observer) for GetUserTask.
     */

    private class GetUserHandler extends Handler {
        private Observer observer;

        public GetUserHandler(Observer observer) {
            super(Looper.getMainLooper());
            this.observer = observer;

        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
                observer.startActivity(user);
            } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
                observer.displayMessage("Failed to get user's profile: " + message);
            } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
                observer.displayMessage("Exception: " + ex.getMessage());
            }
        }
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends Handler {
        Observer observer;
        public LoginHandler(Observer observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LoginTask.SUCCESS_KEY);
            if (success) {
                User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

                // Cache user session information
                Cache.getInstance().setCurrUser(loggedInUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                observer.displayMessage("Hello " + Cache.getInstance().getCurrUser().getName());
                observer.startActivity(loggedInUser);

            } else if (msg.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LoginTask.MESSAGE_KEY);
                observer.displayMessage("Failed to login: " + message);
            } else if (msg.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LoginTask.EXCEPTION_KEY);
                observer.displayMessage("Failed to login because of exception: " + ex.getMessage());
            }
        }
    }
}
