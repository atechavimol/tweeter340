package edu.byu.cs.tweeter.client.presenter;


public class LoginPresenter extends AuthenticatePresenter{
    public LoginPresenter(AuthenticateView view) {
        super(view);
    }

    public void loginUser(String alias, String password) {
        try {
            validateLogin(alias, password);
            ((AuthenticateView)view).setErrorView(null);

            ((AuthenticateView)view).showToast("Logging In...");
            userService.loginUser(alias, password, new UserObserver());

        } catch (Exception e) {
            ((AuthenticateView)view).setErrorView(e.getMessage());
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



}
