package com.pickasso.analysis;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;

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
public class WordCount {

	public static void main(String[] args) throws IOException {

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

		articleList.forEach(article -> {
			String text = article.getString("content");
			TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
			TOKENIZER_FACTORY = new LowerCaseTokenizerFactory(TOKENIZER_FACTORY);
			TOKENIZER_FACTORY = new WhitespaceNormTokenizerFactory(TOKENIZER_FACTORY);
			Set<String> stopWordSet = new HashSet<String>();
			stopWordSet.add("a");
			stopWordSet.add("ai");
			stopWordSet.add("au");
			stopWordSet.add("aux");
			stopWordSet.add("avec");
			stopWordSet.add("ce");
			stopWordSet.add("ces");
			stopWordSet.add("cet");
			stopWordSet.add("cette");
			stopWordSet.add("dans");
			stopWordSet.add("de");
			stopWordSet.add("des");
			stopWordSet.add("du");
			stopWordSet.add("elle");
			stopWordSet.add("en");
			stopWordSet.add("et");
			stopWordSet.add("eux");
			stopWordSet.add("il");
			stopWordSet.add("je");
			stopWordSet.add("la");
			stopWordSet.add("le");
			stopWordSet.add("les");
			stopWordSet.add("leur");
			stopWordSet.add("lui");
			stopWordSet.add("ma");
			stopWordSet.add("mais");
			stopWordSet.add("me");
			stopWordSet.add("même");
			stopWordSet.add("mes");
			stopWordSet.add("moi");
			stopWordSet.add("mon");
			stopWordSet.add("ne");
			stopWordSet.add("nos");
			stopWordSet.add("notre");
			stopWordSet.add("nous");
			stopWordSet.add("on");
			stopWordSet.add("ou");
			stopWordSet.add("par");
			stopWordSet.add("pas");
			stopWordSet.add("pour");
			stopWordSet.add("qu");
			stopWordSet.add("que");
			stopWordSet.add("qui");
			stopWordSet.add("sa");
			stopWordSet.add("se");
			stopWordSet.add("ses");
			stopWordSet.add("son");
			stopWordSet.add("sur");
			stopWordSet.add("ta");
			stopWordSet.add("te");
			stopWordSet.add("tes");
			stopWordSet.add("toi");
			stopWordSet.add("ton");
			stopWordSet.add("tu");
			stopWordSet.add("un");
			stopWordSet.add("une");
			stopWordSet.add("vos");
			stopWordSet.add("votre");
			stopWordSet.add("vous");
			stopWordSet.add("c");
			stopWordSet.add("d");
			stopWordSet.add("j");
			stopWordSet.add("l");
			stopWordSet.add("à");
			stopWordSet.add("m");
			stopWordSet.add("n");
			stopWordSet.add("s");
			stopWordSet.add("t");
			stopWordSet.add("y");
			stopWordSet.add("été");
			stopWordSet.add("étée");
			stopWordSet.add("étées");
			stopWordSet.add("étés");
			stopWordSet.add("étant");
			stopWordSet.add("étante");
			stopWordSet.add("étants");
			stopWordSet.add("étantes");
			stopWordSet.add("suis");
			stopWordSet.add("es");
			stopWordSet.add("est");
			stopWordSet.add("sommes");
			stopWordSet.add("êtes");
			stopWordSet.add("sont");
			stopWordSet.add("serai");
			stopWordSet.add("seras");
			stopWordSet.add("sera");
			stopWordSet.add("serons");
			stopWordSet.add("serez");
			stopWordSet.add("seront");
			stopWordSet.add("serais");
			stopWordSet.add("serait");
			stopWordSet.add("serions");
			stopWordSet.add("seriez");
			stopWordSet.add("seraient");
			stopWordSet.add("étais");
			stopWordSet.add("était");
			stopWordSet.add("étions");
			stopWordSet.add("étiez");
			stopWordSet.add("étaient");
			stopWordSet.add("fus");
			stopWordSet.add("fut");
			stopWordSet.add("fûmes");
			stopWordSet.add("fûtes");
			stopWordSet.add("furent");
			stopWordSet.add("sois");
			stopWordSet.add("soit");
			stopWordSet.add("soyons");
			stopWordSet.add("soyez");
			stopWordSet.add("soient");
			stopWordSet.add("fusse");
			stopWordSet.add("fusses");
			stopWordSet.add("fût");
			stopWordSet.add("fussions");
			stopWordSet.add("fussiez");
			stopWordSet.add("fussent");
			stopWordSet.add("ayant");
			stopWordSet.add("ayante");
			stopWordSet.add("ayantes");
			stopWordSet.add("ayants");
			stopWordSet.add("eu");
			stopWordSet.add("eue");
			stopWordSet.add("eues");
			stopWordSet.add("eus");
			stopWordSet.add("ai");
			stopWordSet.add("as");
			stopWordSet.add("avons");
			stopWordSet.add("avez");
			stopWordSet.add("ont");
			stopWordSet.add("aurai");
			stopWordSet.add("auras");
			stopWordSet.add("aura");
			stopWordSet.add("aurons");
			stopWordSet.add("aurez");
			stopWordSet.add("auront");
			stopWordSet.add("aurais");
			stopWordSet.add("aurait");
			stopWordSet.add("aurions");
			stopWordSet.add("auriez");
			stopWordSet.add("auraient");
			stopWordSet.add("avais");
			stopWordSet.add("avait");
			stopWordSet.add("avions");
			stopWordSet.add("aviez");
			stopWordSet.add("avaient");
			stopWordSet.add("eut");
			stopWordSet.add("eûmes");
			stopWordSet.add("eûtes");
			stopWordSet.add("eurent");
			stopWordSet.add("aie");
			stopWordSet.add("aies");
			stopWordSet.add("ait");
			stopWordSet.add("ayons");
			stopWordSet.add("ayez");
			stopWordSet.add("aient");
			stopWordSet.add("eusse");
			stopWordSet.add("eusses");
			stopWordSet.add("eût");
			stopWordSet.add("eussions");
			stopWordSet.add("eussiez");
			stopWordSet.add("eussent");
			stopWordSet.add("\r");
			stopWordSet.add(".");
			stopWordSet.add(",");
			stopWordSet.add("-");
			stopWordSet.add("’");
			stopWordSet.add("'");
			stopWordSet.add(";");
			stopWordSet.add("«");
			stopWordSet.add("»");
			stopWordSet.add("\"");
			stopWordSet.add(":");
			stopWordSet.add("(");
			stopWordSet.add(")");
			TOKENIZER_FACTORY = new StopTokenizerFactory(TOKENIZER_FACTORY, stopWordSet);
			TOKENIZER_FACTORY = new WhitespaceNormTokenizerFactory(TOKENIZER_FACTORY);
			Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
			tokenizer.tokenize(tokenList, whiteList);
		});
		// tokenizer.tokenize(tokenList, whiteList);

		List<Word> wordList = new ArrayList<Word>();
		tokenList.forEach(token -> {
			Word word = wordList.stream().filter(c -> c.getWord().equals(token)).findFirst().orElse(null); // .get()
																											// ||
																											// Optional<Word>
			int index = -1;
			if (word != null) {
				index = wordList.indexOf(word);
				if (index != -1) {
					word.setCount(word.getCount() + 1);
					wordList.set(index, word);
				}
			} else {
				wordList.add(new Word(token, 1));
			}
		});

		List<Word> result = wordList.stream().sorted((word1, word2) -> word2.getCount().compareTo(word1.getCount()))
				.collect(Collectors.toList());

		result.stream().map(token -> {
			System.out.print(token.getWord());
			System.out.println(" " + token.getCount());
			return null;
		}).collect(Collectors.toList());

		// TF = Nombre d’occurrence du terme analysé / Nombre de termes total
		// IDF = log(Nombre total de documents / Nombre de documents contenant
		// le terme analysé)

		// System.out.println();
		// System.out.print("Article tags:");
		// for(int i = 0; i < 5; i++){
		// System.out.print(" "+result.get(i).getWord());
		// }
		System.out.println();
		System.out.println(tokenList.size() + " TOKENS");
		System.out.println(whiteList.size() + " WHITESPACES");

	}
}
