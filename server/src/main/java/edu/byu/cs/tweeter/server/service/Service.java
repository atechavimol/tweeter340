package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.Factory;
import edu.byu.cs.tweeter.server.dao.dynamoDB.dynamoDAOs.DynamoDBFactory;


public class Service {
    protected Factory factory;

    public Service(){
        this.factory = new DynamoDBFactory();
    }

    public Service(Factory factory) {
        this.factory = factory;
    }
}
