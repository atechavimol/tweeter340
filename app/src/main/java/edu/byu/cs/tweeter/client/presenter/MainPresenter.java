package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CheckFollowerObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CountTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter{
    private static final String LOG_TAG = "MainActivity";


    public interface MainView extends Presenter.View {

        void setFollowButton(boolean isFollower);

        void updateFollowUnfollow();

        void logout();

        void setFollowerCount(int count);

        void setFollowingCount(int count);

        void cancelPostingToast();
    }

    FollowService followService;
    UserService userService;
    StatusService statusService;
    
    public MainPresenter(View view) {
        super(view);
        followService = new FollowService();
    }

    protected UserService getUserService() {
        if(userService == null) {
            userService = new UserService();
        }
        return userService;
    }

    protected StatusService getStatusService() {
        if(statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
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

    public void logoutUser() {
        getUserService().logoutUser(new LogoutObserver());
    }

    public void updateFollowingAndFollowers(User selectedUser) {
        followService.updateFollowingAndFollowers(selectedUser, new CountObserver());
    }

    public void postStatus(String post) {
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            getStatusService().postStatus(newStatus, new PostStatusObserver());

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }
    
    public class IsFollowerObserver implements CheckFollowerObserver {

        @Override
        public void setFollowButton(boolean isFollower) {
            ((MainView)view).setFollowButton(isFollower);
        }

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }
    }
    
    public class FollowUnfollowObserver implements SimpleNotificationObserver {

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleSuccess() {
            ((MainView)view).updateFollowUnfollow();
        }


    }

    public class LogoutObserver implements SimpleNotificationObserver {

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleSuccess() {
            ((MainView)view).logout();
        }
    }
    
    public class CountObserver implements CountTaskObserver {
        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }


        @Override
        public void setFollowing(int count) {
            ((MainView)view).setFollowingCount(count);
        }

        @Override
        public void setFollowers(int count) {
            ((MainView)view).setFollowerCount(count);
        }
    }

    public class PostStatusObserver implements SimpleNotificationObserver {

        @Override
        public void displayMessage(String s) {
            view.displayMessage(s);
        }

        @Override
        public void handleSuccess() {
            ((MainView)view).cancelPostingToast();
            view.displayMessage("Successfully Posted!");
        }
    }

}
