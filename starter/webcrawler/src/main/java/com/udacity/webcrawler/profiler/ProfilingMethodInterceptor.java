package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final Object delegate;
  private final ProfilingState state;
  private final ZonedDateTime startTime;

  // TODO: You will need to add more instance fields and constructor arguments to this class.
  ProfilingMethodInterceptor(Clock clock, Object delegate, ProfilingState state, ZonedDateTime startTime) {
    this.clock = Objects.requireNonNull(clock);
    this.delegate = Objects.requireNonNull(delegate);
    this.state = Objects.requireNonNull(state);
    this.startTime = Objects.requireNonNull(startTime);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable { // use "Throwable" to handle unexpected events & prevent program crashes
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.

    // Initializes and/or Resets "startTime"
    Instant startTime = null;

    // Obtains Method Annotation
    Profiled annotation = method.getAnnotation(Profiled.class); // will be used to Determine if Annotation Has Been Profiled Or Not

    // Return Value for Method
    Object currentInvokeMethod = null;

    // Checks if All Methods Have Been Profiled
    if (annotation != null) {
      try {
        // Records Start Time As Current Time
        startTime = clock.instant();

        // Calls "delegate" Class Using ".invoke()"
        currentInvokeMethod = method.invoke(delegate, args);
      }
      // "InvocationTargetException" - wraps exception thrown by Invoked Method or Constructor
      catch (InvocationTargetException ex) { // MUST SPECIFICALLY CATCH "InvocationTargetException" exceptions TO PREVENT BUILD ERRORS
        throw ex.getTargetException();
      } catch (IllegalArgumentException e) { // MUST SPECIFICALLY CATCH "IllegalAruguementException" exceptions TO PREVENT BUILD ERRORS
        throw new RuntimeException(e);
      } finally {
        // Measures Duration of "method" Invocation
        Duration methodDuration = Duration.between(startTime, clock.instant());

        // Records How Long Method Call Took
        state.record(delegate.getClass(), method, methodDuration);
      }

      // Returns Invoked Method
      return currentInvokeMethod;
    } else {
      // Calls & Returns "delegate" Class Using ".invoke()"
      return method.invoke(delegate, args);
    }
  }
}
