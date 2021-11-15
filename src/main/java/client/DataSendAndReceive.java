package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DataSendAndReceive {

    static Logger log = LogManager.getLogger(DataSendAndReceive.class);
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private final static String RESPONSE_OK = "ok";
    private final static String OK = "OK";

    public DataSendAndReceive(Socket socket) {
        this.socket = socket;
    }

    public void send(String key, String agentInfo, String javaInfo) throws IOException {
        writer = new PrintWriter(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        dataSend(key); // 키 값 전송
        log.info("send key to server");

        if (dataReceive(reader).equals(RESPONSE_OK)) { // ok 응답 시
            log.info("[{}] key authentication success !!", OK);
            log.info("agentInfo sending data ...");
            dataSend(agentInfo); // agentInfo 전송
            log.info("[{}] agentInfo sent success", OK);

            if (dataReceive(reader).equals(RESPONSE_OK)) { // agentInfo 전송 성공
                log.info("javaInfo sending data ...");
                dataSend(javaInfo);
                log.info("[{}] javaInfo sent success", OK);

                if (dataReceive(reader).equals(RESPONSE_OK)) {
                    log.info("[{}] All data send completed !!", OK);
                }
            }
        } else if (dataReceive(reader).equals("key authentication failed")){ // 키 인증 실패
            log.error("key authentication failed..");
            writer.close();
            reader.close();
        } else if (dataReceive(reader).equals("incorrect key")) { // 정확한 키 값이 아님
            log.error("Incorrect key between server and client.");
            writer.close();
            reader.close();
        }
    }

    // 데이터 전송
    public void dataSend(String sendData) {
        writer.println(sendData);
        writer.flush();
    }

    // 데이터 수신
    public String dataReceive(BufferedReader reader) throws IOException {
        return reader.readLine();
    }
}
