package com.pickasso.analysis;

public class Word {

	private String word;
	private Integer count;
	private Integer numberDocContain;
	private Double tfIdf;

	public Word(String word, int count) {
		super();
		this.word = word;
		this.count = count;
	}

	public Word(String word, int count, Integer numberDocCountain) {
		super();
		this.word = word;
		this.count = count;
		this.numberDocContain = numberDocCountain;
	}

	public Word() {
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return this.word;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Integer getCount() {
		return this.count;
	}

	public Integer getNumberDocContain() {
		return numberDocContain;
	}

	public void setNumberDocContain(Integer numberDocContain) {
		this.numberDocContain = numberDocContain;
	}

	public Double getTfIdf() {
		return tfIdf;
	}

	public void setTfIdf(Double tfIdf) {
		this.tfIdf = tfIdf;
	}

	@Override
	public String toString() {
		return "Word [word=" + word + ", count=" + count + ", numberDocContain=" + numberDocContain + ", tfIdf=" + tfIdf
				+ "]";
	}
}
