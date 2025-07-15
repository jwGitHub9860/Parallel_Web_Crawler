package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveTask;

final class CrawlRecursiveTask extends RecursiveTask {

  private final String url;
  private final Instant deadline;
  private final Clock clock;
  private final PageParserFactory parserFactory;
  private final int maxDepth;
  private final ConcurrentHashMap<String, Integer> countUrls;
  private final ConcurrentSkipListSet<String> accessedUrls;
  private final List<Pattern> ignoredUrls;

  public CrawlRecursiveTask(
      String url,
      Instant deadline,
      int maxDepth,
      ConcurrentHashMap<String, Integer> countUrls,
      ConcurrentSkipListSet<String> accessedUrls,
      Clock clock,
      PageParserFactory parserFactory,
      List<Pattern> ignoredUrls) {
    this.url = url;
    this.deadline = deadline;
    this.clock = clock;
    this.parserFactory = parserFactory;
    this.maxDepth = maxDepth;
    this.countUrls = countUrls;
    this.accessedUrls = accessedUrls;
    this.ignoredUrls = ignoredUrls;
  }

  @Override
  protected Boolean compute() {
    // Checks if "maxDepth" Equals "0" OR if Current Time is AFTER "deadline"
    if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
      // Returns "false" if there's "0" URLs To Compare OR if ALL URLs Have ALREADY BEEN COMPARED
      return false;
    }

    // Iterates Through "ignoredUrls"
    for (Pattern pattern : ignoredUrls) {
      // Checks if "url" Matches "pattern"
      if (pattern.matcher(url).matches()) {
        // Returns "false" if Current URL Has ALREADY BEEN Accounted For to PREVENT DUPLICATE URLs
        return false;
      }
    }

    // Allow Only ONE Thread at a time To Enter Code Block
    synchronized (this) {
      // Checks if "accessedUrls" Contains "url"
      if (accessedUrls.contains(url)) {
        return true;
      }

      // Checks if "url" was NOT Added to "accessedUrls"
      if (!accessedUrls.add(url)) {
        return false;
      }
    }

    // Obtains "url" & Parses it into "result"
    PageParser.Result result = parserFactory.get(url).parse();

    // Iterates Through "result.getWordCounts().entrySet()"
    for (ConcurrentHashMap.Entry<String, Integer> e : result.getWordCounts().entrySet()) { // MUST USE "ConcurrentHashMap" NOT "Map" or Error Will Occur
      // Calls "compute()" Method for "countUrls" Concurrent Hash Map
      countUrls.compute(
              // Obtains KEY within "countUrls" Map
              e.getKey(),
              // Returns Result of Boolean To Obtain VALUE When Provided With "key" & "value"
              (key, value) -> (value == null) ? e.getValue() : e.getValue() + value);
    }

    // Holds Instances when "compute()" is Called [Ex. computeInstance = new compute(link, deadline, maxDepth - 1, countUrls, accessedUrls);]
    List<CrawlRecursiveTask> subTasks = new ArrayList<>();

    // Iterates Through "result.getLinks()"
    for (String link : result.getLinks()) {
      // Adds "CrawlRecursiveTask" Instance to "subTasks" Array List
      subTasks.add(new CrawlRecursiveTask(link, deadline, maxDepth - 1, countUrls, accessedUrls, clock, parserFactory, ignoredUrls));
    }

    // Runs ALL METHODS In "CrawlRecursiveTask" Class Using ".invokeAll()"
    invokeAll(subTasks);

    // Returns "true" to Indicate that ALL URLs have been Accounted For & Number of Access Times for each URL Has Been Found
    return true;
  }
}
