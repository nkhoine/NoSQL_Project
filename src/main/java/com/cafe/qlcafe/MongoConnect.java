package com.cafe.qlcafe;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MongoConnect {

    @Value("${mongodb.uri:mongodb://localhost:27017}")
    private String mongoUri;

    @Value("${mongodb.database:QLCafe}")
    private String dbName;

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    @PostConstruct
    public void init() {
        mongoClient = MongoClients.create(mongoUri);
        database = mongoClient.getDatabase(dbName);
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("QLCafe");
        }
        return database;
    }

    public static MongoCollection<Document> getCollection(String name) {
        return getDatabase().getCollection(name);
    }

    @PreDestroy
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
