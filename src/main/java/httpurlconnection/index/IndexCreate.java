package httpurlconnection.index;

import client.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IndexCreate {

    private BufferedReader reader = null;
//    private final static Logger log = LoggerFactory.getLogger(IndexCreate.class);
    static final Logger log = LogManager.getLogger(IndexCreate.class);

    /** 인덱스 생성 */
    public void indexCreate(String indexName, String typeName, String settingFilePath, int shard, int replica) {
        try {
            URL url = new URL(Client.URL + indexName); // 호출할 URL
            try {
                String toJson = indexSettings(typeName, settingFilePath, shard, replica); // settings, mappings
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                byte[] postDataBytes = toJson.getBytes(StandardCharsets.UTF_8);

                // 헤더 설정
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();

                outputStream.write(postDataBytes);  // put 메서드 호출
                outputStream.flush();
                outputStream.close();

                reader= new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String data;
                while ((data = reader.readLine()) != null) {
                    log.info(data);
                }
                reader.close();
            } catch (IOException e) {
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String indexSettings(String typeName, String path, int shard, int replica) {
        String toJson = null;
        try {
            reader = new BufferedReader(new FileReader(path));

            String setData;
            toJson = "{\n" +
                    "    \"settings\" : {\n" +
                    "        \"number_of_shards\":" + shard + ",\n" +
                    "        \"number_of_replicas\" :" + replica + "\n" +
                    "    },\n" +
                    "\n" +
                    "    \"mappings\" : {\n" +
                    "\t\"" + typeName + "\"" + " : {\n\t\t";

            while ((setData = reader.readLine()) != null) {
                toJson += setData + "\n";
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toJson;
    }

}
