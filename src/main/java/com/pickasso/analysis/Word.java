package com.pickasso.analysis;

public class Word {
	
	private String word;
	private Integer count;
	
	public Word(String word, int count) {
		super();
		this.word = word;
		this.count = count;
	}
	
	public Word() {
	}
	
	public void setWord(String word){
		this.word = word;
	}
	
	public String getWord(){
		return this.word;
	}
	
	public void setCount(int count){
		this.count=count;
	}
	
	public Integer getCount(){
		return this.count;
	}
}
