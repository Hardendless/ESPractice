package org.example.pool;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ESClientPool {
    private static final int MAX_CORE_CLIENTS = 5; // 最大核心对象数量
    private static final int MAX_NON_CORE_CLIENTS = 10; // 最大非核心对象数量

    private Queue<RestHighLevelClient> coreClients; // 核心对象队列
    private Queue<RestHighLevelClient> nonCoreClients; // 非核心对象队列

    private static ESClientPool instance;

    private ESClientPool() {
        coreClients = new ConcurrentLinkedQueue<>();
        nonCoreClients = new ConcurrentLinkedQueue<>();
    }

    public static ESClientPool getInstance() {
        if (instance == null) {
            synchronized (ESClientPool.class) {
                if (instance == null) {
                    instance = new ESClientPool();
                    instance.initializePool();
                }
            }
        }
        return instance;
    }

    // 从对象池中获取ESClient
    public RestHighLevelClient getClient() {
        RestHighLevelClient client = null;
        if (!coreClients.isEmpty()) {
            client = coreClients.poll();
        } else if (coreClients.size() + nonCoreClients.size() < MAX_NON_CORE_CLIENTS) {
            client = createESClient();
            nonCoreClients.add(client);
        } else {
            throw new IllegalStateException("ESClient pool exhausted");
        }
        return client;
    }

    // 将ESClient归还给对象池
    public void releaseClient(RestHighLevelClient client) {
        if (coreClients.size() < MAX_CORE_CLIENTS) {
            coreClients.add(client);
        } else {
            nonCoreClients.remove(client);
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 初始化对象池
    private void initializePool() {
        for (int i = 0; i < MAX_CORE_CLIENTS; i++) {
            RestHighLevelClient client = createESClient();
            coreClients.add(client);
        }
    }

    // 创建一个ESClient对象
    private RestHighLevelClient createESClient() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("localhost", 9200, "http")
                // 可以根据需要设置其他ES连接配置
        );
        return new RestHighLevelClient(builder);
    }
}

