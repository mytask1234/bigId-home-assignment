package com.bigid.callable;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigid.model.WordLocation;
import com.bigid.model.WordToLocationsMap;

public class SearchWordsInTextCallable implements Callable<WordToLocationsMap> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchWordsInTextCallable.class);

	private final int firstLineOffset;
	private final int firstCharOffset;
	private final String textSrc;
	private final Pattern pattern;
	private final boolean caseSensitive;

	public SearchWordsInTextCallable(int firstLineOffset, int firstCharOffset, String textSrc, Pattern pattern, 
																							boolean caseSensitive) {
		super();
		this.firstLineOffset = firstLineOffset;
		this.firstCharOffset = firstCharOffset;
		this.textSrc = textSrc;
		this.pattern = pattern;
		this.caseSensitive = caseSensitive;
	}

	@Override
	public WordToLocationsMap call() {

		WordToLocationsMap wordToWordOffsets = new WordToLocationsMap();

		Matcher m = pattern.matcher(textSrc);

		int lineSeparatorCount = 0;

		while (m.find()) {

			String group = m.group();

			if (group.equals("\n") || group.equals("\r\n")) {

				++lineSeparatorCount;

			} else {

				int left  = m.start() - 1;
				int right = m.start() + group.length();

				boolean isLeftCharWhitespace = true;
				boolean isRightCharWhitespace = true;

				if (left >= 0) {

					isLeftCharWhitespace = Character.isWhitespace(textSrc.charAt(left));
				}

				if (right < textSrc.length()) {

					isRightCharWhitespace = Character.isWhitespace(textSrc.charAt(right));
				}

				if (isLeftCharWhitespace && isRightCharWhitespace) { // if real word

					if (LOGGER.isDebugEnabled()) {

						LOGGER.debug("group={}, m.start()={}", group, m.start());
					}

					WordLocation wordOffset = new WordLocation(firstLineOffset + lineSeparatorCount, firstCharOffset + m.start());

					if (!caseSensitive) {
						
						group = group.toLowerCase();
					}
					
					wordToWordOffsets.addWordLocation(group, wordOffset);
				}
			}
		}

		return wordToWordOffsets;
	}
}
