import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.bson.types.ObjectId;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;


@Log4j2
public class MongoToEs {

    public static void main(String[] args) throws IOException {
        int pageSize = 10000;

        try {
            MongoClient mongo = new MongoClient("localhost", 27017);

            /**** Get database ****/
            // if database doesn't exists, MongoDB will create it for you
//            MongoDatabase test = mongo.getDatabase("test");
            DB db = mongo.getDB("test");

            /**** Get collection / table from 'testdb' ****/
            // if collection doesn't exists, MongoDB will create it for you
            DBCollection table = db.getCollection("enterprise");

            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")));
            DBCursor dbObjects;
            Long cnt = table.count();
            System.out.println(table.getStats().toString());
            Long page = getPageSize(cnt, pageSize);
            ObjectId lastIdObject = null;
            Long start = System.currentTimeMillis();
            long ss = start;
            for (Long i = 0L; i < page; i++) {
                start = System.currentTimeMillis();
                dbObjects = getCursorForCollection(table, lastIdObject, pageSize);
                System.out.println("第" + (i + 1) + "次查询，耗时:" + (System.currentTimeMillis() - start) + " 毫秒");
                List<DBObject> objs = dbObjects.toArray();
                start = System.currentTimeMillis();
                batchInsertToEsSync(client, objs, "enterprise_data_gov_20191025", "enterprise_data_gov");
                lastIdObject = (ObjectId) objs.get(objs.size() - 1).get("_id");
                System.out.println("第" + (i + 1) + "次插入，耗时:" + (System.currentTimeMillis() - start) + " 毫秒");
            }
            System.out.println("耗时:" + (System.currentTimeMillis() - ss) / 1000 + "秒");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static void batchInsertToEsSync(RestHighLevelClient client, List<DBObject> objs, String tableName, String type) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (DBObject obj : objs) {
            IndexRequest req = new IndexRequest(tableName, type);
            Map<String, Object> map = new HashMap<String, Object>();
            for (String key : obj.keySet()) {
                if ("_id".equalsIgnoreCase(key)) {
                    map.put("id", obj.get(key).toString());
                } else {
                    String valStr = "";
                    Object val = obj.get(key);
//                    if (val != null) {
//                        valStr = Base64.encodeBase64String(val.toString().getBytes());
//                    }
                    map.put(key, val);
                }
                map.remove("_utime");
                map.remove("trademark");

            }
            req.id(map.get("id").toString());
            req.source(map, XContentType.JSON);
            bulkRequest.add(req);
        }
        bulkRequest.timeout(TimeValue.timeValueHours(2));
//        client.bulkAsync(bulkRequest);
        BulkResponse bulkResponse = client.bulk(bulkRequest);
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
            if (bulkItemResponse.isFailed()) {
                System.out.println(bulkItemResponse.getId() + "," + bulkItemResponse.getFailureMessage());
            }
        }
    }

    public static DBCursor getCursorForCollection(DBCollection collection, ObjectId lastIdObject, int pageSize) {
        DBCursor dbObjects = null;
        if (lastIdObject == null) {
            lastIdObject = (ObjectId) collection.findOne().get("_id");
        }
        BasicDBObject query = new BasicDBObject();
        query.append("_id", new BasicDBObject("$gt", lastIdObject));
        BasicDBObject sort = new BasicDBObject();
        sort.append("_id", 1);
        dbObjects = collection.find(query).limit(pageSize).sort(sort);
        return dbObjects;
    }

    public static Long getPageSize(Long cnt, int pageSize) {
        return cnt % pageSize == 0 ? cnt / pageSize : cnt / pageSize + 1;
    }
}
