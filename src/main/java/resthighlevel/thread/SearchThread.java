package resthighlevel.thread;

import client.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resthighlevel.indexcreate.AgentInfoIndexCreate;
import resthighlevel.search.Search;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchThread extends Thread {

    static final Logger log = LogManager.getLogger(SearchThread.class);

    @Override
    public void run() {
        while (true) {
            try {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String typeName = sdf.format(date);
                InsertThread insertThread = new InsertThread();
                AgentInfoIndexCreate create = new AgentInfoIndexCreate();

                Search search = new Search();
                search.dataPerMin(Client.AGENT_INFO_INDEX, typeName);
                search.dataPerMin(Client.JAVA_INFO_INDEX, typeName);

                Thread.sleep(59340);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }
}
