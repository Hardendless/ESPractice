package org.example.task;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.example.pool.ESClientPool;

import java.io.IOException;

public class MyTask implements Runnable {
    @Override
    public void run() {
        RestHighLevelClient client = null;
        try {
            client = ESClientPool.getInstance().getClient();
            // 使用ESClient执行任务逻辑
            IndexRequest request = new IndexRequest("my_index").id("1").source("{\"field\": \"value\"}", XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            System.out.println("Document indexed: " + response.getId());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                ESClientPool.getInstance().releaseClient(client);
            }
        }
    }
}

