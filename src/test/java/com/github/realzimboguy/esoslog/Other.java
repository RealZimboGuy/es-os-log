package com.github.realzimboguy.esoslog;

import java.util.HashMap;
import java.util.Map;

public class Other {


    public void doSomething() throws InterruptedException {

//        EsLog.log(EsLog.LogType.INFO,"SYSTEM","In Business people");

        EsLog.log(EsLog.LogType.MSG,"MESSAGE_TO_REMOTE","w1528534502353-4902-aba7-11909e8e39b1");

        Map<String,Object> testMap = new HashMap<String, Object>();

        testMap.put("F2","123456789");
        testMap.put("F3","123456789");
        testMap.put("F4","123456789");
        testMap.put("F5","123456789");

        EsLog.log(EsLog.LogType.MSG,"MESSAGE FROM REMOTE",testMap);




    }


}
