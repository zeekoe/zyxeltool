package nl.ronaldteune.zyxeltool;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class ZyxelTool {

    static {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        } catch (Exception e) {
            throw new RuntimeException("error");
        }
    }
    static String cookie = "";
    static String sessionkey = "";

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/etc/zyxel.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String loginString = (String) properties.get("login");

        login(loginString);

        for (String arg : args) {
            final String urlString = (String) properties.get(arg + ".url");
            final String payLoad = (String) properties.get(arg + ".payload");
            System.out.println(urlString + " => " + payLoad);
            System.out.println(doAction(urlString, payLoad));
        }

        System.out.println(doRequest("https://192.168.1.2/cgi-bin/UserLogout", "POST",""));
    }

    private static String doAction(String urlString, String params) throws IOException {
        return doRequest(urlString, "PUT", params);
    }

    private static void login(String loginString) throws IOException {
        final String urlString = "https://192.168.1.2/UserLogin";
        final String requestMethod = "POST";
        doRequest(urlString, requestMethod, loginString);
    }

    private static String doRequest(String urlString, String requestMethod, String params) throws IOException {
        if (!sessionkey.equals("")) {
            if (urlString.contains("?")) {
                urlString += "&";
            } else {
                urlString += "?";
            }
            urlString += "sessionkey=" + sessionkey;
        }
        URL url = new URL(urlString);
        HttpsURLConnection yc = (HttpsURLConnection) url.openConnection();
        yc.setRequestMethod(requestMethod);
        yc.setRequestProperty("Content-Type", "application/json");
        yc.setDoOutput(true);
        if (!cookie.equals("")) {
            yc.setRequestProperty("Cookie", cookie);
        }
        if (!params.equals("")) {
            DataOutputStream wr = new DataOutputStream(yc.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();
        }

        if (yc.getHeaderFields().containsKey("Set-Cookie")) {
            final String cookieSetter = yc.getHeaderField("Set-Cookie");
            cookie = cookieSetter.replaceAll("(Session=.*?);.*", "$1");;
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine;
        inputLine = in.readLine();
        in.close();

        final String checkSessionKey = inputLine.replaceAll(".*?\"sessionkey\":(\\d*).*", "$1");
        if (checkSessionKey.length() > 0 && checkSessionKey.length() < 12) {
            sessionkey = checkSessionKey;
        }
        return inputLine;
    }
}
