package com.faithccd.config;

import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AppplicationConfig {
	
    /** 샘플 기본 Thread 수 */
    private static int TASK_SAMPLE_CORE_POOL_SIZE = 8;
    /** 샘플 최대 Thread 수 */
    private static int TASK_SAMPLE_MAX_POOL_SIZE = 16;
    /** 샘플 QUEUE 수 */
    private static int TASK_SAMPLE_QUEUE_CAPACITY = 0;
    /** 샘플 Thread Bean Name */
    private static String EXECUTOR_SAMPLE_BEAN_NAME = "faithccd-pool";
	
    /**
     * 샘플 Thread 생성
     *
     * @return
     */
    @Bean(name = "asyncThreadTaskExecutor")
    public Executor asyncThreadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(TASK_SAMPLE_CORE_POOL_SIZE);
        executor.setMaxPoolSize(TASK_SAMPLE_MAX_POOL_SIZE);
        executor.setQueueCapacity(TASK_SAMPLE_QUEUE_CAPACITY);
        executor.setBeanName(EXECUTOR_SAMPLE_BEAN_NAME);
        executor.setThreadNamePrefix(EXECUTOR_SAMPLE_BEAN_NAME);
        return executor;
    }

}
