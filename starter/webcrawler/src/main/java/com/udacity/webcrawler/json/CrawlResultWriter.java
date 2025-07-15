package com.udacity.webcrawler.json;

// Defines "ObjectMapper" by Creating new "com.fasterxml.jackson.databind.ObjectMapper"
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.io.FileOutputStream; // defines "ObjectOutputStream" and/or "Files.ObjectOutputStream"
import java.io.ObjectOutputStream; // defines "ObjectOutputStream" and/or "Files.ObjectOutputStream"
import java.io.OutputStreamWriter; // defines "OutputStreamWriter"
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class to write a {@link CrawlResult} to file.
 */
public final class CrawlResultWriter {
  private final CrawlResult result;

  /**
   * Creates a new {@link CrawlResultWriter} that will write the given {@link CrawlResult}.
   */
  public CrawlResultWriter(CrawlResult result) {
    this.result = Objects.requireNonNull(result);
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Path}.
   *
   * <p>If a file already exists at the path, the existing file should not be deleted; new data
   * should be appended to it.
   *
   * @param path the file path where the crawl result data should be written.
   */
  public void write(Path path) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(path);
    // TODO: Fill in this method.

    // Creates "Writer" (JSON string) from "Path" file
    try (Writer writer = Files.newBufferedWriter(Objects.requireNonNull(path))) { // "Files.newBufferedWriter()" - creates writer (JSON string) from "Path" file
      // Writes Bytes to File -> Properly Serializing JSON to File, Ensuring "Writer" Closes Correctly By try-with-resources, & Avoiding Recursion that Causes Stream Handling Issues
      write(writer); // write(path) -> "writer(Path path)" method contains recursive call that prevents proper stream closure, creating infinite loop that prevents proper stream closure
    } catch (java.lang.Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Writer}.
   *
   * @param writer the destination where the crawl result data should be written.
   */
  public void write(Writer writer) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(writer);
    // TODO: Fill in this method.
    try {
      // Creates "ObjectMapper" Instance & Serialize Java objects into JSON
      ObjectMapper objectMapper = new ObjectMapper();

      // Calls ".writeValue()" for "objectMapper" to Write to "result" File
      objectMapper.writeValue(Objects.requireNonNull(writer), result); // "result" File is DEFINED in "CrawlResultWriter" constructor
    } catch (java.lang.Exception e) {
      throw new RuntimeException(e);
    }
  }
}
