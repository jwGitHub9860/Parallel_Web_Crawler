package com.udacity.webcrawler.main;

import com.google.inject.Guice;
import com.udacity.webcrawler.WebCrawler;
import com.udacity.webcrawler.WebCrawlerModule;
import com.udacity.webcrawler.json.ConfigurationLoader;
import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.json.CrawlResultWriter;
import com.udacity.webcrawler.json.CrawlerConfiguration;
import com.udacity.webcrawler.profiler.Profiler;
import com.udacity.webcrawler.profiler.ProfilerModule;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

public final class WebCrawlerMain {

  private final CrawlerConfiguration config;

  private WebCrawlerMain(CrawlerConfiguration config) {
    this.config = Objects.requireNonNull(config);
  }

  @Inject
  private WebCrawler crawler;

  @Inject
  private Profiler profiler;

  private void run() throws Exception {
    Guice.createInjector(new WebCrawlerModule(config), new ProfilerModule()).injectMembers(this);

    CrawlResult result = crawler.crawl(config.getStartPages());
    CrawlResultWriter resultWriter = new CrawlResultWriter(result);
    // TODO: Write the crawl results to a JSON file (or System.out if the file name is empty)
    // Checks if "config.getResultPath()" Value is NOT Empty
    if (!config.getResultPath().isEmpty()) {
      // Creates "Path" using "config.getResultPath()" as File Name
      Path resultPath = Path.of(config.getResultPath());

      // Passes "resultPath" to "CrawlResultWriter write(Path)" Method to Write Crawl Results to "resultWriter"
      resultWriter.write(resultPath);
    } else {
      // Creates "Writer" (JSON string) from "System.out"
      try (Writer writer = new OutputStreamWriter(System.out)) { // "OutputStreamWriter" -> converts "System.out" to "Writer"
        // Passes "writer" to "CrawlResultWriter write(Path)" Method to Write Crawl Results to "resultWriter"
        resultWriter.write(writer);
      } catch (java.lang.Exception e) {
        throw new RuntimeException(e);
      }
    }
    // TODO: Write the profile data to a text file (or System.out if the file name is empty)
    // Checks if "config.getProfileOutputPath()" Value is NOT Empty
    if (!config.getProfileOutputPath().isEmpty()) {
      // Creates "Path" using "config.getProfileOutputPath()" as File Name
      Path resultPath = Path.of(config.getProfileOutputPath());

      // Writes "resultPath" to "profiler" by Calling "writeData(Path path)" Method from "Profiler.java"
      profiler.writeData(resultPath); // "writeData()" -> general method that writes data to specific location
    } else {
      // Creates "Writer" (JSON string) from "System.out"
      try (Writer writer = new ObjectOutputStream(new OutputStreamWriter(System.out))) { // "OutputStreamWriter" -> converts "System.out" to "Writer"
        // Writes "writer" to "profiler" by Calling "writeData(Path path)" Method from "Profiler.java"
        profiler.writeData(writer); // "writeData()" -> general method that writes data to specific location
      } catch (java.lang.Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: WebCrawlerMain [starting-url]");
      return;
    }

    CrawlerConfiguration config = new ConfigurationLoader(Path.of(args[0])).load();
    new WebCrawlerMain(config).run();
  }
}
