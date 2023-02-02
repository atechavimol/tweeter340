package edu.byu.cs.tweeter.client.presenter;


import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter {

    public interface View {

        void startActivity(User user);

        void displayMessage(String s);

        void setErrorView(String s);

        void registeringToast(String s);

        String getImageByteString();
    }

    View view;
    UserService userService;

    public RegisterPresenter(View view){
        this.view = view;
        userService = new UserService();
    }

    public void registerUser(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        try {
            validateRegistration(firstName, lastName, alias, password, imageToUpload);
            view.setErrorView(null);

            view.registeringToast("Registering...");

            String imageString = view.getImageByteString();
            userService.registerUser(firstName, lastName, alias, password, imageString, new RegisterObserver());

        } catch (Exception e) {
            view.setErrorView(e.getMessage());
        }
    }

    public void validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }

    public class RegisterObserver implements UserService.Observer {

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
