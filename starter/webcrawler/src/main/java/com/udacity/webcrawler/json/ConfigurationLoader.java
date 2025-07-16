package com.udacity.webcrawler.json;

// Defines "ObjectMapper" by Creating new "com.fasterxml.jackson.databind.ObjectMapper"
import com.fasterxml.jackson.databind.ObjectMapper; // to implement "CrawlerConfiguration read(Reader reader)" method

// Creates new "com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE"
import com.fasterxml.jackson.core.JsonParser;

// Defines "@JsonDeserialize" Annotation
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.nio.file.Files;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A static utility class that loads a JSON configuration file.
 */
// Marks "CrawlerConfiguration read(Reader reader)" method as Deserialization Target to Deserialize JSON input & Complete Method
@JsonDeserialize(builder = CrawlerConfiguration.Builder.class) // Jackson JSON-parsing annotation
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from this {@link ConfigurationLoader}'s path
   *
   * @return the loaded {@link CrawlerConfiguration}.
   */
  // Reads JSON string from "Path" File
  public CrawlerConfiguration load() throws Exception {
    // TODO: Fill in this method.

    // Creates "Reader" (JSON string) from "Path" file; "{}" of try block -> Uses "try-with-resources idiom" When Creating Output Stream to Guarantee File Will Be Closed
    try (Reader reader = Files.newBufferedReader(path)) { // "Files.newBufferedReader()" - creates reader (JSON string) from "Path" file
      // Passes "reader" (JSON string) into "read(Reader reader)" method & Returns "CrawlerConfiguration" Result
      return read(reader);
    }
  }

  /**
   * Loads crawler configuration from the given reader.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   */
  // Reads JSON input & De-serializes (or Parses) it into "CrawlerConfiguration" using Jackson JSON Library
  public static CrawlerConfiguration read(Reader reader) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(reader); // CANNOT Be Used in "readValue()" When Reading "reader" or ERROR WILL OCCUR
    // TODO: Fill in this method
    try {
      // Serialize Java objects into JSON & Creates "ObjectMapper" Instance
      ObjectMapper objectMapper = new ObjectMapper(); // creates new Jackson "objectMapper"

      // Disables Feature that Automatically Closes "ObjectMapper" to Prevent Jackson Library from Closing Input "Reader"
      objectMapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

      // Creates "CrawlerConfiguration" Instance
      CrawlerConfiguration jsonInput = objectMapper
              // Calls ".readValue()" for "ObjectMapper"
              .readValue(reader, CrawlerConfiguration.Builder.class) // to implement "CrawlerConfiguration read(Reader reader)" method
              // Returns Final "ObjectMapper" Instance AS Last Step in Creating "ObjectMapper" Instance
              .build();

      // Returns "ObjectMapper" Instance to "load()" method
      return jsonInput;
    } catch (Exception e) {
      e.printStackTrace(); // MUST USE "printStackTrace()" TO PREVENT ERROR FROM OCCURING
    }
    return null;
  }
}
