package com.pickasso.analysis;

import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class City {

	static final double CHUNK_SCORE = 1.0;

	public static void main(String[] args) {

		JSONParser parser = new JSONParser();
		MapDictionary<String> dictionary = new MapDictionary<String>();

		try {
			Object obj = parser.parse(new FileReader("resources/les-communes-generalisees-dile-de-france.json"));
			JSONArray jsonArray = (JSONArray) obj;
			jsonArray.stream().forEach(object -> {
				JSONObject jsonObject = (JSONObject) object;
				JSONObject fields = (JSONObject) jsonObject.get("fields");
				String nomCom = (String) fields.get("nomcom");
				dictionary.addEntry(new DictionaryEntry<String>(nomCom, "CITY", CHUNK_SCORE));
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ExactDictionaryChunker dictionaryChunkerTT = new ExactDictionaryChunker(dictionary,
				IndoEuropeanTokenizerFactory.INSTANCE, true, true);

		ExactDictionaryChunker dictionaryChunkerTF = new ExactDictionaryChunker(dictionary,
				IndoEuropeanTokenizerFactory.INSTANCE, true, false);

		ExactDictionaryChunker dictionaryChunkerFT = new ExactDictionaryChunker(dictionary,
				IndoEuropeanTokenizerFactory.INSTANCE, false, true);

		ExactDictionaryChunker dictionaryChunkerFF = new ExactDictionaryChunker(dictionary,
				IndoEuropeanTokenizerFactory.INSTANCE, false, false);

		System.out.println("\nDICTIONARY\n" + dictionary);

		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("test");
		MongoCollection<Document> collection = database.getCollection("associations");
		String associationName = "La Croix-rouge";
		Document document = collection.find(eq("name", associationName)).first();
		Document carenews = (Document) document.get("carenews");
		ArrayList<Document> articleList = (ArrayList<Document>) carenews.get("articles");

		try {
			FileWriter fw = new FileWriter("city.json", false);
			fw.write("{\"associations\":{\"name\":\"" + associationName + "\",\"carenews\":{\"articles\":[");
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		articleList.forEach(article -> {
			String text = article.getString("content");
			String title = article.getString("title");
			Object id = article.get("_id");
			// title = title.replaceAll("\"", "\\\\\"");
			String str = "{\"id\":\"" + id + "\",\"city\":[";
			str += chunk(dictionaryChunkerTT, text) + "]},";
			chunk(dictionaryChunkerTF, text);
			chunk(dictionaryChunkerFT, text);
			chunk(dictionaryChunkerFF, text);

			try {
				FileWriter fw = new FileWriter("city.json", true);
				fw.write(str);
				fw.flush();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		try {
			FileReader fr = new FileReader("city.json");
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			if ((s.substring(s.length() - 1, s.length())).equals(",")) {
				s = s.substring(0, s.length() - 1);
			}
			fr.close();

			FileWriter fw = new FileWriter("city.json", false);
			fw.write(s + "]}}}");
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String chunk(ExactDictionaryChunker chunker, String text) {
		System.out.println("\nChunker." + " All matches=" + chunker.returnAllMatches() + " Case sensitive="
				+ chunker.caseSensitive());
		Chunking chunking = chunker.chunk(text);
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeSpecialFloatingPointValues();
		Gson gson = gsonBuilder.create();
		String str = "";
		for (Chunk chunk : chunking.chunkSet()) {
			int start = chunk.start();
			int end = chunk.end();
			String type = chunk.type();
			double score = chunk.score();
			String phrase = text.substring(start, end);
			str += "{\"phrase\":\"" + phrase + "\",\"start\":" + start + ",\"end\":" + end + ",\"type\":\"" + type
					+ "\",\"score\":\"" + score + "\"},";
		}
		if (!str.equals("")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
}
