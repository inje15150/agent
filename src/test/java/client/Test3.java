package client;

import com.google.gson.Gson;
import entity.AgentInfoEntity;
import entity.JavaInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resource.ResourceExtraction;

import java.io.IOException;
import java.net.Socket;

public class Test3 {

    static Logger log = LogManager.getLogger(Test3.class);
    public final static int PORT = 9200;
    public final static int SOCKET_PORT = 8080;
    public final static String URL = "http://192.168.60.80:" + PORT + "/";
    public final static String AGENT_INFO_INDEX = "agent_info";
    public final static String JAVA_INFO_INDEX = "java_info";
    private final static String keyRule = "%%#!";
    private final static String splitKeyWord = "&&&";
    public static Gson gson = new Gson();

//    public final static String AGENT_SETTINGS_PATH = "C:\\Users\\joseph\\IdeaProjects\\resource\\src\\main\\java\\httpurlconnection\\index\\agentindex_settings";
//    public final static String JAVA_SETTINGS_PATH = "C:\\Users\\joseph\\IdeaProjects\\resource\\src\\main\\java\\httpurlconnection\\index\\javaindex_settings";

    public static void main(String[] args) {
        Socket socket = null;
        ResourceExtraction resource = new ResourceExtraction();

        try {
            socket = new Socket(resource.getIp(), SOCKET_PORT);

            while (true) {
                DataSendAndReceive send = new DataSendAndReceive(socket);
                AgentInfoEntity agentInfoEntity = new AgentInfoEntity(resource);
                JavaInfoEntity javaInfoEntity = new JavaInfoEntity(resource);

                String key = createKey(resource);
                String agentInfoToJson = entityToJson(agentInfoEntity);
                String javaInfoToJson = entityToJson(javaInfoEntity);

                send.send(key, agentInfoToJson, javaInfoToJson);

//                Thread.sleep(2000);
                Thread.sleep(59420);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static String createKey(ResourceExtraction resource) throws IOException {
//        return resource.getIp() + keyRule + resource.getGateway() + keyRule + resource.getMacAddress();
        return "dsadsadasdasd";
    }

    public static <T> String entityToJson(T entity) {
        return gson.toJson(entity);
    }
}
