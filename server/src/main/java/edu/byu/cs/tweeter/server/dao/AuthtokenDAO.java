package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthtokenDAO {
    AuthToken insertToken(String username);
}
