package com.jy.yande.config.threadpool;

import lombok.Data;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConfigurationProperties(prefix = "threadpoolconfig")
@Data
public class ThreadPoolConfig {

    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private Integer keepAliveSeconds;
    private String threadNamePrefix;

    @Bean(name = "yandeThreadPool")
    public ThreadPoolTaskExecutor myTaskAsyncPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);//表示线程池核心线程，正常情况下开启的线程数量。
        executor.setMaxPoolSize(maxPoolSize);//当核心线程都在跑任务，还有多余的任务会存到此处。
        executor.setQueueCapacity(queueCapacity);//如果queueCapacity存满了，还有任务就会启动更多的线程，直到线程数达到maxPoolSize。如果还有任务，则根据拒绝策略进行处理。
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//该策略是又调用任务的线程执行。
        executor.setKeepAliveSeconds(keepAliveSeconds);//非核心线程的超时时长，超长后会被回收。
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();//初始化线程池。
        return executor;
    }

}
