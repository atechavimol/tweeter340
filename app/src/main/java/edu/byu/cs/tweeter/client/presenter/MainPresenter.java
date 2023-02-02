package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {


    

    public interface View {

        void setFollowButton(boolean isFollower);

        void displayMessage(String s);

        void updateFollowUnfollow(boolean b);

        void enableFollowButton(boolean b);
    }
    
    View view;
    
    FollowService followService;
    
    public MainPresenter(View view) {
        this.view = view;
        followService = new FollowService();
    }

    public void isFollower(User selectedUser) {
        followService.isFollower(selectedUser, new IsFollowerObserver());
    }

    public void followOrUnfollow(User selectedUser, boolean isFollowing) {
        if (isFollowing) {
            followService.unfollowUser(selectedUser, new FollowUnfollowObserver());
            view.displayMessage("Removing " + selectedUser.getName() + "...");
        } else {
            followService.followUser(selectedUser, new FollowUnfollowObserver());
            view.displayMessage("Adding " + selectedUser.getName() + "...");
        }
    }
    
    public class IsFollowerObserver implements FollowService.isFollowerObserver {

        @Override
        public void setFollowButton(boolean isFollower) {
            view.setFollowButton(isFollower);
        }

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }
    }
    
    public class FollowUnfollowObserver implements FollowService.FollowUnfollowObserver {

        @Override
        public void update(boolean b) {
            view.updateFollowUnfollow(b);
        }

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }

        @Override
        public void enableFollowButton(boolean b) {
            view.enableFollowButton(b);
        }


    }
}
