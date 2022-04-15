package com.github.realzimboguy.esoslog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class EsLog {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static EsLogRecord esLogRecords = null;
    protected static EsProperty ES_PROPERTIES;
    private EsThreadProcessor esThreadProcessor;
    private EsThreadExpiredTimers expiredTimers;
    private EsThreadDateChecker esThreadDateChecker;

    public EsLog(boolean enabled, EsProperty esProperties) throws MalformedURLException {

        esProperties.setEnabled(enabled);
        ES_PROPERTIES = esProperties;

        if (!esProperties.isEnabled()) {
            return;
        }

        //we dont allow this to be too small
        if (esProperties.getTimerAgeSeconds() < 5) {
            logger.warn("Changed the configured timer age as it was configured to low {}, set to:{}", esProperties.getTimerAgeSeconds(), 30);
        }

        esLogRecords = new EsLogRecord(esProperties.getProcessSizeLimit(), esProperties.getTimerAgeSeconds());

        esThreadDateChecker = new EsThreadDateChecker(esProperties.getIndexDatePattern());
        new Thread(esThreadDateChecker).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        esThreadProcessor = new EsThreadProcessor(esLogRecords);
        new Thread(esThreadProcessor).start();

        new Thread(new EsThreadExpiredTimers(esLogRecords)).start();


    }


    public static void log(LogType logType, String message, String details) {

        if (!ES_PROPERTIES.isEnabled()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(Util.singleQuote).append(EsKey.TIMESTAMP).append(Util.singleQuote).append(Util.colon)
                .append(new Date().getTime())
                .append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.SERVERNAME).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(ES_PROPERTIES.getServerName())
                .append(Util.singleQuote).append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.LOGTYPE).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(logType)
                .append(Util.singleQuote).append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.MESSAGE).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(message)
                .append(Util.singleQuote).append(Util.delimiter);

        sb.append(Util.singleQuote).append(EsKey.DETAILS).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(details)
                .append(Util.singleQuote);
        sb.append("}");

        esLogRecords.add(sb.toString());

    }


    public static void log(LogType logType, String message, String identifier, TimerType timerType, String details) {

        if (!ES_PROPERTIES.isEnabled()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        long now = new Date().getTime();
        sb.append("{");
        sb.append(Util.singleQuote).append(EsKey.TIMESTAMP).append(Util.singleQuote).append(Util.colon)
                .append(now)
                .append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.SERVERNAME).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(ES_PROPERTIES.getServerName())
                .append(Util.singleQuote).append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.LOGTYPE).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(logType)
                .append(Util.singleQuote).append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.MESSAGE).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(message)
                .append(Util.singleQuote).append(Util.delimiter);
        if (identifier != null) {
            sb.append(Util.singleQuote).append(EsKey.IDENTIFIER).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                    .append(identifier)
                    .append(Util.singleQuote).append(Util.delimiter);

            if (timerType == TimerType.START) {
                //new record add the timer
                esLogRecords.addTimer(identifier, now);
            } else if (timerType != TimerType.NOTHING) {

                Long timer = null;
                if (timerType == TimerType.END) {
                    timer = esLogRecords.removeTimer(identifier);
                } else if (timerType == TimerType.LOG) {
                    timer = esLogRecords.getTimer(identifier);
                }
                if (timer != null) {
                    sb.append(Util.singleQuote).append(EsKey.RSPTIME).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                            .append(now - timer)
                            .append(Util.singleQuote).append(Util.delimiter);
                }


            }
        }
        sb.append(Util.singleQuote).append(EsKey.DETAILS).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(details.replaceAll("[^a-zA-Z0-9 *-_]()", " "))
                .append(Util.singleQuote);
        sb.append("}");

        esLogRecords.add(sb.toString());

    }

    public static void log(LogType logType, String message, Map<String, Object> map) {
        log(logType, message, null, TimerType.NOTHING, map);
    }

    public static void log(LogType logType, String message, String identifier, TimerType timerType, Map<String, Object> map) {

        if (!ES_PROPERTIES.isEnabled()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        long now = new Date().getTime();
        sb.append("{");
        sb.append(Util.singleQuote).append(EsKey.TIMESTAMP).append(Util.singleQuote).append(Util.colon)
                .append(new Date().getTime())
                .append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.SERVERNAME).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(ES_PROPERTIES.getServerName())
                .append(Util.singleQuote).append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.LOGTYPE).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(logType)
                .append(Util.singleQuote).append(Util.delimiter);

        if (identifier != null) {
            sb.append(Util.singleQuote).append(EsKey.IDENTIFIER).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                    .append(identifier)
                    .append(Util.singleQuote).append(Util.delimiter);

            if (timerType == TimerType.START) {
                //new record add the timer
                esLogRecords.addTimer(identifier, now);
            } else if (timerType != TimerType.NOTHING) {

                Long timer = null;
                if (timerType == TimerType.END) {
                    timer = esLogRecords.removeTimer(identifier);
                } else if (timerType == TimerType.LOG) {
                    timer = esLogRecords.getTimer(identifier);
                }
                if (timer != null) {
                    sb.append(Util.singleQuote).append(EsKey.RSPTIME).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                            .append(now - timer)
                            .append(Util.singleQuote).append(Util.delimiter);
                }


            }
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Long || entry.getValue() instanceof Double || entry.getValue() instanceof Integer) {
                sb.append(Util.singleQuote).append(entry.getKey()).append(Util.singleQuote).append(Util.colon)
                        .append((String.valueOf(entry.getValue())).replace("\"", ""))
                        .append(Util.delimiter);
            } else if (entry.getValue() instanceof LinkedHashMap) {

                LinkedHashMap<String, Object> subMap = ((LinkedHashMap) entry.getValue());

                for (Map.Entry<String, Object> subEntry : subMap.entrySet()) {
                    sb.append(Util.singleQuote).append(entry.getKey()).append("_").append(subEntry.getKey()).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                            .append((String.valueOf(subEntry.getValue())).replace("\"", ""))
                            .append(Util.singleQuote).append(Util.delimiter);
                }

            } else {

                if (entry.getValue() != null) {
                    sb.append(Util.singleQuote).append(entry.getKey()).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                            .append(((String) entry.getValue()).replace("\"", ""))
                            .append(Util.singleQuote).append(Util.delimiter);
                }
            }
        }

        sb.append(Util.singleQuote).append(EsKey.MESSAGE).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(message)
                .append(Util.singleQuote);


        sb.append("}");

        esLogRecords.add(sb.toString());

    }

    public static void log(LogType logType, String message, Map<String, Object> map, Date date) {
        log(logType, message, null, TimerType.NOTHING, map, date);
    }

    public static void log(LogType logType, String message, String identifier, TimerType timerType, Map<String, Object> map, Date date) {

        if (!ES_PROPERTIES.isEnabled()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        long now = new Date().getTime();
        sb.append("{");
        sb.append(Util.singleQuote).append(EsKey.TIMESTAMP).append(Util.singleQuote).append(Util.colon)
                .append(date.getTime())
                .append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.SERVERNAME).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(ES_PROPERTIES.getServerName())
                .append(Util.singleQuote).append(Util.delimiter);
        sb.append(Util.singleQuote).append(EsKey.LOGTYPE).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(logType)
                .append(Util.singleQuote).append(Util.delimiter);

        if (identifier != null) {
            sb.append(Util.singleQuote).append(EsKey.IDENTIFIER).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                    .append(identifier)
                    .append(Util.singleQuote).append(Util.delimiter);

            if (timerType == TimerType.START) {
                //new record add the timer
                esLogRecords.addTimer(identifier, now);
            } else if (timerType != TimerType.NOTHING) {

                Long timer = null;
                if (timerType == TimerType.END) {
                    timer = esLogRecords.removeTimer(identifier);
                } else if (timerType == TimerType.LOG) {
                    timer = esLogRecords.getTimer(identifier);
                }
                if (timer != null) {
                    sb.append(Util.singleQuote).append(EsKey.RSPTIME).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                            .append(now - timer)
                            .append(Util.singleQuote).append(Util.delimiter);
                }


            }
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Long) {
                sb.append(Util.singleQuote).append(entry.getKey()).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                        .append((String.valueOf(entry.getValue())).replace("\"", ""))
                        .append(Util.singleQuote).append(Util.delimiter);
            } else if (entry.getValue() instanceof LinkedHashMap) {

                LinkedHashMap<String, Object> subMap = ((LinkedHashMap) entry.getValue());

                for (Map.Entry<String, Object> subEntry : subMap.entrySet()) {
                    sb.append(Util.singleQuote).append(entry.getKey() + "_" + subEntry.getKey()).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                            .append((String.valueOf(subEntry.getValue())).replace("\"", ""))
                            .append(Util.singleQuote).append(Util.delimiter);
                }

            } else {

                if (entry.getValue() != null) {
                    sb.append(Util.singleQuote).append(entry.getKey()).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                            .append(((String) entry.getValue()).replace("\"", ""))
                            .append(Util.singleQuote).append(Util.delimiter);
                }
            }
        }

        sb.append(Util.singleQuote).append(EsKey.MESSAGE).append(Util.singleQuote).append(Util.colon).append(Util.singleQuote)
                .append(message)
                .append(Util.singleQuote);


        sb.append("}");

        esLogRecords.add(sb.toString());

    }

    public static class EsKey {
        public static final String MESSAGE = "message";
        public static final String TIMESTAMP = "@timestamp";
        public static final String LOGTYPE = "logType";
        public static final String IDENTIFIER = "identifier";
        public static final String RSPTIME = "rspTime";
        public static final String SERVERNAME = "serverName";
        public static final String DETAILS = "details";

    }

    public static enum LogType {
        MSG,
        DEBUG,
        INFO,
        WARNING,
        ERROR
    }

    public static enum TimerType {
        NOTHING,
        START,
        LOG,
        END
    }

}
