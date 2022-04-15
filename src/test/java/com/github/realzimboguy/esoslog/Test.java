package com.github.realzimboguy.esoslog;

//import com.fasterxml.uuid.Generators;


import java.net.MalformedURLException;
import java.util.Date;
import java.util.UUID;

public class Test {



    public static void main(String[] args) throws MalformedURLException, InterruptedException {

        new EsLog(true,new EsProperty()
                .setEsBulkUrl("http://localhost:9200/_bulk") //es
//                .setEsBulkUrl("https://localhost:9200/_bulk") //os
                .setUsername("elastic")  //es
                .setPassword("FXl7CjgKFDjC0r5S29au")  //es
//                .setUsername("admin") //os
//                .setPassword("admin") //os
                .setTimerAgeSeconds(5)
                .setDebug(true)
                .setDisableSslChecking(true) //os
        );

        long before = new Date().getTime();
        for (int i = 0; i < 10; i++) {

            System.out.println(generatecombUUID());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long after = new Date().getTime();
        System.out.println("dif:" + (after - before));


        Other other = new Other();

        other.doSomething();

        EsLog.log(EsLog.LogType.MSG,"INBOUND","asdfasdf" , EsLog.TimerType.START,"sd");

        Thread.sleep(1000);
        EsLog.log(EsLog.LogType.MSG,"PROCESSING","asdfasdf" , EsLog.TimerType.LOG,"sd");

        Thread.sleep(1300);
        EsLog.log(EsLog.LogType.MSG,"OUTBOUND","asdfasdf" , EsLog.TimerType.END,"sd");


//        while (true) {
//
//            for (int i = 0; i < 5000; i++) {
//                EsLog.log(EsLog.LogType.MSG,"OUTGOING_REQUEST", UUID.randomUUID().toString());
//            }
//            Thread.sleep(5000);
//
//
//        }

    }

    public static String generatecombUUID(){

        return new Date().getTime() + UUID.randomUUID().toString().substring(13);

    }


}
