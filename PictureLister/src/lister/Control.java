/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lister;

import java.awt.Dimension;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Oldřich
 */
public class Control {

    public final String PATH;
    private int levels;
    private int timeout;
    private boolean keepOnDomain;
    private String domain;
    private String userAgent;
    private Set<String> imgsPaths;
    private Set<String> linkPaths;
    private Set<String> errPaths;
    private Set<String> scannedPaths;

    public Control(String PATH) {
        this.PATH = PATH;
        this.levels = 2;
        this.timeout = 0;
        this.keepOnDomain = true;
        this.userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0";
        this.domain = new String();
        //clearResults();
    }

    public void Scan() throws InterruptedException, URISyntaxException {
        List<Page> threads = new LinkedList<>();
        clearResults();
        URI uri = new URI(PATH);
        domain = uri.getHost();
        Page p = new Page(PATH);
        configPage(p);
        p.start();
        p.join();
        scannedPaths.add(PATH);
        linkPaths.removeAll(scannedPaths);
        linkPaths.removeAll(errPaths);
        imgsPaths.removeAll(scannedPaths);
        for (int deep = 0; deep < levels; deep++) {
            Set<String> tempLinks = Collections.synchronizedSet(new HashSet<>(linkPaths));
            for (String path : linkPaths) {
                uri = new URI(path);
                p = new Page(path);
                configPage(p, tempLinks);
                if (deep + 1 == levels) {
                    p.setLeaf(true);
                }
                p.start();
                threads.add(p);
            }
            System.out.println("Threads:" + threads.size());
            for (Page thread : threads) {
                thread.join();
                scannedPaths.add(thread.getPATH());
            }
            threads.clear();

            linkPaths.addAll(tempLinks);
            linkPaths.removeAll(scannedPaths);
            imgsPaths.removeAll(scannedPaths);
            linkPaths.removeAll(errPaths);
        }
    }

    public List<Picture> limitTo(Dimension size) {
        List<Picture> list = new ArrayList<>(10);
        for (String imgsPath : imgsPaths) {
            Picture picture = new Picture(imgsPath);
            Dimension pr = picture.vratRozliseni();
            if (pr != null && ((pr.height >= size.height && pr.width >= size.height) || (pr.height >= size.width && pr.width >= size.height))) {
                list.add(picture);
            }
        }
        return list;
    }

    private void configPage(Page p) {
        configPage(p, linkPaths);
    }

    private void configPage(Page p, Set<String> tempLinks) {
        p.setImgsPaths(imgsPaths);
        p.setErrPaths(errPaths);
        p.setLinkPaths(tempLinks);
        p.setTimeout(timeout);
        p.setUserAgent(userAgent);
        p.setTopDomain(domain);
        p.setKeepOnDomain(keepOnDomain);

    }

    public final void clearResults() {
        this.imgsPaths = Collections.synchronizedSet(new HashSet<>());
        this.linkPaths = Collections.synchronizedSet(new HashSet<>());
        this.errPaths = Collections.synchronizedSet(new HashSet<>());
        this.scannedPaths = Collections.synchronizedSet(new HashSet<>());
    }

    public boolean isKeepOnDomain() {
        return keepOnDomain;
    }

    public void setKeepOnDomain(boolean keepOnDomain) {
        this.keepOnDomain = keepOnDomain;
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public static void main(String[] args) {
        Control c = new Control("http://www.zive.cz/");
        c.setLevels(1);
        c.setKeepOnDomain(true);
        c.setTimeout(3000);
        Dimension dim = new Dimension(300, 300);
        try {
            c.Scan();
        } catch (InterruptedException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(c.getImgsPaths());
        System.out.println(c.getImgsPaths().size());
        System.out.println("*************");
        System.out.println(c.getLinkPaths());
        System.out.println(c.getLinkPaths().size());

        System.out.println("*************");
        System.out.println(c.limitTo(dim));
        System.out.println();
    }
}
