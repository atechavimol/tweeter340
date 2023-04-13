package edu.byu.cs.tweeter.server.Fill;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.server.dao.Factory;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs.DynamoDBFactory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.schemas.UserTable;

public class Filler {

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 7000;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@amy";

    public static void main(String[] args) {
        fillDatabase();
    }

    public static void fillDatabase() {

        // Get instance of DAOs by way of the Abstract Factory Pattern
        UserDAO userDAO = new DynamoDBFactory().getUserDAO();
        FollowsDAO followDAO = new DynamoDBFactory().getFollowsDAO();

        List<String> followers = new ArrayList<>();
        List<UserTable> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String firstName = "wow" + i;
            String lastName = "Last" + i;
            String alias = "@wow" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            UserTable user = new UserTable();
            user.setAlias(alias);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setFollowingCount(1);
            user.setFollowersCount(0);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        if (followers.size() > 0) {
            followDAO.addFollowersBatch(followers, FOLLOW_TARGET);
        }
    }
}

