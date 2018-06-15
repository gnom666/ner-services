package com.taiger.nlp.ner.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Sentence {

	private String original;
	private List<Word> s;
	private List<Location> locations;
	private Set<Period> periods;
	
	public Sentence () {
		s = new ArrayList<>();
		original = "";
		locations = new LinkedList<>();
		periods = new LinkedHashSet<>();
	}
	
	public void addWord (Word word) {
		Assert.notNull(word, "word should not be null");
		s.add(word);
	}
	
	public void addLocation (Location location) {
		if (location == null) return;
		if (!locationExists(location)) this.locations.add(location);
	}
	
	private boolean locationExists (Location location) {
		Assert.notNull(location, "location shouldn't be null");
		boolean found = false;
		Iterator<Location> it = this.locations.iterator();
		while (it.hasNext() && !found) {
			if (it.next().equal(location)) found = true;
		}
		return found;
	}
	
	public void addPeriod (Period period) {
		if (period == null) return;
		if (!periodExists(period)) this.periods.add(period);
	}
	
	private boolean periodExists (Period period) {
		Assert.notNull(period, "period shouldn't be null");
		boolean found = false;
		Iterator<Period> it = this.periods.iterator();
		while (it.hasNext() && !found) {
			if (it.next().equals(period)) found = true;
		}
		return found;
	}
}
