package edu.byu.cs.tweeter.client.presenter;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.UserTaskObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends AuthenticatePresenter{
    public RegisterPresenter(AuthenticateView view){
        super(view);
    }

    public void registerUser(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        try {
            validateRegistration(firstName, lastName, alias, password, imageToUpload);
            ((AuthenticateView)view).setErrorView(null);
            ((AuthenticateView)view).showToast("Registering...");

            userService.registerUser(firstName, lastName, alias, password, getImageString(imageToUpload), new RegisterObserver());

        } catch (Exception e) {
            ((AuthenticateView)view).setErrorView(e.getMessage());
        }
    }

    private void validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
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

    private String getImageString(ImageView imageToUpload) {

        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);
        return  imageBytesBase64;
    }

    public class RegisterObserver implements UserTaskObserver {

        @Override
        public void startActivity(User user) {
            ((AuthenticateView)view).startActivity(user);
        }

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }
    }
}
