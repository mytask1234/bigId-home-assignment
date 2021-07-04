package com.bigid.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigid.callable.SearchWordsInTextCallable;
import com.bigid.exception.CustomException;
import com.bigid.model.WordToLocationsMap;

public class FileSearchServiceImpl implements FileSearchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSearchServiceImpl.class);

	private final int linesPerPart;
	private final boolean caseSensitive;
	private final ExecutorService executorService;
	
	private static final int CHARS_BUFFER_SIZE = 122995; // 122895

	public FileSearchServiceImpl(int linesPerPart, boolean caseSensitive, ExecutorService executorService) {
		super();
		this.linesPerPart = linesPerPart;
		this.caseSensitive = caseSensitive;
		this.executorService = executorService;
	}

	@Override
	public WordToLocationsMap findWordsLocationsInTextFile(File inputFile, Set<String> words) {

		List<Future<WordToLocationsMap>> futures = readFileAndSubmitLinesForSearchTasks(inputFile, words);

		return aggregateSearchResults(futures);
	}

	private List<Future<WordToLocationsMap>> readFileAndSubmitLinesForSearchTasks(File inputFile, Set<String> words) {

		Pattern pattern = getPattern(words);

		List<Future<WordToLocationsMap>> futures = new ArrayList<Future<WordToLocationsMap>>();

		int firstLineOffset = 0;		
		int firstCharOffset = 0;

		int i = 0;
		
		StringBuilder sbTextPart = new StringBuilder(CHARS_BUFFER_SIZE);

		Path path = inputFile.toPath();
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {

			String line;

			while ((line = reader.readLine()) != null) {

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug("line={}", line);
				}

				sbTextPart.append(line).append(System.lineSeparator());

				++i;

				if (i == linesPerPart) {

					SearchWordsInTextCallable searchTask = new SearchWordsInTextCallable(firstLineOffset, firstCharOffset, sbTextPart.toString(), pattern, caseSensitive);

					futures.add(executorService.submit(searchTask));

					firstLineOffset += i;

					i = 0;

					firstCharOffset += sbTextPart.length();
					
					sbTextPart.setLength(0);
				}
			}
			
		} catch (IOException ioe) {

			throw new CustomException("fail to read file: " + inputFile.getName(), ioe);
		}

		if (i > 0 && i < linesPerPart) { // submit last part, if need

			SearchWordsInTextCallable searchTask = new SearchWordsInTextCallable(firstLineOffset, firstCharOffset, sbTextPart.toString(), pattern, caseSensitive);

			futures.add(executorService.submit(searchTask));
		}
		
		return futures;
	}

	private WordToLocationsMap aggregateSearchResults(List<Future<WordToLocationsMap>> futures) {

		WordToLocationsMap result = new WordToLocationsMap();

		if (futures.size() == 0) {

			return result;
		}

		try {

			if (futures.size() == 1) {

				return futures.get(0).get();
			}

			for(Future<WordToLocationsMap> future : futures) {

				future.get().merge(result);
			}

		} catch (InterruptedException | ExecutionException e) {

			throw new CustomException("fail to aggregate the search results. futures.size()=" + futures.size(), e);
		}

		return result;
	}

	private Pattern getPattern(Set<String> words) {

		StringBuilder sb = new StringBuilder("(\r?\n)");

		for (String word : words) {

			sb.append("|(").append(word).append(")");
		}

		if (caseSensitive) {

			return Pattern.compile(sb.toString());

		} else {

			return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
		}
	}
}
