/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lister;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Oldřich
 */
public class Page extends Thread {

    public final String PATH;
    private int timeout;
    private String userAgent;
    private Set<String> imgsPaths;
    private Set<String> linkPaths;
    private Set<String> errPaths;
    private String topDomain;
    private boolean keepOnDomain;
    private String domain;
    private boolean leaf;

    public Page(String Path) {
        this.PATH = Path;
        this.timeout = 0;
        this.userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0";
        this.imgsPaths = new HashSet<>();
        this.linkPaths = new HashSet<>();
        this.errPaths = new HashSet<>();
        this.leaf = false;
    }

    private void scanPage() {
        try {
            Document webData;
            webData = Jsoup.connect(PATH).timeout(timeout).followRedirects(true).userAgent(userAgent).get();
            URI uri = new URI(PATH);
            domain = uri.getHost();
            if (keepOnDomain && domain == topDomain) {
                return;
            }
            Element element = webData.body();
            Checker ch = new Checker();
            ch.setTimeout(timeout);
            ch.setUserAgent(userAgent);
            Elements img = element.getElementsByTag("img");
            for (int i = 0; i < img.size(); i++) {
                imgsPaths.add(img.get(i).attr("abs:src"));
            }
            Elements a = element.getElementsByTag("a");
            for (int i = 0; i < a.size(); i++) {
                String attr = a.get(i).attr("abs:href");
                if (ch.exists(attr)) {
                    if (!leaf && ch.isPage(attr)) {
                        try {
                            if (keepOnDomain && !(new URI(attr).getHost().equalsIgnoreCase(topDomain))) {
                            } else {
                                linkPaths.add(attr);
                            }
                        } catch (URISyntaxException ex) {
                            errPaths.add(PATH);
                        }
                    } else if (ch.isPicture(attr)) {
                        imgsPaths.add(attr);
                    }
                }
            }

        } catch (URISyntaxException | IOException ex) {
            errPaths.add(PATH);
        }
    }

    @Override
    public void run() {
        //super.run(); //To change body of generated methods, choose Tools | Templates.
        scanPage();
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getDomain() {
        return domain;
    }

    public String getTopDomain() {
        return topDomain;
    }

    public void setTopDomain(String topDomain) {
        this.topDomain = topDomain;
    }

    public boolean isKeepOnDomain() {
        return keepOnDomain;
    }

    public void setKeepOnDomain(boolean keepOnDomain) {
        this.keepOnDomain = keepOnDomain;
    }

    public Set<String> getErrPaths() {
        return errPaths;
    }

    public void setErrPaths(Set<String> errPaths) {
        this.errPaths = errPaths;
    }

    public void setImgsPaths(Set<String> imgsPaths) {
        this.imgsPaths = imgsPaths;
    }

    public void setLinkPaths(Set<String> linkPaths) {
        this.linkPaths = linkPaths;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPATH() {
        return PATH;
    }

    public Set<String> getImgsPaths() {
        return imgsPaths;
    }

    public Set<String> getLinkPaths() {
        return linkPaths;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public static void main(String[] args) {
        Page p = new Page("http://www.zive.cz");
        p.scanPage();
        
    }
}
