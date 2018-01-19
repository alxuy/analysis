package com.pickasso.analysis;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceModel;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;

import com.aliasi.util.Files;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Use SentenceModel to find sentence boundaries in text */
public class WordCount {

    static final TokenizerFactory TOKENIZER_FACTORY = new RegExTokenizerFactory("(-|'|\\d|\\p{L})+|\\S");

//    public class CountWord {
//    	private String word;
//    	private int count;
//    	
//    	public void setWord(String word){
//    		this.word = word;
//    	}
//    	
//    	public String getWord(){
//    		return this.word;
//    	}
//    	
//    	public void setCount(int count){
//    		this.count=count;
//    	}
//    	
//    	public int getCount(){
//    		return this.count;
//    	}
//    }
    
    public static void main(String[] args) throws IOException {
	File file = new File("D:/PickAsso/lingpipe-4.1.2-website/demos/data/carenews.txt");
	String text = Files.readFromFile(file,"UTF-8");
	System.out.println("INPUT TEXT: ");
	System.out.println(text);

	List<String> tokenList = new ArrayList<String>();
	List<String> whiteList = new ArrayList<String>();
	Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(),0,text.length());
	tokenizer.tokenize(tokenList,whiteList);

	System.out.println(tokenList.size() + " TOKENS");
	System.out.println(whiteList.size() + " WHITESPACES");

	String[] tokens = new String[tokenList.size()];
	String[] whites = new String[whiteList.size()];
	tokenList.toArray(tokens);
	whiteList.toArray(whites);
	
	List<String> tokenUnic = new ArrayList<String>();
	List<Integer> tokenCount = new ArrayList<Integer>();
	tokenList.forEach(token -> {
		int index = tokenUnic.indexOf(token);
		if(index != -1){
			tokenCount.set(index, tokenCount.get(index) + 1); 
		} else {
			tokenUnic.add(token);
			tokenCount.add(1);
		}
	});
	
	tokenUnic
		.stream()
		.map(token ->{
			int index = tokenUnic.indexOf(token);
			System.out.print(tokenUnic.get(index));
			System.out.println(" " + tokenCount.get(index));
			return null;
		}).collect(Collectors.toList());
    }
}
