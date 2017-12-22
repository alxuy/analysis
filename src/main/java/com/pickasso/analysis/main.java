package com.pickasso.analysis;
import com.aliasi.classify.*;
import com.aliasi.util.AbstractExternalizable;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static org.apache.commons.lang3.StringUtils.stripAccents;

public class main {
    private static String[] CATEGORIES = {
            "Humanitaire",
            "Enfance",
            "Ecologie"
    };

    private static int NGRAM_SIZE = 6;

    private static boolean containsIgnoreCaseAndAccent(String string, ArrayList<String> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (stripAccents(arrayList.get(i)).toLowerCase().contains(string.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args)
            throws ClassNotFoundException, IOException {
        DynamicLMClassifier classifier = DynamicLMClassifier
                .createNGramBoundary(CATEGORIES, NGRAM_SIZE);
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("associations");
        Document document = collection.find(eq("name", "La Croix-rouge")).first();
        Document ulule = (Document) document.get("ulule");
        ArrayList projects = (ArrayList) ulule.get("projects");
        for (int i = 0; i < CATEGORIES.length; i++) {
            for (int j = 0; j < projects.size(); j++) {
                Document project = (Document) projects.get(j);
                ArrayList tags = (ArrayList) project.get("tags");
                if (containsIgnoreCaseAndAccent(CATEGORIES[i], tags)) {
                    Classification classification = new Classification(CATEGORIES[i]);
                    Classified<CharSequence> classified =
                            new Classified<CharSequence>((String) project.get("content"), classification);
                    classifier.handle(classified);
                }
            }
        }

        //compiling
        System.out.println("Compiling");
        @SuppressWarnings("unchecked") // we created object so know it's safe
                JointClassifier<CharSequence> compiledClassifier
                = (JointClassifier<CharSequence>) AbstractExternalizable.compile(classifier);

        boolean storeCategories = true;
        JointClassifierEvaluator<CharSequence> evaluator
                = new JointClassifierEvaluator<CharSequence>(compiledClassifier, CATEGORIES, storeCategories);
        for (int i = 0; i < CATEGORIES.length; ++i) {
            for (int j = 0; j < projects.size(); ++j) {
                Document project = (Document) projects.get(j);
                String title = (String) project.get("title");
                String content = (String) project.get("content");
                System.out.print("Testing on " + CATEGORIES[i] + "/" + title + " ");
                Classification classification
                        = new Classification(CATEGORIES[i]);
                Classified<CharSequence> classified
                        = new Classified<CharSequence>(content, classification);
                evaluator.handle(classified);
                JointClassification jc =
                        compiledClassifier.classify(content);
                String bestCategory = jc.bestCategory();
                String details = jc.toString();
                System.out.println("Got best category of: " + bestCategory);
                System.out.println(details);
                System.out.println("---------------");
            }
        }
        ConfusionMatrix confMatrix = evaluator.confusionMatrix();
        System.out.println("Total Accuracy: " + confMatrix.totalAccuracy());

        System.out.println("\nFULL EVAL");
        System.out.println(evaluator);
    }
}
