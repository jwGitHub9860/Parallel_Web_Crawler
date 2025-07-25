package com.udacity.webcrawler.json;

// Defines "ObjectMapper" by Creating new "com.fasterxml.jackson.databind.ObjectMapper"
import com.fasterxml.jackson.databind.ObjectMapper;

// Defines "JsonGenerator" by Creating new "com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET"
import com.fasterxml.jackson.core.JsonGenerator;

import java.nio.file.Files;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption; // defines "StandardOpenOption"
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
  public void write(Path path) throws Exception {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(path); // CANNOT Be Used in "newBufferedWriter()" When Creating "Writer" (JSON string) from "Path" file or Stream Will NOT Be Able To Close, ULTIMATELY CAUSING ERROR
    // TODO: Fill in this method.

    // Creates "Writer" (JSON string) from "Path" file; "StandardOpenOption.CREATE" -> Creates New File If it Does NOT Exist; "StandardOpenOption.APPEND" -> Writes To File If File is OPEN; "{}" of try block -> Uses "try-with-resources idiom" When Creating Output Stream to Guarantee File Will Be Closed
    try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) { // "Files.newBufferedWriter()" -> creates writer (JSON string) from "Path" file & "ObjectOutputStream" CANNOT BE USED Because "ObjectOutputStream" CANNOT BE CONVERTED TO "Writer"
      // Writes Bytes to File -> Properly Serializing JSON to File, Ensuring "Writer" Closes Correctly By try-with-resources, & Avoiding Recursion that Causes Stream Handling Issues
      write(writer); // write(path) -> "writer(Path path)" method contains recursive call that prevents proper stream closure, creating infinite loop that prevents proper stream closure
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

      // Disables "OutputStream" from Automatically Closing After ".writeValue()" is Used for "ObjectMapper"
      objectMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

      // Calls ".writeValue()" for "objectMapper" to Write to "result" File
      objectMapper.writeValue(Objects.requireNonNull(writer), result); // "result" File is DEFINED in "CrawlResultWriter" constructor
    } catch (java.lang.Exception e) { // MUST USE "catch block" TO PREVENT ERROR FROM OCCURING
      throw new RuntimeException(e);
    }
  }
}
