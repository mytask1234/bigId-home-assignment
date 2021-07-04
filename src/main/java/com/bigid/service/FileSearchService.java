package com.bigid.service;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.bigid.model.WordToLocationsMap;

public interface FileSearchService {

	WordToLocationsMap findWordsLocationsInTextFile(File inputFile, Set<String> words) throws IOException, InterruptedException, ExecutionException;
}
