package com.bigid.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bigid.service.FileSearchService;
import com.bigid.service.FileSearchServiceImpl;

@Configuration
public class AppConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

	@Value("${threads.in.pool}")
	private int numOfThreads;

	@Value("${lines.per.part}")
	private int linesPerPart;
	
	@Value("${case.sensitive}")
	private boolean caseSensitive;

	private ExecutorService executorService;

	@PostConstruct
	private void postConstruct() {

		LOGGER.info("numOfThreads={}", numOfThreads);
		LOGGER.info("linesPerPart={}", linesPerPart);
		LOGGER.info("caseSensitive={}", caseSensitive);
	}

	@PreDestroy
	private void preDestroy() {

		LOGGER.info("executorService.shutdown()");

		if (executorService != null) {

			executorService.shutdown();
		}
	}

	@Bean
	public FileSearchService fileSearchService() {

		executorService = Executors.newFixedThreadPool(numOfThreads);

		return new FileSearchServiceImpl(linesPerPart, caseSensitive, executorService);
	}
}
