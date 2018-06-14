package com.taiger.nlp.ner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taiger.nlp.ner.model.Sentence;
import com.taiger.nlp.ner.tokenizer.METokenizer;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/tokenizer")
@Component
public class TokenizerServices {

	@Autowired
	private METokenizer tokenizer;

	@RequestMapping(value="/annotate", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public Sentence tokenization(@RequestParam(value="sentence", defaultValue="") String sentence) {
		Assert.notNull(sentence, "sentence shouldn't be null");
		Sentence result = new Sentence();
		if (sentence.trim().isEmpty()) return result;
		
		result = tokenizer.tokenize(sentence.trim());
		
		log.info(result.toString());
		
		return result;
    }
	
}
