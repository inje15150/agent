package resthighlevel.indexcreate;

import client.Client;
import org.apache.http.HttpHost;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class AgentInfoIndexCreate {

    static final Logger log = LogManager.getLogger(AgentInfoIndexCreate.class);
    private RestHighLevelClient highLevelClient = null;

    public void connection() {
        highLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("192.168.60.80", Client.PORT, "http")));
    }

    // getter
    public RestHighLevelClient getHighLevelClient() {
        return highLevelClient;
    }

    //Create Index
    public void createIndex(String indexName, String typeName) {

        CreateIndexRequest indexRequest = new CreateIndexRequest(indexName); // index 생성 객체
        PutMappingRequest putMappingRequest = new PutMappingRequest(indexName); // mapping 객체 생성
        putMappingRequest.type(typeName);

        indexSettings(indexRequest); // 인덱스 세팅 후 생성
        indexMappings(putMappingRequest, typeName, indexName);
    }

    //Settings
    public void indexSettings(CreateIndexRequest indexRequest) {
        try {
            indexRequest.settings(Settings.builder()
                    .put("index.number_of_shards", 5)
                    .put("index.number_of_replicas", 1));
            CreateIndexResponse createIndexResponse = highLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT); // 실제 인덱스 생성

            log.info("index settings complete !");

        } catch (IOException e) {
            log.error("Index Settings error.. {}", e.getMessage());
        }
    }

    //Mapping
    public void indexMappings(PutMappingRequest putMappingRequest, String typeName, String indexName) {
        XContentBuilder builder = null;
        try {
            if (indexName.equals(Client.AGENT_INFO_INDEX)) {
                builder = agentInfoIndex(typeName);
            } else if (indexName.equals(Client.JAVA_INFO_INDEX)) {
                builder = javaInfoIndex(typeName);
            }
            putMappingRequest.source(builder);
            AcknowledgedResponse response = highLevelClient.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);

//            if (response.isAcknowledged()) {
//                log.info( "[{}] index mapping successfully", indexName);
//            } else {
//                log.info("[{}] index already exist", indexName);
//            }
        } catch (IOException e) {
            log.error("[{}] index mapping error..{}", indexName, e.getMessage());
        }
    }

    // java_info index mapping builder
    public XContentBuilder javaInfoIndex(String typeName) {
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
                    .startObject(typeName)
                    .startObject("properties")
                    .startObject("totalMem")
                    .field("type", "float")
                    .endObject()
                    .startObject("usedMem")
                    .field("type", "float")
                    .endObject()
                    .startObject("freeMem")
                    .field("type", "float")
                    .endObject()
                    .startObject("event_time")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd HH:mm:ss||yyyy/MM/dd HH:mm:ss||epoch_millis")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            log.error("mapping type error !! {}", e.getMessage());
        }
        return builder;
    }

    // agent_info index mapping builder
    public XContentBuilder agentInfoIndex(String typeName) {
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
                    .startObject(typeName)
                    .startObject("properties")
                    .startObject("hostname")
                    .field("type", "text")
                    .endObject()
                    .startObject("OS")
                    .field("type", "text")
                    .endObject()
                    .startObject("interface_name")
                    .field("type", "text")
                    .endObject()
                    .startObject("ip")
                    .field("type", "text")
                    .endObject()
                    .startObject("gateway")
                    .field("type", "text")
                    .endObject()
                    .startObject("mac_address")
                    .field("type", "text")
                    .endObject()
                    .startObject("cpu")
                    .field("type", "float")
                    .endObject()
                    .startObject("memory")
                    .field("type", "float")
                    .endObject()
                    .startObject("disk")
                    .field("type", "float")
                    .endObject()
                    .startObject("event_time")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd HH:mm:ss||yyyy/MM/dd HH:mm:ss||epoch_millis")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            log.error("mapping type error !! {}", e.getMessage());
        }
        return builder;
    }

    // 인덱스 존재 여부
    public boolean existIndex(String indexName) {
        GetIndexRequest getIndexRequest = new GetIndexRequest().indices(indexName);

        boolean acknowledged = false;

        try {
            acknowledged = highLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return acknowledged;
    }

    public void close() {
        try {
            highLevelClient.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
