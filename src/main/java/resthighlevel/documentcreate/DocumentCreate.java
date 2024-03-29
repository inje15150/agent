package resthighlevel.documentcreate;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class DocumentCreate<T> {

//    private final static Logger log = LoggerFactory.getLogger(DocumentCreate.class);
    static final Logger log = LogManager.getLogger(DocumentCreate.class);
    private Gson gson = new Gson();
    private Random rd = new Random();

    public void documentCreate(RestHighLevelClient client, T entity, String indexName, String typeName) throws IOException {

        String toJson = gson.toJson(entity); // entity -> json 변환

        IndexRequest request = new IndexRequest(indexName, typeName); // request 객체 생성

        request.source(toJson, XContentType.JSON);

        IndexResponse index = client.index(request, RequestOptions.DEFAULT);// request document create

        log.info( "[{}/{}] : create document successfully !", indexName, typeName);
    }
}
