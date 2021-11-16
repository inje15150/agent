package client;

import com.google.gson.Gson;
import entity.AgentInfoEntity;
import entity.JavaInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import resource.ResourceExtraction;

import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Test3 {

    final static Logger log = LogManager.getLogger(Test3.class);

    public static void main(String[] args) {

            Timer timer = new Timer();

            TimerTask timerTask = new TimerTask() {

                @Override
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    log.info(df.format(calendar.getTime()));
                }
            };
            timer.schedule(timerTask,0,60000);
    }
}
