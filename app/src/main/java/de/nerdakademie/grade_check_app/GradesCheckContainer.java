package de.nerdakademie.grade_check_app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GradesCheckContainer {

    private final String user;
    private final String pass;
    private String url = "https://cis.nordakademie.de/pruefungsamt/pruefungsergebnisse/?no_cache=1";
    private String url_login = "https://cis.nordakademie.de/?no_cache=1";
    private Map<String, String> oldGrades = new HashMap<>();
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
    private List<String> cookies;
    private CookieManager cookieManager = new CookieManager();

    public GradesCheckContainer(String user, String pass) {
        this.user = user;
        this.pass = pass;
        try {
            checkLoadOldGrades();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void checkForNewGrades() throws Exception {
        login();
        Map<String, String> newGrades = new HashMap<>();
        Map<String, String> grades = getGradesFromInput();
        for (String key : grades.keySet()) {
            String newGrade = grades.get(key);
            if (newGrade.matches("\\s*"))
                continue;
            if (!oldGrades.containsKey(key)) {
                newGrades.put(key, newGrade);
            } else {
                String oldGrade = oldGrades.get(key);
                if (!oldGrade.equalsIgnoreCase(newGrade)) {
                    newGrades.put(key, newGrade);
                }
            }
        }
        oldGrades = grades;
        if (newGrades.size() > 0) {
            String grade = "";
            for (String s : newGrades.keySet())
                grade += s + ": " + newGrades.get(s) + "\n";
            if (grade.contains("*"))
                grade += "\n*) Muendliche Nachpruefung moeglich.";
            // Receiver.instance.functions.sendMessage("Du hast neue Noten:\n" + grade, String.valueOf(userID));
        }
    }

    private void checkLoadOldGrades() throws Exception {
        login();
        oldGrades = getGradesFromInput();
    }

    private void login() throws IOException {
        CookieHandler.setDefault(cookieManager);
        if (this.cookies != null)
            return;
        String page = getPageContent(url_login);
        String postParams = getFormParams(page, user, pass);

        sendPost(url_login, postParams);
    }

    private Map<String, String> getGradesFromInput() throws IOException {
        String content = getPageContent(url);
        HashMap<String, String> ret = new HashMap<>();
        Document doc = Jsoup.parse(content);
        Element loginform = doc.getElementsByClass("table").get(0);
        Elements trElements = loginform.getElementsByTag("tr");
        for (Element trElement : trElements) {
            Elements tdElements = trElement.getElementsByTag("td");
            int i = 0;
            String module = "";
            String grade = "";
            for (Element tdElement : tdElements) {
                if (i == 1) {
                    module = tdElement.html();
                } else if (i == 4) {
                    grade = tdElement.html();
                }
                i++;
            }
            if (grade.contains("(m)")) {
                grade = grade.substring(0, 3) + "*";
            }
            ret.put(module, grade);
        }
        return ret;
    }

    private void sendPost(String url, String postParams) throws IOException {

        URL obj = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

        // Acts like a browser
        conn.setInstanceFollowRedirects(false);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Host", "cis.nordakademie.de");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        // conn.setRequestProperty("Accept",
        // "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        // conn.setRequestProperty("Accept-Language",
        // "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4,eu;q=0.2");
        // conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        // conn.setRequestProperty("Origin", "https://cis.nordakademie.de");
        // conn.setRequestProperty("Connection", "keep-alive");
        // conn.setRequestProperty("Referrer", url_login);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
        conn.setRequestProperty("charset", "utf-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(postParams);
        writer.flush();
        writer.close();

        conn.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        System.out.println(response.toString());

        in.close();
        setCookies(conn.getHeaderFields().get("Set-Cookie"));
    }

    private String getPageContent(String url) throws IOException {

        URL obj = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);

        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en,de-DE,de;q=0.5");

        if (this.cookies != null)
            for (String cookie : this.cookies) {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        conn.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    private String getFormParams(String html, String username, String password) throws UnsupportedEncodingException {

        Document doc = Jsoup.parse(html);

        // Google form id
        Element loginform = doc.getElementsByAttributeValue("action", "/?no_cache=1").get(0);
        Elements inputElements = loginform.getElementsByTag("input");
        List<String> paramList = new ArrayList<>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("user"))
                value = username;
            else if (key.equals("pass"))
                value = password;
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }
        return result.toString();
    }

    private void setCookies(List<String> cookies) {
        if (cookies != null)
            this.cookies = cookies;
    }

}