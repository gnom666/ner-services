package com.taiger.nlp.ner.postagger;

import com.taiger.nlp.ner.model.Sentence;

public interface POSTagger {

	Sentence annotate (Sentence sentence);
	
}
