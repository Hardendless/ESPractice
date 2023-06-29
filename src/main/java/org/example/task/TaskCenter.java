package org.example.task;

import org.elasticsearch.client.RestHighLevelClient;
import org.example.pool.ESClientPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskCenter {
    private ESClientPool clientPool;
    private ThreadPoolExecutor executor;

    public TaskCenter() {
        clientPool = ESClientPool.getInstance();
        int corePoolSize = 5; // 核心线程数
        int maxPoolSize = 10; // 最大线程数
        long keepAliveTime = 60; // 线程空闲时间（秒）
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100); // 任务队列
        executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    }

    public void executeTask(Runnable task) {
        executor.execute(() -> {
            RestHighLevelClient client = null;
            try {
                client = clientPool.getClient();
                // 使用ESClient执行任务
                task.run();
            } finally {
                if (client != null) {
                    clientPool.releaseClient(client);
                }
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

