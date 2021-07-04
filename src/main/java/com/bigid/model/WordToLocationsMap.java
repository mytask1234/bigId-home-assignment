package com.bigid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordToLocationsMap {

	private Map<String, List<WordLocation>> wordToItsLocationsMap = new HashMap<String, List<WordLocation>>();

	public WordToLocationsMap() {

	}

	public void addWordLocation(String word, WordLocation wordLocation) {

		List<WordLocation> wordOffsets = wordToItsLocationsMap.get(word);

		if (wordOffsets == null) {

			wordOffsets = new ArrayList<WordLocation>();

			wordToItsLocationsMap.put(word, wordOffsets);
		}

		wordOffsets.add(wordLocation);
	}

	public void merge(WordToLocationsMap mergeTarget) {

		wordToItsLocationsMap.forEach( (k,v) -> {

			List<WordLocation> wordOffsets = mergeTarget.wordToItsLocationsMap.get(k);
			if (wordOffsets == null) {

				mergeTarget.wordToItsLocationsMap.put(k, v);

			} else {

				wordOffsets.addAll(v);
			}
		});
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		wordToItsLocationsMap.forEach( (k,v) -> {

			builder.append(System.lineSeparator()).append(k).append(" --> ").append(v);
		});

		return builder.toString();
	}
}
