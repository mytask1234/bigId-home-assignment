package com.bigid;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bigid.model.WordToLocationsMap;
import com.bigid.service.FileSearchService;

@SpringBootApplication
public class FileSearchApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSearchApplication.class);

	private static final String INPUT_DIR = "input/";

	@Value("${input.file.name}")
	private String inputFile;

	@Autowired
	private FileSearchService fileSearchService;

	public static void main(String[] args) {
		SpringApplication.run(FileSearchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		try {

			File flie = new File(INPUT_DIR + inputFile);

			Set<String> words = getWords();

			long start = System.currentTimeMillis();
			
			WordToLocationsMap wordToLocationsMap = fileSearchService.findWordsLocationsInTextFile(flie, words);

			long end = System.currentTimeMillis();
			
			LOGGER.info("wordToLocationsMap={}", wordToLocationsMap);
			
			NumberFormat formatter = new DecimalFormat("#0.00000");
			LOGGER.info("The search took {} seconds", formatter.format((end - start) / 1000d));

		} catch (Throwable t) {

			LOGGER.error(t.getMessage(), t);
		}

		System.exit(0);
	}

	private Set<String> getWords() {

		StringBuilder sb = new StringBuilder();

		sb.append("James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,");
		sb.append("George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey,");
		sb.append("Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,");
		sb.append("Henry,Carl,Arthur,Ryan,Roger");

		Set<String> words = Stream.of(sb.toString().split(",")).collect(Collectors.toSet());

		LOGGER.info("words.size()={}", words.size());
		LOGGER.info("words={}", words);

		return words;
	}
}
