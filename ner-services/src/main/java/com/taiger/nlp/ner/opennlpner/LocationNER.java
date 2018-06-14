package com.taiger.nlp.ner.opennlpner;

import java.io.IOException;
import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.taiger.nlp.ner.model.Constants;
import com.taiger.nlp.ner.model.Location;
import com.taiger.nlp.ner.model.Sentence;
import com.taiger.nlp.ner.model.Word;
import com.taiger.nlp.ner.repositories.LocationRepository;

import lombok.extern.log4j.Log4j2;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

@Log4j2
@Component
public class LocationNER implements NER {

	private NameFinderME nameFinderEn;
	
	private NameFinderME nameFinderEs;
	
	@Autowired
	private LocationRepository locationRepo;
	
	public LocationNER () {
		initialize  ();
	}

	public LocationNER initialize () {
		try {
			TokenNameFinderModel lnfModel = new TokenNameFinderModel(LocationNER.class.getClassLoader().getResourceAsStream(Constants.NER_EN_LOCATION));
			this.nameFinderEn = new NameFinderME (lnfModel);
			lnfModel = new TokenNameFinderModel(LocationNER.class.getClassLoader().getResourceAsStream(Constants.NER_ES_LOCATION));
			this.nameFinderEs = new NameFinderME (lnfModel);
		} 	catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return this;
	}

	@Override
	public Sentence annotate(Sentence sentence) {
		Assert.notNull(sentence, "sentence shouldn't be null");
		Assert.notNull(sentence.getS(), "sentence content shouldn't be null");
		
		//* searching in english
		String[] tokens = new String[sentence.getS().size()];
		for (int i = 0; i < sentence.getS().size(); i++) {
			tokens[i] = sentence.getS().get(i).getW();
		}

		Span[] spansEn = nameFinderEn.find(tokens);
		for (Span span : spansEn) {
			sentence.getS().get(span.getStart()).setNerTag(Constants.B + Constants.LOCATION);
			sentence.getS().get(span.getStart()).setNerProb(nameFinderEn.probs()[span.getStart()]);
			sentence.getS().get(span.getStart()).setLocation(findLocation(Constants.formChunk(tokens, span)));
			
			for (int i = span.getStart() + 1; i < span.getEnd(); i++) {
				sentence.getS().get(i).setNerTag(Constants.I + Constants.LOCATION);
				sentence.getS().get(i).setNerProb(nameFinderEn.probs()[i]);
			}
		} //*/
		
		//* Searching in spanish
		int i = 0;
		for (Word w : sentence.getS()) {
			boolean condition = w.getNerTag().contains(Constants.B) || w.getNerTag().contains(Constants.I);
			tokens[i] = condition ? "" : w.getW();
			i++;
		}

		Span[] spansEs = nameFinderEs.find(tokens);
		for (Span span : spansEs) {
			sentence.getS().get(span.getStart()).setNerTag(Constants.B + Constants.LOCATION);
			sentence.getS().get(span.getStart()).setNerProb(nameFinderEs.probs()[span.getStart()]);
			sentence.getS().get(span.getStart()).setLocation(findLocation(Constants.formChunk(tokens, span)));
			
			for (i = span.getStart() + 1; i < span.getEnd(); i++) {
				sentence.getS().get(i).setNerTag(Constants.I + Constants.LOCATION);
				sentence.getS().get(i).setNerProb(nameFinderEs.probs()[i]);
			}
		} //*/
		
		return sentence;
	}
	
	public Location findLocation (String chunk) {
		Assert.notNull(chunk, "got null chunk");
		String searchChunk = chunk;
		
		Location loc = null;
		List<Location> locations = locationRepo.findTop100ByCountryNameIgnoreCaseContainingOrCityNameIgnoreCaseContainingOrRegionNameIgnoreCaseContaining(searchChunk, searchChunk, searchChunk);
		double min = Constants.MAXINT;
		for (int i = 0; i < locations.size(); i++) {
			double d = LevenshteinDistance.getDefaultInstance().apply(chunk, locations.get(i).getCountryName()) * bias (chunk, locations.get(i).getCountryName()) +
					LevenshteinDistance.getDefaultInstance().apply(chunk, locations.get(i).getRegionName()) * bias (chunk, locations.get(i).getRegionName()) +
					LevenshteinDistance.getDefaultInstance().apply(chunk, locations.get(i).getCityName()) * bias (chunk, locations.get(i).getCityName());
			
			if (LevenshteinDistance.getDefaultInstance().apply(chunk, locations.get(i).getCountryName()) == 0) return locations.get(i);

			if (d < min) {
				min = d;
				loc = locations.get(i);
			}
		}
		
		return loc;
	}

	private Double bias(String chunk, String name) {
		if (name.contains(chunk)) return 0.2;
		return 1.0;
	}

}
