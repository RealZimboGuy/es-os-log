package com.github.realzimboguy.esoslog;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EsThreadDateChecker implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String pattern;
    private SimpleDateFormat sdf;

    public EsThreadDateChecker(String pattern) {
        this.pattern = pattern;
        sdf = new SimpleDateFormat(pattern);
    }

    public void run() {

        Thread.currentThread().setName("eslog-EsThreadDateChecker");
        while (true) {
            try {

                checkDate();
                Thread.sleep(60 * 1000);

            } catch (Exception e) {

                logger.error("ERROR",e);
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    private void checkDate() {

        String newDate = sdf.format(new Date());

        if (EsLog.ES_PROPERTIES.getIndexDate() == null ){
            EsLog.ES_PROPERTIES.setIndexDate(newDate);

        }else if (! EsLog.ES_PROPERTIES.getIndexDate().equals(newDate)){
            logger.debug("update current index date");
            EsLog.ES_PROPERTIES.setIndexDate(newDate);
            EsLog.ES_PROPERTIES.setEsProperties("{ \"index\" : { \"_index\" : \"" + EsLog.ES_PROPERTIES.getIndexName()+"-"+EsLog.ES_PROPERTIES.getIndexDate() + "\", \"_type\" : \"" + EsLog.ES_PROPERTIES.getIndexType() + "\" } }");

        }

    }


}
