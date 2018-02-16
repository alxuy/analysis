package com.pickasso.analysis;

import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/** Use SentenceModel to find sentence boundaries in text */
public class Taggings {

	private static Set<String> stopWordSet = new HashSet<String>();

	private static void initiateStopWord() {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("resources/stopwords-fr.json"));
			JSONArray jsonArray = (JSONArray) obj;
			stopWordSet = (Set<String>) jsonArray.stream().collect(Collectors.toSet());
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private static List<String> tokenize(String text, Set<String> stopWordSet) {
		List<String> tokenList = new ArrayList<String>();
		List<String> whiteList = new ArrayList<String>();
		TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
		TOKENIZER_FACTORY = new LowerCaseTokenizerFactory(TOKENIZER_FACTORY);
		TOKENIZER_FACTORY = new WhitespaceNormTokenizerFactory(TOKENIZER_FACTORY);
		TOKENIZER_FACTORY = new StopTokenizerFactory(TOKENIZER_FACTORY, stopWordSet);
		TOKENIZER_FACTORY = new WhitespaceNormTokenizerFactory(TOKENIZER_FACTORY);
		Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
		tokenizer.tokenize(tokenList, whiteList);
		return tokenList;
	}

	private static Set<String> tokenizeAssociationName(String text, Set<String> stopWordSet) {
		List<String> tokenList = new ArrayList<String>();
		List<String> whiteList = new ArrayList<String>();
		TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
		TOKENIZER_FACTORY = new LowerCaseTokenizerFactory(TOKENIZER_FACTORY);
		TOKENIZER_FACTORY = new WhitespaceNormTokenizerFactory(TOKENIZER_FACTORY);
		TOKENIZER_FACTORY = new StopTokenizerFactory(TOKENIZER_FACTORY, stopWordSet);
		TOKENIZER_FACTORY = new WhitespaceNormTokenizerFactory(TOKENIZER_FACTORY);
		Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
		tokenizer.tokenize(tokenList, whiteList);
		return tokenList.stream().collect(Collectors.toSet());
	}

	private static List<Word> reduce(List<String> tokenList) {
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
		return wordList;
	}

	private static List<Word> sortByCount(List<Word> wordList) {
		return wordList.stream().sorted((word1, word2) -> word2.getCount().compareTo(word1.getCount()))
				.collect(Collectors.toList());
	}

	private static List<Word> sortByDocCount(List<Word> wordList) {
		return wordList.stream().sorted((word1, word2) -> word2.getTfIdf().compareTo(word1.getTfIdf()))
				.collect(Collectors.toList());
	}

	private static List<Word> countDocContainWord(List<Word> wordList, List<Document> articleList) {
		List<Word> result = wordList.stream().map(word -> {
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
		return result;
	}

	private static List<Word> calculateTfIdf(List<Word> wordList, List<Document> articleList, List<String> tokenList) {
		List<Word> result = wordList.stream().map(token -> {
			double tf = ((double) token.getCount() / tokenList.size());
			double idf = StrictMath.log(articleList.size() / token.getNumberDocContain());
			token.setTfIdf(tf * idf);
			return token;
		}).collect(Collectors.toList());
		return result;
	}

	public static void main(String[] args) throws IOException, ParseException {

		initiateStopWord();

		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("test");
		MongoCollection<Document> collection = database.getCollection("associations");
		String associationName = "La Croix-rouge";
		Document document = collection.find(eq("name", associationName)).first();
		Document carenews = (Document) document.get("carenews");
		Document ulule = (Document) document.get("Ulule");
		mongoClient.close();
		List<Document> articleList = (List<Document>) carenews.get("articles");

		try {
			FileWriter fw = new FileWriter("taggings.json", false);
			fw.write("{\"associations\":{\"name\":\"" + associationName + "\",\"carenews\":{\"articles\":[");
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		stopWordSet.addAll(tokenizeAssociationName(associationName, stopWordSet));

		List<String> tokenList = new ArrayList<String>();
		List<Word> wordList = new ArrayList<Word>();

		articleList.forEach(article -> {
			String text = article.getString("content");
			String title = article.getString("title");
			title = title.replaceAll("\"", "\\\\\"");
			String str = "{\"title\":\"" + title + "\",\"taggings\":[";
			tokenList.addAll(tokenize(text, stopWordSet));
			List<Word> alist = reduce(tokenList);

			try {
				FileWriter fw = new FileWriter("taggings.json", true);
				fw.write(str);
				fw.flush();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			GsonBuilder gb = new GsonBuilder();
			gb.serializeSpecialFloatingPointValues();
			Gson g = gb.create();

			alist.forEach(word -> {
				try {
					FileWriter fw = new FileWriter("taggings.json", true);
					fw.write(g.toJson(word) + ",");
					fw.flush();
					fw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			try {
				FileReader fr = new FileReader("taggings.json");
				BufferedReader br = new BufferedReader(fr);
				String s = br.readLine();
				if ((s.substring(s.length() - 1, s.length())).equals(",")) {
					s = s.substring(0, s.length() - 1);
				}
				fr.close();

				FileWriter fw = new FileWriter("taggings.json", false);
				fw.write(s + "]},");
				fw.flush();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		try {
			FileReader fr = new FileReader("taggings.json");
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			if ((s.substring(s.length() - 1, s.length())).equals(",")) {
				s = s.substring(0, s.length() - 1);
			}
			fr.close();

			FileWriter fw = new FileWriter("taggings.json", false);
			fw.write(s + "]}}}");
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		wordList = reduce(tokenList);

		List<Word> result = sortByCount(wordList);

		result = countDocContainWord(result, articleList);

		result = calculateTfIdf(wordList, articleList, tokenList);

		List<Word> result2 = sortByDocCount(wordList);

		try {
			FileWriter fw = new FileWriter("output.json", false);
			fw.write("{\"associations\":{\"name\":\"" + associationName + "\",\"carenews\":{\"taggings\":[");
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeSpecialFloatingPointValues();
		Gson gson = gsonBuilder.create();

		result.forEach(word -> {
			try {
				FileWriter fw = new FileWriter("output.json", true);
				fw.write(gson.toJson(word) + ",");
				fw.flush();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			gson.toJson(word);
			// System.out.println(gson.toJson(word));
			// System.out.println(word.toString());
		});

		try {
			FileReader fr = new FileReader("output.json");
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			if ((s.substring(s.length() - 1, s.length())).equals(",")) {
				s = s.substring(0, s.length() - 1);
			}
			fr.close();

			FileWriter fw = new FileWriter("output.json", false);
			fw.write(s + "]}}}");
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// result2.forEach(word -> {
		// System.out.println(word.toString());
		// });

		System.out.println();
		System.out.println(tokenList.size() + " TOKENS");
	}
}
