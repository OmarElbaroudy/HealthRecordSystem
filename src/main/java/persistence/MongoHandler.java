package persistence;

import persistence.models.Block;
import persistence.models.ClinicCredentials;

import static com.mongodb.client.model.Filters.eq;


public class MongoHandler extends MongoConnectionHandler {
    private final String fieldName = "metaData.blockIndex";

    public MongoHandler(){
        super();
    }

    public void saveBlock(Block block) {
        int idx = block.getMetaData().getBlockIndex();
        this.getBlockchain().findOneAndDelete(eq(fieldName, idx));
        this.getBlockchain().insertOne(block);
        System.out.println("block inserted successfully");
    }

    public void deleteBlock(int idx) {
        this.getBlockchain().findOneAndDelete(eq(fieldName, idx));
        System.out.println("block deleted successfully");
    }

    public Block getBlock(int idx) {
        return this.getBlockchain().find(eq(fieldName, idx)).first();
    }

    public void saveClinic(ClinicCredentials clinicCredentials){
        String fieldName = "identifierPK";
        this.getClinicCredentials().findOneAndDelete(eq(fieldName, clinicCredentials.getIdentifierPK()));
        this.getClinicCredentials().insertOne(clinicCredentials);
        System.out.println("Clinic is registered at certificate authority successfully");
    }

    public ClinicCredentials getClinic(String publicKey){
        String fieldName = "identifierPK";
        return this.getClinicCredentials().find(eq(fieldName, publicKey)).first();
    }
}
