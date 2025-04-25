package indexer.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.InputStream;
import java.util.Properties;

public class MongoConnector {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void initialize() {
        try (InputStream input = MongoConnector.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            mongoClient = MongoClients.create(prop.getProperty("mongo.uri"));
            database = mongoClient.getDatabase(prop.getProperty("mongo.database"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load MongoDB config", e);
        }
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}