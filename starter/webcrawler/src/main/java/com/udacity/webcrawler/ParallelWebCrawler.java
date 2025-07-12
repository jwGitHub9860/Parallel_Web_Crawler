package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A concrete implementation of {@link WebCrawler} that runs multiple threads on a
 * {@link ForkJoinPool} to fetch and process multiple web pages in parallel.
 */
final class ParallelWebCrawler implements WebCrawler {
  private final Clock clock;
  private final Duration timeout;
  private final List<Pattern> ignoredUrls;
  private final PageParserFactory parserFactory;
  private final int maxDepth;
  private final int popularWordCount;
  private final ForkJoinPool pool;

  @Inject
  ParallelWebCrawler(
          Clock clock,
          @IgnoredUrls List<Pattern> ignoredUrls, // @IgnoredUrls - CUSTOM ANNOTATION that Ignores Particular Tests Or Groups To Skip Build Failure
          PageParserFactory parserFactory,
          @MaxDepth int maxDepth, // @MaxDepth - Specifies Maximum Depth which Object Should Be Serialized
          @Timeout Duration timeout,
          @PopularWordCount int popularWordCount,
          @TargetParallelism int threadCount) {
    this.clock = clock;
    this.ignoredUrls = ignoredUrls;
    this.parserFactory = parserFactory;
    this.maxDepth = maxDepth;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.pool = new ForkJoinPool(Math.min(threadCount, getMaxParallelism()));
  }

  @Override
  public CrawlResult crawl(List<String> startingUrls) {
    // Obtains Current Time with "timeout"
    Instant deadline = clock.instant().plus(timeout);

    // Creates "ConcurrentHashMap" to hold Threads of URLs & Amount of Times they were Accessed
    ConcurrentHashMap<String, Integer> countUrls = new ConcurrentHashMap<>(); // did NOT Use "HashMap" because it's NOT Thread-Safe

    // Creates "ConcurrentSkipListSet" to hold Threads of Each URL Accessed
    ConcurrentSkipListSet<String> accessedUrls = new ConcurrentSkipListSet<>(); // Sorted Set that Safely Accesses & Modifys Multiple Threads AT THE SAME TIME

    // Fills "countUrls" & "accessedUrls"
    for (String url : startingUrls) {
      // Calls "CrawlRecursiveTask" Constructor to create "CrawlRecursiveTask" instance Using ".invoke()"
      pool.invoke(new CrawlRecursiveTask(url, deadline, maxDepth, countUrls, accessedUrls, clock, parserFactory, ignoredUrls));
    }

    // Checks if "countUrls" is EMPTY
    if (countUrls.isEmpty()) {
      return new CrawlResult.Builder()
              // Sets & Sorts URLs (words) & Amount of Times they were Accessed
              .setWordCounts(countUrls)
              // Sets & Sorts Amount of Times URLs were Accessed
              .setUrlsVisited(accessedUrls.size())
              // Returns Final "CrawlResult" Instance AS Last Step in Creating "CrawlResult" Instance
              .build();
    }

    return new CrawlResult.Builder()
            // Sets & Sorts URLs (words) & Amount of Times they were Accessed
            .setWordCounts(WordCounts.sort(countUrls, popularWordCount))
            // Sets & Sorts Amount of Times URLs were Accessed
            .setUrlsVisited(accessedUrls.size())
            // Returns Final "CrawlResult" Instance AS Last Step in Creating "CrawlResult" Instance
            .build();
  }

  @Override
  public int getMaxParallelism() {
    return Runtime.getRuntime().availableProcessors();
  }
}
