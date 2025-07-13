package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler; // defines "Handler"
import java.lang.reflect.Method; // defines "Method"
import java.lang.reflect.Proxy; // allows use of dynamic proxy
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * Concrete implementation of the {@link Profiler}.
 */
final class ProfilerImpl implements Profiler {

  private final Clock clock;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(Clock clock) {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
  }

  @Override
  public <T> T wrap(Class<T> klass, T delegate) {
    // Gets Rid of Unused Variable Warning
    Objects.requireNonNull(klass);

    // TODO: Use a dynamic proxy (java.lang.reflect.Proxy) to "wrap" the delegate in a
    //       ProfilingMethodInterceptor and return a dynamic proxy from this method.
    //       See https://docs.oracle.com/javase/10/docs/api/java/lang/reflect/Proxy.html.

    // Checks if "klass" Has NO Annotation
    if (!hasAnnotation(klass)) {
      throw new IllegalArgumentException("Please make sure that " + klass.getName() + " has annotations.");
    }

    // Calls "ProfilingMethodInterceptor" Constructor to "wrap" "delegate" in "ProfilingMethodInterceptor" & Create Handler for Dynamic Proxy Instance
    ProfilingMethodInterceptor profilerImplHandler = new ProfilingMethodInterceptor(this.clock, delegate, this.state, this.startTime);

    // Creates Dynamic Proxy Instance
    Object proxy = Proxy.newProxyInstance( // proxy MUST BE "Object" variable WHEN FIRST CREATED To Allow Proxy To Be Created With Different Types Of Variables
            // Passes in "ProfilingMethodInterceptor" as Class Loader
            ProfilingMethodInterceptor.class.getClassLoader(), // MUST USE ".class" TO CORRECTLY PASS CLASS AS CLASS LOADER & "Object" allows use of Different Types Of Variables to create proxy instance
            // Passes in "Objects.requireNonNull(klass)" as Interface that Proxy should implement
            new Class[]{Objects.requireNonNull(klass)}, // "Object" allows use of Different Types Of Variables to create proxy instance
            // Passes in Custom Invocation Handler
            profilerImplHandler);

    // Return Dynamic Proxy from method
    return (T) proxy; // proxy delegates to "T"
  }

  @Override
  public void writeData(Path path) {
    // TODO: Write the ProfilingState data to the given file path. If a file already exists at that
    //       path, the new data should be appended to the existing file.

    // Gets Rid of Unused Variable Warning
    Objects.requireNonNull(path);

    // Creates Object Output Stream of "Writer" (JSON string) from "Path" file
    try (Writer writer = Files.ObjectOutputStream(Objects.requireNonNull(path))) { // "Files.ObjectOutputStream()" -> allows Serialized Data to be Written To File
      // Writes Bytes to File -> Properly Serializing JSON to File, Ensuring "Writer" Closes Correctly By try-with-resources, & Avoiding Recursion that Causes Stream Handling Issues
      writer(path); // writeData(path) -> "writeData(Path path)" method contains recursive call that prevents proper stream closure, creating infinite loop that prevents proper stream closure
    } catch (java.lang.Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
  }

  @Profiled
  public boolean hasAnnotation(Class<?> klass) throws IllegalArgumentException {
    // Returns Annotation(s) of "klass"
    Method[] annotationMethods = klass.getDeclaredMethods(); // use array because there may be more that 1 element

    // Iterates Through "annotationMethods" Array
    for (Method annotationMethod : annotationMethods) {
      // Check if "annotationMethod" is NOT Empty
      if (annotationMethod.getAnnotation(Profiled.class) != null) {
        return true;
      }
    }
    return false;
  }
}
