package com.chopstick.jdk8future.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Customizes the {@link java.util.concurrent.Executor} instance used when processing async methods.
 * <p>
 * This {@link AsyncConfigurer} configuration class get initialized early in the application context bootstrap.
 * If you need any dependencies on other beans there, make sure to declare them 'lazy' as far as possible
 * in order to let them go through other post-processors as well.
 * <p>
 * <p>
 * The {@link EnableAsync#mode} is left to default {@link AdviceMode#PROXY},
 * which allows for interception of calls through the proxy only; local calls within the same class cannot get intercepted this way.
 *
 * @see EnableAsync
 */
@EnableAsync(mode = AdviceMode.PROXY)
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "executor")
@lombok.Getter
@lombok.Setter
public class AsyncConfig implements AsyncConfigurer {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private int keepAliveSeconds;

    private String threadGroupName;
    private String threadNamePrefix;

    @Lazy
    @Autowired
    private TaskDecorator taskDecorator;

    @Lazy
    @Autowired
    private AsyncUncaughtExceptionHandler exceptionHandler;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);

        executor.setTaskDecorator(taskDecorator);

        executor.setThreadGroupName(threadGroupName);
        executor.setThreadNamePrefix(threadNamePrefix);

        executor.initialize();
        return executor;
    }

    /**
     * The {@link AsyncUncaughtExceptionHandler} instance to be used
     * when an exception is thrown during an asynchronous method execution
     * with {@code void} return type.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return exceptionHandler;
    }

}
