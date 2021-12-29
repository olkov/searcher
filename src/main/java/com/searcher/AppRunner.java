package com.searcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.searcher.dto.SearchMatch;
import com.searcher.service.FileClient;
import com.searcher.service.FileReader;
import com.searcher.service.SearchMatchersAggregator;
import com.searcher.service.StringMatcher;
import com.searcher.task.PhraseProcessingTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppRunner {

	private static final Logger log = LogManager.getLogger(AppRunner.class);

	private static final String FILE_PATH = "http://norvig.com/big.txt";
	private static final Set<String> SEARCH_KEYS = Set.of(("James,John,Robert,Michael,William,David,Richard,Charles," +
			"Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony," +
			"Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey,Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory," +
			"Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger").split(","));

	private final FileClient fileClient = new FileClient();
	private final FileReader fileReader = new FileReader();
	private final StringMatcher stringMatcher = new StringMatcher();
	private final SearchMatchersAggregator searchMatchersAggregator = new SearchMatchersAggregator();

	public void run() throws InterruptedException, ExecutionException {
		File file = fileClient.download(FILE_PATH);
		List<List<String>> linesBatches = fileReader.getLinesBatches(file);
		CountDownLatch countDownLatch = new CountDownLatch(linesBatches.size());
		ExecutorService executor = buildExecutor(linesBatches.size());
		List<Future<List<Map<String, List<SearchMatch>>>>> processedBatches = new ArrayList<>();
		log.info("Starting processing...");
		for (int index = 0; index < linesBatches.size(); index++) {
			PhraseProcessingTask phraseProcessingTask = new PhraseProcessingTask(countDownLatch, stringMatcher,
					linesBatches.get(index), SEARCH_KEYS, index);
			processedBatches.add(executor.submit(phraseProcessingTask));
		}
		log.info("Waiting for threads completion...");
		countDownLatch.await();
		executor.shutdown();
		log.info("File is successfully processed. Starting results aggregation...");
		List<Map<String, List<SearchMatch>>> searchMatches = new ArrayList<>(processedBatches.size());
		for (Future<List<Map<String, List<SearchMatch>>>> future : processedBatches) {
			searchMatches.add(searchMatchersAggregator.aggregate(future.get()));
		}
		log.info("Aggregated results: ");
		Map<String, List<SearchMatch>> aggregatedSearchMatches = searchMatchersAggregator.aggregate(searchMatches);
		for (Map.Entry<String, List<SearchMatch>> entry : aggregatedSearchMatches.entrySet()) {
			log.info("Matches {} for '{}' --> {}", entry.getValue().size(), entry.getKey(), entry.getValue());
		}
	}

	private ExecutorService buildExecutor(int maxPoolSize) {
		return new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
				maxPoolSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
	}
}
