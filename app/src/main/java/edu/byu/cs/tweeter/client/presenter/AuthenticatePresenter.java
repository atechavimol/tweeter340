package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticatePresenter extends Presenter{
    UserService userService;

    public interface AuthenticateView extends Presenter.View{

        void startActivity(User user);

        void setErrorView(String message);

        void showToast(String s);
    }

    public AuthenticatePresenter(AuthenticateView view) {
        super(view);
        userService = new UserService();
    }
}
