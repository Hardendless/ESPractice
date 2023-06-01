package org.example;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
//        创建索引
//        CreateIndexRequest request = new CreateIndexRequest("user");
//        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
//        boolean acknowledged = response.isAcknowledged();
//        System.out.println("操作状态 = " + acknowledged);

        // 查询索引
//        GetIndexRequest request = new GetIndexRequest("user");
//        GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
//
//        System.out.println("aliases" + response.getAliases());
//        System.out.println("mappings" + response.getMappings());
//        System.out.println("settings" + response.getSetting("user", "index.number_of_shards"));

        // 删除索引
//        DeleteIndexRequest request = new DeleteIndexRequest("user");
//        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
//        System.out.println("删除操作的结果" + response.isAcknowledged());


        client.close();
    }
}