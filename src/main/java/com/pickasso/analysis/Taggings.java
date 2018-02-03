package com.pickasso.analysis;

import static com.mongodb.client.model.Filters.eq;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.WhitespaceNormTokenizerFactory;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/** Use SentenceModel to find sentence boundaries in text */
public class Taggings {

	public static void main(String[] args) throws IOException, ParseException {

		// Pattern regex = Pattern.compile("(-|,|;|'|’|");
		// TOKENIZER_FACTORY = new
		// RegExFilteredTokenizerFactory(TOKENIZER_FACTORY,regex);

		List<String> tokenList = new ArrayList<String>();
		List<String> whiteList = new ArrayList<String>();

		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("test");
		MongoCollection<Document> collection = database.getCollection("associations");
		String associationName = "La Croix-rouge";
		Document document = collection.find(eq("name", associationName)).first();
		Document carenews = (Document) document.get("carenews");
		ArrayList<Document> articleList = (ArrayList<Document>) carenews.get("articles");
		// Document article = articleList.get(0);
		// String text = article.getString("content");
		// Tokenizer tokenizer;

		// BufferedReader reader = new BufferedReader(new
		// FileReader("resources/stopwords-fr.txt"));
		// Set<String> stopWordSet = new HashSet<String>();
		// String line = null;
		// while ((line = reader.readLine()) != null) {
		// stopWordSet.add(line);
		// }
		// reader.close();

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader("resources/stopwords-fr.json"));
		JSONArray jsonArray = (JSONArray) obj;
		Set<String> stopWordSet = (Set<String>) jsonArray.stream().collect(Collectors.toSet());
		stopWordSet.add(".");
		stopWordSet.add(",");
		stopWordSet.add("-");
		stopWordSet.add("’");
		stopWordSet.add("'");
		stopWordSet.add(";");
		stopWordSet.add("«");
		stopWordSet.add("»");
		stopWordSet.add("\"");
		stopWordSet.add("/");
		stopWordSet.add("\\");
		stopWordSet.add(":");
		stopWordSet.add("(");
		stopWordSet.add(")");
		// List<String> setword = stopWordSet.stream().sorted((word1, word2) ->
		// word1.compareTo(word2))
		// .collect(Collectors.toList());
		articleList.forEach(article -> {
			String text = article.getString("content");
			TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
			TOKENIZER_FACTORY = new LowerCaseTokenizerFactory(TOKENIZER_FACTORY);
			TOKENIZER_FACTORY = new WhitespaceNormTokenizerFactory(TOKENIZER_FACTORY);
			TOKENIZER_FACTORY = new StopTokenizerFactory(TOKENIZER_FACTORY, stopWordSet);
			TOKENIZER_FACTORY = new WhitespaceNormTokenizerFactory(TOKENIZER_FACTORY);
			Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
			tokenizer.tokenize(tokenList, whiteList);
		});
		// tokenizer.tokenize(tokenList, whiteList);

		List<Word> wordList = new ArrayList<Word>();
		tokenList.forEach(token -> {
			Word word = wordList.stream().filter(c -> c.getWord().equals(token)).findFirst().orElse(null);
			int index = -1;
			if (word != null) {
				index = wordList.indexOf(word);
				if (index != -1) {
					word.setCount(word.getCount() + 1);
					wordList.set(index, word);
				}
			} else {
				wordList.add(new Word(token, 1, 1));
			}
		});

		List<Word> result = wordList.stream().sorted((word1, word2) -> word2.getCount().compareTo(word1.getCount()))
				.collect(Collectors.toList());

		result = result.stream().map(word -> {
			articleList.forEach(article -> {
				String text = article.getString("content");
				int index = -1;
				index = text.indexOf(word.getWord());
				if (index != -1) {
					word.setNumberDocContain(word.getNumberDocContain() + 1);
				}
			});
			return word;
		}).collect(Collectors.toList());

		// result.stream().map(token -> {
		// System.out.println(token.toString());
		// // System.out.print(token.getWord());
		// // System.out.println(" " + token.getCount());
		// return null;
		// }).collect(Collectors.toList());

		result = result.stream().map(token -> {
			double tf = ((double) token.getCount() / tokenList.size());
			double idf = StrictMath.log(articleList.size() / token.getNumberDocContain());
			token.setTfIdf(tf * idf);
			// System.out.println(token.toString());
			return token;
		}).collect(Collectors.toList());

		List<Word> result2 = wordList.stream().sorted((word1, word2) -> word2.getTfIdf().compareTo(word1.getTfIdf()))
				.collect(Collectors.toList());

		result.forEach(word -> {
			System.out.println(word.toString());
		});

		// result2.forEach(word -> {
		// System.out.println(word.toString());
		// });

		System.out.println();
		System.out.println(tokenList.size() + " TOKENS");
		System.out.println(whiteList.size() + " WHITESPACES");
	}
}
