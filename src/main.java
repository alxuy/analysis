import com.aliasi.classify.DynamicLMClassifier;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class main {
    private static String[] CATEGORIES = {
            "Humanitaire",
            "Enfance",
            "Ecologie"
    };

    private static int NGRAM_SIZE = 6;

    public static void main(String[] args) {
        DynamicLMClassifier classifier = DynamicLMClassifier
                .createNGramBoundary(CATEGORIES, NGRAM_SIZE);
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("associations");
        Document document = collection.find(eq("name", "La Croix-rouge")).first();
        System.out.println(document);
    }
}
