/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lister;

import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Oldřich
 */
public class Picture {

    private final String PATH;

    public Picture(String filePath) {
        this.PATH = filePath;
    }

    public Dimension vratRozliseni() {
        try {
            URL url = new URL("http://.jpg");
            SimpleImageInfo s = new SimpleImageInfo(url.openStream());
            return new Dimension(s.getWidth(), s.getHeight());
        } catch (IOException ex) {
            Logger.getLogger(Picture.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getFilePath() {
        return PATH;
    }

    public static void main(String[] args) {
        Picture p = new Picture("http://.jpg");
        System.out.println(p.vratRozliseni());

    }
}
