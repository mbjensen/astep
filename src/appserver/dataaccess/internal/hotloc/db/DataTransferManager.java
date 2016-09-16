package dk.aau.astep.appserver.dataaccess.internal.hotloc.db;

import com.mongodb.Block;
import com.mongodb.BulkWriteException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;
import org.bson.Document;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataTransferManager {
    private Instant         timeWindowFrom;
    private Instant         getTimeWindowTo;
    private Mongodb         mongoClient;
    private MongoDatabase   database;
    private String          newCollectionName;

    private static final int DATA_TRANSFER_INTERVAL = 31;

    /***
     * Constructor which sets the time windows in which data should be retrieved and transferred to the new collection.
     * Based on the statically defined transfer interval (DATA_TRANSFER_INTERVAL).
     * Connects to the database and retrieves the aSTEP MongoDB database
     */
    public DataTransferManager() {
        this.timeWindowFrom     = Instant.now().minus(Duration.ofMinutes(DATA_TRANSFER_INTERVAL));
        this.getTimeWindowTo    = Instant.now().plus(Duration.ofMinutes(1));
        this.mongoClient        = new Mongodb();
        this.database           = mongoClient.getDatabase();
    }

    /***
     * Starts the transfer process, by retrieving the data, creates a new collection and bulk writes the data
     * to the new location.
     * @return Boolean value indication whether the transfer process was successful or not.
     * If no data exist within the time window no new collection will be created and it will return true
     */
    public Boolean startTransferProcess() {
        List<WriteModel<Document>> transferDocs = getDataToBeTransferred();

        if (!transferDocs.isEmpty()) {
            newCollectionName = "toPersistentFrom" + timeWindowFrom.getEpochSecond();
            database.createCollection(newCollectionName);
            MongoCollection<Document> newCollection = database.getCollection(newCollectionName);

            try {
                BulkWriteOptions bulkWriteOptions = new BulkWriteOptions();
                // Unordered bulk writing should increase performance when the collection is sharded
                bulkWriteOptions.ordered(false);

                newCollection.bulkWrite(transferDocs, bulkWriteOptions);
                return true;
            } catch (BulkWriteException e) {
                ALogger.log("Mongodb, bulk write error. Bulk writes which failed: " + e.getWriteErrors().toString() + " Error message: " + e.getMessage(), Module.DB, Level.ERROR);
            }
        } else { return true; }

        return false;
    }

    public List<Document> getDataFromPersistentCollection(Instant timestamp) {
        List<Document> rawLocationData  = new ArrayList<>();

        // Get a specific collection based on the timestamp from the beginning of the time window of the data in the collection
        MongoCollection<Document> collection = database.getCollection(newCollectionName);

        // -1 in the sort modifier indicates descending order
        FindIterable<Document> iterable = collection.find().sort(new Document("timestamp", -1));

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> rawLocationData.add(document));
        }

        return rawLocationData;
    }

    /***
     * Helper function for getting the data from the defined time window
     * @return List of insert documents to be transferred to the new collection
     */
    private List<WriteModel<Document>> getDataToBeTransferred() {
        List<WriteModel<Document>> transferDocs = new ArrayList<>();
        MongoCollection<Document> collection    = mongoClient.getCollection();

        Document query = new Document("timestamp", new Document()
                                .append("$gte", new Date().from(timeWindowFrom))
                                .append("$lte", new Date().from(getTimeWindowTo))
                            );

        FindIterable<Document> iterable = collection.find(query);

        iterable.forEach((Block<Document>) document -> transferDocs.add(new InsertOneModel<>(document)));

        return transferDocs;
    }
}