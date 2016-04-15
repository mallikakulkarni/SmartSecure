package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import edu.sjsu.smartsecure.domain.CleansedRecord;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.logging.Logger;

/**
 * Created by mallika on 4/4/16.
 */
public class CleansedDataHandler {
    private String profession;
    private boolean safeRecord;
    private AggregationAndMasterUserTableHandler aggregationAndMasterUserTableHandler;

    public CleansedDataHandler() {
        aggregationAndMasterUserTableHandler = new AggregationAndMasterUserTableHandler();
    }

    /*
        TODO - Apoorva Write query here ot get all records aggregated on user id, date etc.
        TODO - Replace hard coded numeric figures with the actual variables recd from method
    */

    public void createRecord() {
        String userId = "123";
        aggregationAndMasterUserTableHandler.getProfession(userId);
        //TODO Change
        CleansedRecord cleansedRecord = new CleansedRecord(profession, 0, 0, true, true);
        insertRecord(cleansedRecord);
    }

    private void insertRecord(CleansedRecord record) {
        DBCollection collection = MongoFactory.getCollection("TrainingData");
        record.set_id(new ObjectId());
        BasicDBObject dbRecord = createDbRecordObject(record);
        WriteResult res = collection.insert(dbRecord);
        System.out.println("Cleansed record " + record.get_id().toString() + " writtent to db");
    }

    //TODO Apoorva Add new fields
    private BasicDBObject createDbRecordObject(CleansedRecord record) {
        BasicDBObject dbRecord = new BasicDBObject();
        dbRecord.put("Profession", record.getProfession());
        dbRecord.put("MBRx", record.getMegaBytesRx());
        dbRecord.put("MBTx", record.getMegaBytesTx());
        dbRecord.put("SecSoftware", record.isSecurityAppPresent());
        dbRecord.put("Safe", record.isSafe());
        return dbRecord;
    }


}
