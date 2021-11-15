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

                /** httpUrlConnection */

//                // Index create
//                IndexCreate indexCreate = new IndexCreate();
//                indexCreate.indexCreate(Client.AGENT_INFO_INDEX, typeName, Client.AGENT_SETTINGS_PATH, 5, 1); // agent_info index mapping create
//                indexCreate.indexCreate(Client.JAVA_INFO_INDEX, typeName, Client.JAVA_SETTINGS_PATH, 5, 1); // java_info index mapping create
//
//                // Document create
//                DocCreate docCreate = new DocCreate();
//                docCreate.documentCreate(Client.AGENT_INFO_INDEX, typeName, agentInfoEntity);
//                docCreate.documentCreate(Client.JAVA_INFO_INDEX, typeName, javaInfoEntity);



                /** httpClient */

//                // Index create
//                CreateIndex indexCreate = new CreateIndex();
//                indexCreate.createIndex(Client.AGENT_INFO_INDEX, typeName, Client.AGENT_SETTINGS_PATH, 5, 1);
//                indexCreate.createIndex(Client.JAVA_INFO_INDEX, typeName, Client.JAVA_SETTINGS_PATH, 5, 1);
//
//                // Document create
//                CreateDocument createDocument = new CreateDocument();
//
//                createDocument.createDocument(Client.AGENT_INFO_INDEX, typeName, agentInfoEntity);
//                createDocument.createDocument(Client.JAVA_INFO_INDEX, typeName, javaInfoEntity);




                /** RestHighLevelClient */

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
