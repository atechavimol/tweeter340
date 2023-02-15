package edu.byu.cs.tweeter.client.presenter;

import android.view.View;

import edu.byu.cs.tweeter.client.model.service.FollowService;

public abstract class Presenter {

    protected View view;

    public Presenter(View view) {
        this.view = view;
    }

    public interface View {
        void displayMessage(String s);
    }
}
