package com.github.realzimboguy.esoslog;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;


public class EsThreadExpiredTimers implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private EsLogRecord esLogs;
    private StringBuilder records;
    private OutputStream os;
    private HttpURLConnection httpCon;
    private URL url;


    public EsThreadExpiredTimers(EsLogRecord esLogs) throws MalformedURLException {
        this.esLogs = esLogs;
        this.url = new URL(EsLog.ES_PROPERTIES.getEsBulkUrl().replaceAll("\"", ""));

    }


    public void run() {
        Thread.currentThread().setName("eslog-EsExpiredTimerProcessor");


        while (true) {

            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //cleanup the map for orphaned timers
                long now = new Date().getTime();

                for (Iterator<Map.Entry<String, Long>> it = esLogs.getTimerList().entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Long> entry = it.next();

                    if ((now - entry.getValue() > esLogs.getTimerAgeMs())) {
                        //this is a feedback log to remove the record and log the timeout with a response time
                        if (EsLog.ES_PROPERTIES.isDebug()) {
                            logger.warn("TIMEOUT identifier :{}", entry.getKey());
                        }
                        EsLog.log(EsLog.LogType.ERROR, "TIMEOUT_WAITING", entry.getKey(), EsLog.TimerType.END, "NO RESPONSE FOR IDENTIFIER :" + entry.getKey());
                        it.remove();
                    }
                }


            } catch (Exception e) {
                logger.error("ERROR", e);
            }


        }
    }


}
