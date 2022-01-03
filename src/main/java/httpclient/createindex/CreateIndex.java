package httpclient.createindex;

import client.Client;
import httpurlconnection.index.IndexCreate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateIndex {

//    private final static Logger log = LoggerFactory.getLogger(CreateIndex.class);
    static final Logger log = LogManager.getLogger(CreateIndex.class);

    public void createIndex(String indexName, String typeName, String path, int shard, int replica) {
        try {
            HttpPut put = new HttpPut(Client.URL + indexName + "/" + typeName); // http put 객체 생성
            IndexCreate create = new IndexCreate();

            put.setHeader("Accept", "application/json");
            put.setHeader("Content-Type", "application/json");

            put.setEntity(new StringEntity(create.indexSettings(typeName, path, shard, replica))); // body 설정

            HttpClient httpClient = HttpClientBuilder.create().build(); // httpClient body 객체 생성
            HttpResponse response = httpClient.execute(put); // put 후 response 반환

            String resultJson = EntityUtils.toString(response.getEntity()); // 응답 값 String 변환
            log.info(resultJson);
        } catch (Exception e) {
        }
    }
}
