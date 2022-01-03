package httpclient.createdocument;

import client.Client;
import com.google.gson.Gson;
import httpurlconnection.index.IndexCreate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CreateDocument<T> {
//    private final static Logger log = LoggerFactory.getLogger(CreateDocument.class);
    static final Logger log = LogManager.getLogger(CreateDocument.class);
    Gson gson = new Gson();

    public void createDocument(String indexName, String typeName, T entity) {
        try {
            HttpPost httpPost = new HttpPost(Client.URL + indexName + "/" + typeName);

            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Content-Type", "application/json");

            String toJson = gson.toJson(entity);

            httpPost.setEntity(new StringEntity(toJson));

            HttpClient httpClient = HttpClientBuilder.create().build(); // httpClient post body 객체 생성
            HttpResponse response = httpClient.execute(httpPost); // response 반환

            String resultJson = EntityUtils.toString(response.getEntity()); // 응답 값 String 변환
            log.info(resultJson);
        } catch (IOException e) {
        }
    }
}
