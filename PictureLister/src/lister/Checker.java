/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lister;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Oldřich
 */
public class Checker {

    private String userAgent;
    private int timeout;

    public Checker() {
        this.timeout = 0;
        this.userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)";
    }

    private HttpURLConnection makeConn(String path) throws ProtocolException, IOException {
        String newPath = URLEncoder.encode(path, "UTF-8");
        newPath.replace("\\", "%5C");
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("User-Agent", userAgent);
        con.setReadTimeout(timeout);
        con.setRequestMethod("HEAD");
        con.connect();
        return con;
    }

    public boolean isPicture(String path) {
        try {
            if (path.trim().isEmpty() || path.contains("mailto:")) {
                return false;
            }
            HttpURLConnection con = makeConn(path);
            String type = con.getContentType();
            con.disconnect();
            if (type == null) {
                return false;
            } else {
                return type.contains("image");
            }
        } catch (IOException ex) {
           // Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("^^ " + path);
        }
        return false;
    }

    public boolean isPage(String path) {
        try {
            if (path.trim().isEmpty() || path.contains("mailto:")) {
                return false;
            }
            HttpURLConnection con = makeConn(path);
            String type = con.getContentType();
            con.disconnect();
            if (type == null) {
                return false;
            } else {
                return type.contains("text");
            }
        } catch (IOException ex) {
            //Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("^^ " + path);
        }
        return false;
    }

    public boolean exists(String path) {
        try {
            if (path.trim().isEmpty() || path.contains("mailto:")) {
                return false;
            }
            HttpURLConnection con = makeConn(path);
            int code = con.getResponseCode();
            con.disconnect();
            return (code == (HttpURLConnection.HTTP_OK));
        } catch (IOException ex) {
            //Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("^^ " + path);
        }
        return false;
    }

    

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
