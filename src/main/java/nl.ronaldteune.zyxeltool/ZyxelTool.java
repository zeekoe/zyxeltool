package nl.ronaldteune.zyxeltool;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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

    public static void main(String[] args) throws IOException {
        boolean verbose = Arrays.asList(args).contains("--verbose") || Arrays.asList(args).contains("-v");
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/etc/zyxel.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String loginString = (String) properties.get("login");

        final Zyxel zyxel = new Zyxel(verbose);

        try {
            zyxel.login(loginString);

            final List<String> commands = Arrays.stream(args).filter(a -> !a.contains("-")).collect(Collectors.toList());

            for (String cmd : commands) {
                final String urlString = (String) properties.get(cmd + ".url");
                final String payLoad = (String) properties.get(cmd + ".payload");
                if (verbose) System.out.println(urlString + " => " + payLoad);
                zyxel.doAction(urlString, payLoad);
            }

            zyxel.logout();
        } catch (ZyxelException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
