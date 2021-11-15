package httpurlconnection.search;

import client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataSearch {

    private URL url;
    private OutputStream output = null;
    private BufferedReader reader = null;
    private HttpURLConnection connection = null;
    private final static String COMMAND = "/_search?pretty";

    public void searchCondition() {
        try {
            url = new URL(Client.URL + COMMAND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchCondition(String indexName) {
        try {
            url = new URL(Client.URL + indexName + COMMAND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchCondition(String indexName, String typeName) {
        try {
            url = new URL(Client.URL + indexName + "/" + typeName + COMMAND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String response() {
        String response = null;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String data;
            while ((data = reader.readLine()) != null) {
                response += data + "\n";
//                System.out.println(data);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void connection() {
        try {
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
//            connection.setRequestProperty("Content-Length", String.valueOf(queryToJsonBytes.length));
            connection.setDoOutput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
