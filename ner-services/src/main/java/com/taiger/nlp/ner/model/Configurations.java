package com.taiger.nlp.ner.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.taiger.nlp.ner.opennlpner.DateNER;
import com.taiger.nlp.ner.opennlpner.LocationNER;
import com.taiger.nlp.ner.opennlpner.NER;
import com.taiger.nlp.ner.postagger.METagger;
import com.taiger.nlp.ner.postagger.POSTagger;
import com.taiger.nlp.ner.tokenizer.METokenizer;
import com.taiger.nlp.ner.tokenizer.Tokenizer;

@Configuration
public class Configurations {

	@Bean
	public NER dateNER () { return new DateNER();}
	
	@Bean
	public NER locationNER () { return new LocationNER();}
	
	@Bean
	public POSTagger tagger () { return new METagger();}
	
	@Bean
	public Tokenizer tokenizer () { return new METokenizer();}
	
}
