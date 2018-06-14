package com.taiger.nlp.ner.tokenizer;

import com.taiger.nlp.ner.model.Sentence;

public interface Tokenizer {

	Sentence tokenize (String sentence);
	
}
