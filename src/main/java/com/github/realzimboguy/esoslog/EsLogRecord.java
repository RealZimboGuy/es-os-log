package com.github.realzimboguy.esoslog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EsLogRecord {

    private ConcurrentLinkedQueue loglist;
    private int sizeLimit;
    private ConcurrentHashMap<String,Long> timerList;
    private int timerAgeMs;

    //this is in its own class to make it thread safe

    public EsLogRecord(int sizeLimit, int timerAgeSeconds) {
        this.loglist = new ConcurrentLinkedQueue();
        this.timerList = new ConcurrentHashMap<String, Long>();
        this.sizeLimit = sizeLimit;
        this.timerAgeMs = timerAgeSeconds * 1000;

    }

    public int size() {
        return loglist.size();
    }

    public Object removeFirst () {
        return loglist.poll();
    }

    public void add(String value) {
        if (loglist.size()> sizeLimit){
            loglist.poll();
        }
        loglist.add(value);
    }

    public void addTimer(String value, long time) {
        timerList.put(value, time);

    }

    public Long getTimer (String value){
        return timerList.get(value);
    }

    public Long removeTimer (String value){
        return timerList.remove(value);
    }


    public int getTimerListSize() {
        return timerList.size();
    }

    public ConcurrentHashMap<String, Long> getTimerList() {
        return timerList;
    }

    public int getTimerAgeMs() {
        return timerAgeMs;
    }
}
