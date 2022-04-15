package com.github.realzimboguy.esoslog;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;


public class EsThreadProcessor implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private EsLogRecord esLogs;
    private StringBuilder records;
    private OutputStream os;
    private HttpURLConnection httpCon;
//    private HttpsURLConnection httpCon;
    private URL url;
    private String authenticationHeader;


    public EsThreadProcessor(EsLogRecord esLogs) throws MalformedURLException {
        this.esLogs = esLogs;
        this.url = new URL(EsLog.ES_PROPERTIES.getEsBulkUrl().replaceAll("\"", ""));

        EsLog.ES_PROPERTIES.setEsProperties("{ \"index\" : { \"_index\" : \"" + EsLog.ES_PROPERTIES.getIndexName() + "-" + EsLog.ES_PROPERTIES.getIndexDate() + "\", \"_type\" : \"" + EsLog.ES_PROPERTIES.getIndexType() + "\" } }");

        if (EsLog.ES_PROPERTIES.getUsername() != null) {
            authenticationHeader = "Basic " + Base64.getEncoder().encodeToString((EsLog.ES_PROPERTIES.getUsername() + ":" + EsLog.ES_PROPERTIES.getPassword()).getBytes());
        }

    }

    public String getResponse(InputStream i) throws IOException {
        String res = "";
        InputStreamReader in = new InputStreamReader(i);
        BufferedReader br = new BufferedReader(in);
        String output;
        while ((output = br.readLine()) != null) {
            res += (output);
        }

        return res;
    }

    public void run() {
        Thread.currentThread().setName("eslog-EsThreadProcessor");
        trustAllHosts();

        logger.debug("Starting ESOS thread processor");

        try {
            performMappingUpdate();
        } catch (IOException e) {
            logger.error("Error",e);
        }


        while (true) {

            try {


                if (esLogs.size() <= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                records = new StringBuilder();

                int size = 0;
                while (esLogs.size() > 0) {
                    if (size > EsLog.ES_PROPERTIES.getBatchProcessSize()) {
                        break;
                    }
                    records.append(EsLog.ES_PROPERTIES.getEsProperties()).append("\n").append(esLogs.removeFirst()).append("\n");
                    size++;
                }


                httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                if (authenticationHeader != null) {
                    httpCon.setRequestProperty("Authorization", authenticationHeader);
                }
                httpCon.setRequestMethod("POST");
                httpCon.setDoOutput(true);
                os = httpCon.getOutputStream();
                os.write(records.toString().getBytes("UTF-8"));
                os.close();
                httpCon.getResponseCode();
                if (EsLog.ES_PROPERTIES.isDebug()) {
                    logger.info("Http Response :{}", httpCon.getResponseCode());
                    if (httpCon.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                        logger.info("Http Response Body :{}", getResponse(httpCon.getInputStream()));
                    } else {
                        /* error from server */
                        logger.warn("Http Response Body :{}", getResponse(httpCon.getInputStream()));
                    }

                }
                httpCon.disconnect();



            } catch (Exception e) {

                logger.warn("ERROR", e);
            }
            records = null;


        }
    }

    private void performMappingUpdate() throws IOException {
        logger.info("performMappingUpdate");
        httpCon = (HttpURLConnection) new URL(EsLog.ES_PROPERTIES.getEsBulkUrl().replaceAll("\"", "")
                .replace("_bulk", "_template/esoslog")).openConnection();
        httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        if (authenticationHeader != null) {
            httpCon.setRequestProperty("Authorization", authenticationHeader);
        }
        httpCon.setRequestMethod("PUT");
        httpCon.setDoOutput(true);
        os = httpCon.getOutputStream();
        os.write(EsLog.ES_PROPERTIES.getIndexTemplate().getBytes("UTF-8"));
        os.close();
        httpCon.getResponseCode();

        logger.info("Http Response :{}", httpCon.getResponseCode());
        if (logger.isDebugEnabled()){
            if (httpCon.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                logger.info("Http Response Body :{}", getResponse(httpCon.getInputStream()));
            } else {
                /* error from server */
                logger.warn("Http Response Body :{}", getResponse(httpCon.getInputStream()));
            }
        }


        httpCon.disconnect();


    }

    public void trustAllHosts()
    {
        try
        {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509ExtendedTrustManager()
                    {
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers()
                        {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                        {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                        {
                        }


                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string, Socket socket) throws CertificateException
                        {

                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

                        }

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException
                        {

                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException
                        {

                        }

                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new  HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        }
        catch (Exception e)
        {
            logger.error("Error occurred",e);
        }
    }


}
