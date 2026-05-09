package com.codexlab.aimurder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    /**
     * 提供一个专门用于 SSE 流式任务的异步线程池。
     *
     * @return SSE 异步推送执行器
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService sseExecutorService() {
        return Executors.newCachedThreadPool();
    }
}
