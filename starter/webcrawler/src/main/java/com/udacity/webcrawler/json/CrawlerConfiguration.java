package com.udacity.webcrawler.json;

// Setters & Add Methods MUST HAVE ANNOTATIONS Because Error Will Occur If Getters & Add Methods Do Not Have Annotations

// Defines "ObjectMapper"
import com.fasterxml.jackson.databind.ObjectMapper;

// Defines "@JsonProperty" Annotation
import com.fasterxml.jackson.annotation.JsonProperty;

// Defines "@JsonDeserialize" Annotation
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A data class that represents the configuration of a single web crawl.
 */
// Marks "CrawlerConfiguration read(Reader reader)" method as Deserialization Target to Deserialize JSON input & Complete Method & Tells Jackson that "CrawlerConfiguration" Uses Builder Pattern by Annotating "CrawlerConfiguration" class with "@JsonDeserialize" Annotation
@JsonDeserialize(builder = CrawlerConfiguration.Builder.class) // Jackson JSON-parsing annotation
public final class CrawlerConfiguration {

  private final List<String> startPages;
  private final List<Pattern> ignoredUrls;
  private final List<Pattern> ignoredWords;
  private final int parallelism;
  private final String implementationOverride;
  private final int maxDepth;
  private final Duration timeout;
  private final int popularWordCount;
  private final String profileOutputPath;
  private final String resultPath;

  private CrawlerConfiguration(
          List<String> startPages,
          List<Pattern> ignoredUrls,
          List<Pattern> ignoredWords,
          int parallelism,
          String implementationOverride,
          int maxDepth,
          Duration timeout,
          int popularWordCount,
          String profileOutputPath,
          String resultPath) {
    this.startPages = startPages;
    this.ignoredUrls = ignoredUrls;
    this.ignoredWords = ignoredWords;
    this.parallelism = parallelism;
    this.implementationOverride = implementationOverride;
    this.maxDepth = maxDepth;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.profileOutputPath = profileOutputPath;
    this.resultPath = resultPath;
  }

  /**
   * An unmodifiable {@link List} of URLs that define the starting points of the web crawl. It
   * should not contain any duplicate URLs.
   */
  public List<String> getStartPages() {
    return startPages;
  }

  /**
   * A {@link List} of regular expression {@link Pattern}s that determine which URLs, if any, the
   * web crawler should not follow.
   */
  public List<Pattern> getIgnoredUrls() {
    return ignoredUrls;
  }

  /**
   * A {@link List} of regular expression {@link Pattern}s that determine which words, if any, the
   * web crawler should not be counted toward the popular word count returned from the crawl.
   */
  public List<Pattern> getIgnoredWords() {
    return ignoredWords;
  }

  /**
   * The desired parallelism (e.g., number of CPU cores) that should be used for the web crawl. This
   * setting is optional.
   *
   * <p>If set to 1, the legacy sequential crawler will be used (unless
   * {@link #getImplementationOverride()} explicitly specifies otherwise). If set to a value less
   * than 1, the crawler will default to using the number of available CPU cores on the system.
   */
  public int getParallelism() {
    return parallelism;
  }

  /**
   * An explicit override for which web crawler implementation should be used for this crawl.
   *
   * <p>If set, it should be the fully qualified class name of a
   * {@link com.udacity.webcrawler.WebCrawler} implementation, for example
   * {@code "com.udacity.webcrawler.SequentialWebCrawler"}.
   *
   * <p>If unset or empty, the value of {@link #getParallelism()} setting is used to determine which
   * crawler implementation to use.
   */
  public String getImplementationOverride() {
    return implementationOverride;
  }

  /**
   * The maximum depth allowed for the crawl.
   *
   * <p>The "depth" of a crawl is the number of links the crawler has followed, starting from the
   * starting URLs (see {@link #getStartPages()}).
   *
   * <p>
   * <p>Example:
   *
   * <p>Suppose the max depth is 2, and suppose the starting page "A" links to the following web
   * pages:
   *
   * <p><pre>
   *            A
   *         /  |  \
   *        B   C   D
   *       / \       \
   *      E  F        G
   * </pre>
   *
   * <p>In this example, the crawler will visit at most A, B, C, and D. The crawler may visit
   * <i>fewer</i> pages if it runs out of time before it can finish. See {@link #getTimeout()}.
   */
  public int getMaxDepth() {
    return maxDepth;
  }

  /**
   * The maximum amount of time the crawler is allowed to run.
   *
   * <p>This is not a strict deadline, meaning the crawler does not have to drop everything and
   * terminate immediately. However, Once this amount of time has been reached, the crawler will
   * finish processing any HTML it has already downloaded, but it will not download any more pages
   * or follow any more links.
   */
  public Duration getTimeout() {
    return timeout;
  }

  /**
   * The number of popular words the crawler should record in its output.
   *
   * <p>See {@link com.udacity.webcrawler.json.CrawlResult#getWordCounts()} for more information.
   */
  public int getPopularWordCount() {
    return popularWordCount;
  }

  /**
   * Path to the output file where performance data from this web crawl should be written.
   *
   * <p>If a file already exists at the path, performance information should be appended to the
   * existing file.
   *
   * <p>If the path is empty, the data will be written to standard output.
   */
  public String getProfileOutputPath() {
    return profileOutputPath;
  }

  /**
   * Path to the output file where the result data from this web crawl should be written.
   *
   * <p>If a file already exists at the path, the existing file should be replaced.
   *
   * <p>If the path is empty, the data will be written to standard output.
   *
   * <p>See {@link com.udacity.webcrawler.json.CrawlResult}.
   */
  public String getResultPath() {
    return resultPath;
  }

  /**
   * A builder class to create {@link CrawlerConfiguration} instances.
   */
  public static final class Builder {
    private final Set<String> startPages = new LinkedHashSet<>();
    private final Set<String> ignoredUrls = new LinkedHashSet<>();
    private final Set<String> ignoredWords = new LinkedHashSet<>();
    private int parallelism = -1;
    private String implementationOverride = "";
    private int maxDepth = 0;
    private int timeoutSeconds = 1;
    private int popularWordCount = 0;
    private String profileOutputPath = "";
    private String resultPath = "";

    /**
     * Adds a start page URL.
     *
     * <p>Does nothing if the given page has already been added. See {@link #getStartPages()}.
     */
    // Maps "startPages" in JSON to "addStartPages(String... startPages)" method with "@JsonProperty"
    @JsonProperty("startPages") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder addStartPages(String... startPages) {
      for (String startPage : startPages) {
        this.startPages.add(Objects.requireNonNull(startPage));
      }
      return this;
    }

    /**
     * Adds a regular expression pattern that defines URLs to ignore during the crawl.
     *
     * <p>Does nothing if the same pattern has already been added. See {@link #getIgnoredUrls()}.
     *
     * @param patterns one or more regular expressions that define a valid {@link Pattern}.
     */
    // Maps "ignoredUrls" in JSON to "addIgnoredUrls(String... patterns)" method with "@JsonProperty"
    @JsonProperty("ignoredUrls") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder addIgnoredUrls(String... patterns) {
      for (String pattern : patterns) {
        ignoredUrls.add(Objects.requireNonNull(pattern));
      }
      return this;
    }

    /**
     * Adds a regular expression pattern that defines words to ignore when computing the popular
     * counts.
     *
     * <p>Does nothing if the same pattern has already been added. See {@link #getIgnoredWords()}.
     *
     * <p>See {@link com.udacity.webcrawler.json.CrawlResult#getWordCounts()} for more information
     * about the popular word computation.
     *
     * @param patterns one or more regular expressions that define a valid {@link Pattern}.
     */
    // Maps "ignoredWords" in JSON to "addIgnoredWords(String... patterns)" method with "@JsonProperty"
    @JsonProperty("ignoredWords") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder addIgnoredWords(String... patterns) {
      for (String pattern : patterns) {
        ignoredWords.add(Objects.requireNonNull(pattern));
      }
      return this;
    }

    /**
     * Sets the desired parallelism of the crawl.
     *
     * <p>See {@link #getParallelism()}.
     */
    // Maps "parallelism" in JSON to "setParallelism(int parallelism)" method with "@JsonProperty"
    @JsonProperty("parallelism") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder setParallelism(int parallelism) {
      this.parallelism = parallelism;
      return this;
    }

    /**
     * Overrides the {@link com.udacity.webcrawler.WebCrawler} implementation that should be used
     * for the crawl.
     *
     * <p>See {@link #getImplementationOverride()}.
     */
    // Maps "implementationOverride" in JSON to "setImplementationOverride(String implementationOverride)" method with "@JsonProperty"
    @JsonProperty("implementationOverride") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder setImplementationOverride(String implementationOverride) {
      this.implementationOverride = Objects.requireNonNull(implementationOverride);
      return this;
    }

    /**
     * Sets the maximum depth of the crawl.
     *
     * <p>See {@link #getMaxDepth()}.
     */
    // Maps "maxDepth" in JSON to "setMaxDepth(int maxDepth)" method with "@JsonProperty"
    @JsonProperty("maxDepth") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
      return this;
    }

    /**
     * Sets the maximum amount of time allowed for the crawl, specified in seconds.
     *
     * <p>See {@link #getTimeout()}.
     */
    // Maps "timeoutSeconds" in JSON to "setTimeoutSeconds(int seconds)" method with "@JsonProperty"
    @JsonProperty("timeoutSeconds") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder setTimeoutSeconds(int seconds) {
      this.timeoutSeconds = seconds;
      return this;
    }

    /**
     * Sets the number of most popular words that should be reported by the crawl.
     *
     * <p>See {@link #getPopularWordCount()}.
     */
    // Maps "popularWordCount" in JSON to "setPopularWordCount(int popularWordCount)" method with "@JsonProperty"
    @JsonProperty("popularWordCount") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder setPopularWordCount(int popularWordCount) {
      this.popularWordCount = popularWordCount;
      return this;
    }

    /**
     * Sets the path to the file where profiling data for this crawl should be written.
     *
     * <p>See {@link #getProfileOutputPath()}.
     */
    // Maps "profileOutputPath" in JSON to "setProfileOutputPath(String profileOutputPath)" method with "@JsonProperty"
    @JsonProperty("profileOutputPath") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder setProfileOutputPath(String profileOutputPath) {
      this.profileOutputPath = Objects.requireNonNull(profileOutputPath);
      return this;
    }

    /**
     * Sets the path to the file where the result of this crawl should be written.
     *
     * <p>See {@link #getResultPath()}.
     */
    // Maps "resultPath" in JSON to "setResultPath(String resultPath)" method with "@JsonProperty"
    @JsonProperty("resultPath") // defines mapping between JSON property names & builder methods by annotating all builder's setter methods with "@JsonProperty"
    public Builder setResultPath(String resultPath) {
      this.resultPath = Objects.requireNonNull(resultPath);
      return this;
    }

    /**
     * Constructs a {@link CrawlerConfiguration} from this builder.
     */
    public CrawlerConfiguration build() {
      if (maxDepth < 0) {
        throw new IllegalArgumentException("maxDepth cannot be negative");
      }
      if (timeoutSeconds <= 0) {
        throw new IllegalArgumentException("timeoutSeconds must be positive");
      }
      if (popularWordCount < 0) {
        throw new IllegalArgumentException("popularWordCount cannot be negative");
      }

      return new CrawlerConfiguration(
              startPages.stream().collect(Collectors.toUnmodifiableList()),
              ignoredUrls.stream().map(Pattern::compile).collect(Collectors.toUnmodifiableList()),
              ignoredWords.stream().map(Pattern::compile).collect(Collectors.toUnmodifiableList()),
              parallelism,
              implementationOverride,
              maxDepth,
              Duration.ofSeconds(timeoutSeconds),
              popularWordCount,
              profileOutputPath,
              resultPath);
    }
  }
}
