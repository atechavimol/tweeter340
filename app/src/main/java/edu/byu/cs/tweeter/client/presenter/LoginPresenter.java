package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter {

    public interface View {

        void displayMessage(String s);

        void setErrorView(String message);

        void startActivity(User user);

        void loginToast(String s);
    }

    View view;
    UserService userService;

    public LoginPresenter(View view) {
        this.view = view;
        userService = new UserService();
    }

    public void loginUser(String alias, String password) {
        try {
            validateLogin(alias, password);
            view.setErrorView(null);
            
            view.loginToast("Logging In...");
            userService.loginUser(alias, password, new LoginPresenter.LoginUserObserver());

        } catch (Exception e) {
            view.setErrorView(e.getMessage());
        }
    }

    public void validateLogin(String alias, String password) {
        if (alias.length() > 0 && alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }
    
    public class LoginUserObserver implements UserService.Observer {

        @Override
        public void startActivity(User user) {
            view.startActivity(user);
        }

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }
    }


}
