package nl.ronaldteune.zyxeltool;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Zyxel {
    private final boolean verbose;
    private String cookie = "";
    private String sessionkey = "";

    public Zyxel(boolean verbose) {
        this.verbose = verbose;
    }

    public String doAction(String urlString, String params) throws IOException, ZyxelException {
        return doRequest(urlString, "PUT", params);
    }

    public String login(String loginString) throws IOException, ZyxelException {
        final String urlString = "https://192.168.1.2/UserLogin";
        final String requestMethod = "POST";
        return doRequest(urlString, requestMethod, loginString);
    }

    public String logout() throws IOException, ZyxelException {
        return doRequest("https://192.168.1.2/cgi-bin/UserLogout", "POST","");
    }

    private String doRequest(String urlString, String requestMethod, String params) throws IOException, ZyxelException {
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
        String result;
        result = in.readLine();
        in.close();

        final String checkSessionKey = result.replaceAll(".*?\"sessionkey\":(\\d*).*", "$1");
        if (checkSessionKey.length() > 0 && checkSessionKey.length() < 12) {
            sessionkey = checkSessionKey;
        }
        if (verbose) {
            System.out.println(result);
        }
        if (!result.contains("ZCFG_SUCCESS")) {
            throw new ZyxelException(result);
        }
        return result;
    }
}
