package com.taiger.nlp.ner.opennlpner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.tree.Tree;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import com.taiger.nlp.ner.model.Constants;
import com.taiger.nlp.ner.model.Period;
import com.taiger.nlp.ner.model.Sentence;
import com.taiger.nlp.ner.model.Word;

import lombok.extern.log4j.Log4j2;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

@Log4j2
public class DateNER implements NER {
	
	private NameFinderME nameFinder;
	
	public DateNER () {
		initialize  ();
	}

	public DateNER initialize () {
		try {
			TokenNameFinderModel dnfModel = new TokenNameFinderModel(DateNER.class.getClassLoader().getResourceAsStream(Constants.NER_EN_DATE));
			this.nameFinder = new NameFinderME (dnfModel);
		} 	catch (IOException e) {
			log.error(e.getMessage());
		}
		
		return this;
	}

	@Override
	public Sentence annotate(Sentence sentence) {
		Assert.notNull(sentence, "sentence shouldn't be null");
		Assert.notNull(sentence.getS(), "sentence content shouldn't be null");
		String[] tokens = new String[sentence.getS().size()];
		for (int i = 0; i < sentence.getS().size(); i++) {
			tokens[i] = sentence.getS().get(i).getW();
		}

		Span spans[] = nameFinder.find(tokens);
		for (Span span : spans) {
			sentence.getS().get(span.getStart()).setNerTag(Constants.B + Constants.DATE);
			sentence.getS().get(span.getStart()).setNerProb(nameFinder.probs()[span.getStart()]);
			sentence.getS().get(span.getStart()).setPeriods(findPeriods(getYear(tokens, span)));
			for (int i = span.getStart() + 1; i < span.getEnd(); i++) {
				sentence.getS().get(i).setNerTag(Constants.I + Constants.DATE);
				sentence.getS().get(i).setNerProb(nameFinder.probs()[i]);
			}
		}
		
		extractDates(sentence);
		
		correctPeriodsPositions (sentence);
		
		return sentence;
	}
	
	private void correctPeriodsPositions(Sentence sentence) {
		int size = sentence.getS().size();
		
		for (int i = 0; i < size; i++) {
			Word w = sentence.getS().get(i);
			if (w.getNerTag().contains(Constants.I + Constants.DATE) && !w.getPeriods().isEmpty()) {
				Set<Period> periods = w.getPeriods();
				boolean found = false;
				for (int j = i; j >= 0 && !found; j--) {
					if (sentence.getS().get(j).getNerTag().contains(Constants.B + Constants.DATE)) {
						sentence.getS().get(j).getPeriods().addAll(periods);
						w.setPeriods(new HashSet<>());
					}
				}
			}
		}
	}

	private int getYear (String[] tokens, Span span) {
		Assert.notNull(span, "span shouldn't be null");
		Assert.notNull(tokens, "tokens shouldn't be null");
		Assert.isTrue(tokens.length > span.getEnd(), "index out of range");
		Integer year = null;
		
		for (int i = span.getEnd(); i >= span.getStart(); i--) {
			year = str2i(tokens[i]);
			if (year != null) return year;
		}
		
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	private Integer str2i (String str) {
		Assert.hasText(str, "str should have text");
		if (StringUtils.isNumeric(str) && str.matches("\\d+")) {
			return Integer.parseInt(str);
		}
		return null;
	}
	
	private Set<Period> findPeriods (int year) {
		Set<Period> periods = new HashSet<>();
		
		if (year <= -800) {
			periods.add(Period.PREHISTORY);
		}
		if (year > -800 && year < 800) {
			periods.add(Period.ANCIENT_AGE);
		}
		if (year >= 400 && year < 1000) {
			periods.add(Period.MIDDLE_AGE);
		}
		if (year >= 1000 && year < 1492) {
			periods.add(Period.LATE_MIDDLE_AGE);
		}
		if (year >= 1492 && year < 1600) {
			periods.add(Period.MODERN_HISTORY_XVI);
		}
		if (year >= 1600 && year < 1776) {
			periods.add(Period.MODERN_HISTORY_XVII);
		}
		if (year >= 1776 && year < 1900) {
			periods.add(Period.CONTEMPORARY_HISTORY_XIX);
		}
		if (year >= 1900 && year < 2000) {
			periods.add(Period.CONTEMPORARY_HISTORY_XX);
		}
		if (year >= 2000) {
			periods.add(Period.CONTEMPORARY_HISTORY_XXI);
		}
		
		return periods;
	}
	
	private Sentence extractDates (Sentence sentence) {
		Assert.notNull(sentence, "sentence shouldn't be null");
		
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(sentence.getOriginal());
		for(DateGroup group : groups) {
			//List<Date> dates = group.getDates();
			//int line = group.getLine();
			//String syntaxTree = group.getSyntaxTree().toStringTree();
			//Map<String, List<ParseLocation>> parseMap = group.getParseLocations();
			//boolean isRecurreing = group.isRecurring();
			//Date recursUntil = group.getRecursUntil();
			int column = group.getPosition();
			String matchingValue = group.getText();
			
			int offset = column - 1;
			int end = offset + matchingValue.length();
			
			List<Period> periods = extractPeriods (group);
			for (Word w : sentence.getS()) {
				if (w.getOffset() == offset) {
					w.setNerTag(Constants.B + Constants.DATE);
					periods.forEach(w.getPeriods()::add);
				}	else if (w.getOffset() > offset && w.getOffset() < end) {
					w.setNerTag(Constants.I + Constants.DATE);
				}
			}
		}
		
		return sentence;
	}

	private List<Period> extractPeriods(DateGroup group) {
		List<String> years = new ArrayList<>();
		List<Period> periods = new ArrayList<>();
		
		extractYears (group.getSyntaxTree(), years);
		
		years.forEach (y -> {
			Integer inty = str2i(y);
			if (inty != null) {
				periods.addAll(findPeriods(inty));
			}
		});
		
		return periods;
	}

	private void extractYears(Tree tree, List<String> years) {
		
		if (tree.getText().equals("EXPLICIT_TIME")) return;

		if (tree.getText().equals("YEAR_OF") && tree.getChildCount() > 0) {
			years.add(tree.getChild(0).getText());
		}
		
		if (tree.getChildCount() > 0) {
			for (int i = 0; i < tree.getChildCount(); i++) {
				extractYears(tree.getChild(i), years);
			}
		}
		
	}

}
