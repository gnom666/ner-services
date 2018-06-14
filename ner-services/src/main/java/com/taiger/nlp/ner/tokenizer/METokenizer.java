package com.taiger.nlp.ner.tokenizer;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.taiger.nlp.ner.model.Constants;
import com.taiger.nlp.ner.model.Sentence;
import com.taiger.nlp.ner.model.Word;

import lombok.extern.log4j.Log4j2;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

@Log4j2
@Component
public class METokenizer implements Tokenizer {
	
	private TokenizerME tokenizer;
	
	public METokenizer () {
		initialize ();
	}

	public METokenizer initialize () {
		try {
			TokenizerModel model = new TokenizerModel(METokenizer.class.getClassLoader().getResourceAsStream(Constants.TOKEN_EN));
			this.tokenizer = new TokenizerME(model);
		} 	catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return this;
	}

	@Override
	public Sentence tokenize(String sentence) {
		Assert.hasText(sentence, "sentence should has text");
		
		Sentence result = new Sentence();
		result.setOriginal(sentence);
	
		String[] otokens = tokenizer.tokenize (sentence);
		double[] tokenProbs = tokenizer.getTokenProbabilities ();

		int min = 0;
		int max = sentence.length();
		for (int i = 0; i < otokens.length; i++) {
			int offset = sentence.substring(min, max).indexOf(otokens[i]) + min;
			min = otokens[i].length() + offset;
			Word word = new Word (otokens[i], Constants.O, tokenProbs[i], i, offset);
			result.addWord (word);
		}
		
		return result;
	}

}
