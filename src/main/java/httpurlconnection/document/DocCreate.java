package httpurlconnection.document;

import client.Client;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DocCreate<T> {

//    private final static Logger log = LoggerFactory.getLogger(DocCreate.class);
    static final Logger log = LogManager.getLogger(DocCreate.class);

    public void documentCreate(String indexName, String typeName, T entity) {
        try {
            URL url = new URL(Client.URL + indexName + "/" + typeName);
            try {
                Gson gson = new Gson();
                String toJson = gson.toJson(entity);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                byte[] postDataBytes = toJson.getBytes(StandardCharsets.UTF_8);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();

                outputStream.write(postDataBytes);
                outputStream.flush();
                outputStream.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String data;
                while ((data = reader.readLine()) != null) {
                    log.info(data);
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
