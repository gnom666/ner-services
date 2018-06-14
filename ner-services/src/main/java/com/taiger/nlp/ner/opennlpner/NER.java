package com.taiger.nlp.ner.opennlpner;

import com.taiger.nlp.ner.model.Sentence;

public interface NER {
	
	Sentence annotate (Sentence sentence);
	
}
