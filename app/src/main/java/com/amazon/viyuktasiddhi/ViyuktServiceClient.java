package com.amazon.viyuktasiddhi;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ViyuktServiceClient {
    private static final String TAG = "ViyuktServiceClient";

    private static final String SCHEME = "https://";
    private static final String HOST = "pahwv-rw-lf.amazon.in";

    private static final String ROOT_PATH_PREFIX = "/h/viyukt";

    private static final String TRANSACTIONS_API = "/transactions";
    private static final String PAYMENT_API = "/payment";
    private ObjectMapper objectMapper;

    private static final Map<String, String> DEFAULT_HEADERS = new HashMap<String, String>() {
        {
            put("accept", "application/json");
            put("accept-charset", "utf-8");
            put("Host", HOST);
            put("User-Agent", "ViyuktServiceClientAndroid");
        }};

    public ViyuktServiceClient() {
        init();
        this.objectMapper = new ObjectMapper();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTransactionsForStore(final String storeId) throws IOException {
        return getTransactions(storeId, "ApayStore");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTransactionsForCustomer(final String customerId) throws IOException {
        return getTransactions(customerId, "ApayCustomer");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getTransactions(final String userId, final String userType) throws IOException {
        final String queryParameterData = "?userId=" + userId + "&userType=" + userType;
        HttpsURLConnection con = createHttpGetCall(TRANSACTIONS_API, queryParameterData);

        return getResponse(con);
    }





    private void init() {
        // this part is needed cause Lebocoin has invalid SSL certificate, that cannot be normally processed by Java
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null; // Not relevant.
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing. Just allow them all.
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing. Just allow them all.
                    }
                }
        };

        HostnameVerifier trustAllHostnames = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true; // Just allow them all.
            }
        };

        try {
            System.setProperty("jsse.enableSNIExtension", "false");
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
        } catch (GeneralSecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private HttpsURLConnection createHttpGetCall(final String api, final String query)
            throws IOException {
        URL obj = new URL(SCHEME + HOST + ROOT_PATH_PREFIX + api + query);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("GET");

        setDefaultHeaders(con);

        return con;
    }

    private String getResponse(final HttpsURLConnection con) throws IOException {
        int responseCode = con.getResponseCode();

        Log.i(TAG, "Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Log.i(TAG, "Response : " + response.toString());

        return response.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDefaultHeaders(HttpsURLConnection con) {
        for (Map.Entry<String, String> entry : DEFAULT_HEADERS.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }
        con.setRequestProperty("Date", Instant.now().toString());
    }
}