package com.taiger.nlp.ner.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Word {
	
	private String w;
	private String posTag;
	private String nerTag;
	private Double tokenProb;
	private Double nerProb;
	private int position;
	private int offset;
	
	public Word (String w, String nerTag, Double tokenProb, int position, int offset) {
		this.w = w;
		this.nerTag = nerTag;
		this.tokenProb = tokenProb;
		this.position = position;
		this.offset = offset;
		
		this.posTag = "";
		this.nerProb = 0.0;
	}
	
	public Word () {
		this.w = "";
		this.nerTag = "";
		this.tokenProb = 0.0;
		this.position = -1;
		this.posTag = "";
		this.nerProb = 0.0;
		this.offset = 0;
	}
}
