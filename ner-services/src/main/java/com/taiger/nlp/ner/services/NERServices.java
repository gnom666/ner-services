package com.taiger.nlp.ner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taiger.nlp.ner.model.Sentence;
import com.taiger.nlp.ner.opennlpner.DateNER;
import com.taiger.nlp.ner.opennlpner.LocationNER;
import com.taiger.nlp.ner.postagger.METagger;
import com.taiger.nlp.ner.tokenizer.METokenizer;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/ner")
@Component
public class NERServices {
	
	@Autowired
	private DateNER dateNER;
	
	@Autowired
	private LocationNER locationNER;
	
	@Autowired
	private METokenizer tokenizer;
	
	@Autowired
	private METagger tagger;

	@RequestMapping(value="/annotate", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public Sentence nerAnnotations(@RequestParam(value="sentence", defaultValue="") String sentence) {
		Assert.notNull(sentence, "sentence shouldn't be null");
		Sentence result = new Sentence();
		if (sentence.trim().isEmpty()) return result;
		
		result = tokenizer.tokenize(sentence.trim());
		result = tagger.annotate(result);
		result = dateNER.annotate(result);
		result = locationNER.annotate(result);
		
		log.info(result.toString());
		
		return result;
    }
	
}
