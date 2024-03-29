package client;

import com.google.gson.Gson;
import entity.AgentInfoEntity;
import entity.JavaInfoEntity;
import entity.TotalData;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import resource.ResourceExtraction;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class Client {

//    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Client.class);
    private static Logger log = LogManager.getLogger(Client.class);
    public final static int PORT = 9200;
    public final static int SOCKET_PORT = 8080;
    public final static String URL = "http://192.168.60.80:" + PORT + "/";
    public final static String AGENT_INFO_INDEX = "agent_info";
    public final static String JAVA_INFO_INDEX = "java_info";
    private final static String keyRule = "%%#!";
    public static Gson gson = new Gson();
    private final static String SERVER_IP = "192.168.91.166";
    private static int retryCount = 0;




    public static void main(String[] args) {
        Socket socket = null;
        ResourceExtraction resource = new ResourceExtraction();

        while (true) {
            try {
                socket = new Socket(SERVER_IP, SOCKET_PORT);

                while (true) {

                    long start = System.currentTimeMillis();
                    DataSendAndReceive send = new DataSendAndReceive(socket);
                    AgentInfoEntity agentInfoEntity = new AgentInfoEntity(resource);
                    JavaInfoEntity javaInfoEntity = new JavaInfoEntity(resource);

                    String key = createKey(agentInfoEntity);
                    String agentInfoToJson = entityToJson(agentInfoEntity);
                    String javaInfoToJson = entityToJson(javaInfoEntity);

                    TotalData totalData = new TotalData(agentInfoEntity, javaInfoEntity);

                    send.send(key, agentInfoToJson, javaInfoToJson);
                    retryCount = 0;

                    long end = System.currentTimeMillis();
                    long diffTime = end - start;
                    //noinspection BusyWait
                    Thread.sleep(60000 - diffTime);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            } catch (IOException e) {
                log.error("The connection with the server has been lost..");

                // IOException 발생 시 5회 retry
                if (retryCount < 5) {
                    ++retryCount;
                    log.info("IOException error.. retry.. [{}]", retryCount);
                } else {
                    log.info("[{}] failed retry attempts", retryCount);
                    break;
                }
            }
        }
    }

    public static String createKey(AgentInfoEntity agentInfo) throws IOException {
        return agentInfo.getIp() + keyRule + agentInfo.getGateway() + keyRule + agentInfo.getMac_address();
//        return "dsadsadasdasd";
    }

    public static <T> String entityToJson(T entity) {
        return gson.toJson(entity);
    }
}
