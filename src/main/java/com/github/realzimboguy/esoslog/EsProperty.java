package com.github.realzimboguy.esoslog;

public class EsProperty {

    private String esBulkUrl;
    private String indexName;
    private String indexDatePattern;
    private String indexType;
    private String indexTemplate;
    private String serverName;
    private String indexDate;
    private String esProperties;
    private int processSizeLimit;
    private String username;
    private String password;
    private int batchProcessSize;
    private int timerAgeSeconds;
    private boolean enabled;
    private boolean debug;
    private boolean disableSslChecking;

    public EsProperty() {

        //defaults
        setIndexType("_doc");
        setServerName("myserver");
        setIndexDatePattern("yyyy.MM.dd");
        setIndexName("log");
        setProcessSizeLimit(5000);
        setBatchProcessSize(2000);
        setTimerAgeSeconds(60);
        setIndexTemplate("{\n" +
                "  \"index_patterns\": [\n" +
                "    \""+getIndexName()+"*\"\n" +
                "  ],\n" +
                "  \"settings\": {\n" +
                "    \"number_of_shards\": 5,\n" +
                "    \"number_of_replicas\": 0\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"@timestamp\": {\n" +
                "        \"type\": \"date\",\n" +
                "        \"format\": \"epoch_millis\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");
    }

    public int getTimerAgeSeconds() {
        return timerAgeSeconds;
    }

    public EsProperty setTimerAgeSeconds(int timerAgeSeconds) {
        this.timerAgeSeconds = timerAgeSeconds;
        return this;
    }

    public int getProcessSizeLimit() {
        return processSizeLimit;
    }

    public EsProperty setProcessSizeLimit(int processSizeLimit) {
        this.processSizeLimit = processSizeLimit;
        return this;
    }

    public String getEsProperties() {
        return esProperties;
    }

    protected EsProperty setEsProperties(String esProperties) {
        this.esProperties = esProperties;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected EsProperty setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getEsBulkUrl() {
        return esBulkUrl;
    }

    public EsProperty setEsBulkUrl(String esBulkUrl) {
        this.esBulkUrl = esBulkUrl;
        return this;
    }

    public String getIndexName() {
        return indexName;
    }

    public EsProperty setIndexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    public String getIndexDatePattern() {
        return indexDatePattern;
    }

    public EsProperty setIndexDatePattern(String indexDatePattern) {
        this.indexDatePattern = indexDatePattern;
        return this;
    }

    public String getIndexType() {
        return indexType;
    }

    public EsProperty setIndexType(String indexType) {
        this.indexType = indexType;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public EsProperty setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getIndexDate() {
        return indexDate;
    }

    protected EsProperty setIndexDate(String indexDate) {
        this.indexDate = indexDate;
        return this;
    }

    public int getBatchProcessSize() {
        return batchProcessSize;
    }

    public EsProperty setBatchProcessSize(int batchProcessSize) {
        this.batchProcessSize = batchProcessSize;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public EsProperty setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public EsProperty setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public EsProperty setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getIndexTemplate() {
        return indexTemplate;
    }

    public EsProperty setIndexTemplate(String indexTemplate) {
        this.indexTemplate = indexTemplate;
        return this;
    }

    public boolean isDisableSslChecking() {
        return disableSslChecking;
    }

    public EsProperty setDisableSslChecking(boolean disableSslChecking) {
        this.disableSslChecking = disableSslChecking;
        return this;
    }
}
