package persistence;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import persistence.models.Block;
import persistence.models.ClinicCredentials;

import java.util.Objects;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


/**
 * an object of mongoConnectionHandler should be only once for each node
 * then closed when the program terminates.
 * <p>
 * nodes are given a unique UUID which is used to create a unique database
 */
public class MongoConnectionHandler {

    private final String DB_NAME = "EHS";
    private final MongoClient mc;
    private final MongoDatabase db;
    private final MongoCollection<Block> blockchain;

    private final MongoCollection<ClinicCredentials> clinicCredentials;

    public MongoConnectionHandler() {
        this.mc = MongoClients.create(getMongoClientSettings());
        this.db = mc.getDatabase(DB_NAME);
        this.blockchain = this.db.getCollection("Block", Block.class);
        this.clinicCredentials = this.db.getCollection("ClinicCredentials", ClinicCredentials.class);
        System.out.printf("connected to db %s successfully %n", DB_NAME);
    }

    /**
     * @return mongodb uri connection String
     */
    private ConnectionString getConnectionString() {
        return new ConnectionString(Objects.requireNonNull(System.getenv("MONGODB_URI")));
    }

    /**
     * @return client setting for mapping pojo models to documents
     */
    private MongoClientSettings getMongoClientSettings() {
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        return MongoClientSettings.builder()
                .applyConnectionString(getConnectionString())
                .codecRegistry(codecRegistry)
                .build();
    }

    /**
     * @return mongo client session
     */
    public MongoClient getMc() {
        return mc;
    }


    /**
     * @return unique data base for this mongo client
     */
    public MongoDatabase getDb() {
        return db;
    }


    /**
     * @return blockchain collection
     */
    public MongoCollection<Block> getBlockchain() {
        return blockchain;
    }

    public MongoCollection<ClinicCredentials> getClinicCredentials() {
        return clinicCredentials;
    }

    /**
     * used to close connection after mongo client is used
     */
    public void closeConnection() {
        mc.close();
    }
}
