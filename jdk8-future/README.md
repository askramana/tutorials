# jdk8-future

Example project, explaining joint usage of 
- Java's CompletableFuture
- Spring Boot
- Spring Boot Test
- Spring Actuator

### Acceptance Criteria

We wanted to meet following goals while using _CompletableFuture_
- Uniform exception handling for our web-based application, which remains aligned with non-asynchronous methods.
- Proper usage monitoring

### Test Use Case

Illustration exposes a GET endpoint [customer/{id}](http://localhost:8080/customer/{id}), which combines result from 
2 parallel calls to produce the result. Implementation is made to reject non-positive Ids.

### Exception Handling

#### Non-asynchronous methods

- [ControllerAdvice](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html) 
was used across controllers to handle uncaught exceptions.

#### Asynchronous methods

- Java allows you to override [AsyncUncaughtExceptionHandler](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/aop/interceptor/AsyncUncaughtExceptionHandler.html)
in [AsyncConfigurer](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/annotation/AsyncConfigurer.html).
This handles uncaught exceptions for _void_ asynchronous methods.
```java
void handleUncaughtException(Throwable ex, Method method, Object... params)
```
- _@Async_ methods with _Future_ return types allows you to either define _exceptionally_ or _handle_

```java
CustomerDetails customerDetails = customerService.getCustomerPhoneNumber(customerId)
    .thenCombine(customerService.getCustomerEmailId(customerId), customerDetailMapper)
    .exceptionally(ex -> {
        LOGGER.error("Something went wrong while fetch details for customer " + customerId, ex);
        return null;
    }).get();
```
```java
CustomerDetails customerDetails = customerService.getCustomerPhoneNumber(customerId)
    .thenCombine(customerService.getCustomerEmailId(customerId), customerDetailMapper)
    .handle((result, ex) -> {
        if (ex != null) {
            LOGGER.error("Something went wrong while fetch details for customer " + customerId, ex);
            // return intended value 
        }
        return result;
    }).get();
```
- Since our controllers are the only consumer for services, we chose to let exception uncaught and be handled by _@ControllerAdvice_
```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleCompletionExceptions(Exception ex) {
     LOGGER.error("An async uncaught exception occurred", ex);
     return new ResponseEntity<>(new ErrorResponse("CompletionException", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
 }
```

### Usage Monitoring

- [/actuator/threaddump](localhost:8080/actuator/threaddump): Dumps the thread information of the underlying JVM.
You can find details corresponding to configured threads. An example snippet will look like one below.
```json
{
    "threads": [
        {
            "threadName": "MY-THREAD-1",
            "threadId": 36,
            "blockedTime": -1,
            "blockedCount": 0,
            "waitedTime": -1,
            "waitedCount": 1,
            "lockName": null,
            "lockOwnerId": -1,
            "lockOwnerName": null,
            "inNative": false,
            "suspended": false,
            "threadState": "TIMED_WAITING",
            "stackTrace": [
                {
                    "methodName": "sleep",
                    "fileName": "Thread.java",
                    "lineNumber": -2,
                    "className": "java.lang.Thread",
                    "nativeMethod": true
                },
                {
                    "methodName": "getCustomerEmailId",
                    "fileName": "AsyncCustomerService.java",
                    "lineNumber": 36,
                    "className": "com.chopstick.jdk8future.service.impl.AsyncCustomerService",
                    "nativeMethod": false
                },
                {
                    "methodName": "invoke0",
                    "fileName": "NativeMethodAccessorImpl.java",
                    "lineNumber": -2,
                    "className": "sun.reflect.NativeMethodAccessorImpl",
                    "nativeMethod": true
                },
                {
                    "methodName": "invoke",
                    "fileName": "NativeMethodAccessorImpl.java",
                    "lineNumber": 62,
                    "className": "sun.reflect.NativeMethodAccessorImpl",
                    "nativeMethod": false
                },
                {
                    "methodName": "invoke",
                    "fileName": "DelegatingMethodAccessorImpl.java",
                    "lineNumber": 43,
                    "className": "sun.reflect.DelegatingMethodAccessorImpl",
                    "nativeMethod": false
                },
                {
                    "methodName": "invoke",
                    "fileName": "Method.java",
                    "lineNumber": 498,
                    "className": "java.lang.reflect.Method",
                    "nativeMethod": false
                },
                {
                    "methodName": "invokeJoinpointUsingReflection",
                    "fileName": "AopUtils.java",
                    "lineNumber": 343,
                    "className": "org.springframework.aop.support.AopUtils",
                    "nativeMethod": false
                },
                {
                    "methodName": "invokeJoinpoint",
                    "fileName": "ReflectiveMethodInvocation.java",
                    "lineNumber": 198,
                    "className": "org.springframework.aop.framework.ReflectiveMethodInvocation",
                    "nativeMethod": false
                },
                {
                    "methodName": "proceed",
                    "fileName": "ReflectiveMethodInvocation.java",
                    "lineNumber": 163,
                    "className": "org.springframework.aop.framework.ReflectiveMethodInvocation",
                    "nativeMethod": false
                },
                {
                    "methodName": "lambda$invoke$0",
                    "fileName": "AsyncExecutionInterceptor.java",
                    "lineNumber": 115,
                    "className": "org.springframework.aop.interceptor.AsyncExecutionInterceptor",
                    "nativeMethod": false
                },
                {
                    "methodName": "call",
                    "fileName": null,
                    "lineNumber": -1,
                    "className": "org.springframework.aop.interceptor.AsyncExecutionInterceptor$$Lambda$591/1939250790",
                    "nativeMethod": false
                },
                {
                    "methodName": "lambda$doSubmit$3",
                    "fileName": "AsyncExecutionAspectSupport.java",
                    "lineNumber": 276,
                    "className": "org.springframework.aop.interceptor.AsyncExecutionAspectSupport",
                    "nativeMethod": false
                },
                {
                    "methodName": "get",
                    "fileName": null,
                    "lineNumber": -1,
                    "className": "org.springframework.aop.interceptor.AsyncExecutionAspectSupport$$Lambda$592/269738715",
                    "nativeMethod": false
                },
                {
                    "methodName": "run$$$capture",
                    "fileName": "CompletableFuture.java",
                    "lineNumber": 1590,
                    "className": "java.util.concurrent.CompletableFuture$AsyncSupply",
                    "nativeMethod": false
                },
                {
                    "methodName": "run",
                    "fileName": "CompletableFuture.java",
                    "lineNumber": -1,
                    "className": "java.util.concurrent.CompletableFuture$AsyncSupply",
                    "nativeMethod": false
                },
                {
                    "methodName": "lambda$decorate$0",
                    "fileName": "LoggingTaskDecorator.java",
                    "lineNumber": 23,
                    "className": "com.chopstick.jdk8future.common.LoggingTaskDecorator",
                    "nativeMethod": false
                },
                {
                    "methodName": "run",
                    "fileName": null,
                    "lineNumber": -1,
                    "className": "com.chopstick.jdk8future.common.LoggingTaskDecorator$$Lambda$593/729241916",
                    "nativeMethod": false
                },
                {
                    "methodName": "runWorker",
                    "fileName": "ThreadPoolExecutor.java",
                    "lineNumber": 1149,
                    "className": "java.util.concurrent.ThreadPoolExecutor",
                    "nativeMethod": false
                },
                {
                    "methodName": "run",
                    "fileName": "ThreadPoolExecutor.java",
                    "lineNumber": 624,
                    "className": "java.util.concurrent.ThreadPoolExecutor$Worker",
                    "nativeMethod": false
                },
                {
                    "methodName": "run",
                    "fileName": "Thread.java",
                    "lineNumber": 748,
                    "className": "java.lang.Thread",
                    "nativeMethod": false
                }
            ],
            "lockedMonitors": [],
            "lockedSynchronizers": [
                {
                    "className": "java.util.concurrent.ThreadPoolExecutor$Worker",
                    "identityHashCode": 1736869105
                }
            ],
            "lockInfo": null
        }
        ...
   ]}
```

### Build
Make sure that you've `JAVA_HOME` and `MAVEN_HOME` environment variables set.
```
    - cd <project directory>
    - mvn clean install package
    - cd target
    - java -jar jdk8-future-0.0.1-SNAPSHOT.jar &
```

### Who do I talk to?

[Ajay Singh](ajay.saaya@gmail.com)