package resthighlevel.thread;

import client.Client;
import entity.AgentInfoEntity;
import entity.JavaInfoEntity;
import httpclient.createdocument.CreateDocument;
import httpclient.createindex.CreateIndex;
import httpurlconnection.document.DocCreate;
import httpurlconnection.index.IndexCreate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resource.ResourceExtraction;
import resthighlevel.documentcreate.DocumentCreate;
import resthighlevel.indexcreate.AgentInfoIndexCreate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertThread extends Thread {

//    private final static Logger log = LoggerFactory.getLogger(InsertThread.class);
    static final Logger log = LogManager.getLogger(InsertThread.class);

    @Override
    public void run() {
        ResourceExtraction resource = new ResourceExtraction();

        while (true) {
            try {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String typeName = simpleDateFormat.format(date);

                AgentInfoEntity agentInfoEntity = new AgentInfoEntity(resource);
                JavaInfoEntity javaInfoEntity = new JavaInfoEntity(resource);

                // 인덱스 생성
                AgentInfoIndexCreate agentInfoIndexCreate = new AgentInfoIndexCreate();

                agentInfoIndexCreate.connection();

                boolean agentInfoAck = agentInfoIndexCreate.existIndex(Client.AGENT_INFO_INDEX); // agent_info index exist
                boolean javaInfoAck = agentInfoIndexCreate.existIndex(Client.JAVA_INFO_INDEX); // java_info index exist

                // 인덱스 존재하지 않을 시 인덱스 생성
                if (!agentInfoAck) {
                    agentInfoIndexCreate.createIndex(Client.AGENT_INFO_INDEX, typeName);
                }
                if (!javaInfoAck) {
                    agentInfoIndexCreate.createIndex(Client.JAVA_INFO_INDEX, typeName);
                }

                // 도큐먼트 생성
                DocumentCreate documentCreate = new DocumentCreate();

                documentCreate.documentCreate(agentInfoIndexCreate.getHighLevelClient(), agentInfoEntity, Client.AGENT_INFO_INDEX, typeName);
                documentCreate.documentCreate(agentInfoIndexCreate.getHighLevelClient(), javaInfoEntity, Client.JAVA_INFO_INDEX, typeName);

                agentInfoIndexCreate.close();

                Thread.sleep(59450);

            } catch (IOException e) {
                log.error("Create Document Failed !! {}", e.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
