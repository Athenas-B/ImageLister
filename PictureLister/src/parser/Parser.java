/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Oldřich
 */
public class Parser {

    private final String address;
    private StringBuilder content;

    public Parser(String address) {
        this.address = address;
    }

    public void test() {
        readHtml(address);
        System.out.println(listTags("a"));
        System.out.println(listTags("img"));
    }

    private void readHtml(String urlToSeach) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(urlToSeach).openConnection().getInputStream()))) {
            content = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    private List<String> listTags(String tagName) {
        List<String> list = new ArrayList();
        int from = 0;
        int to;
        do {
            from = content.indexOf("<" + tagName, from);
            to = content.indexOf(">", from + 1);
            if (from != -1) {
                list.add(content.substring(from, to));
            }
        } while (from != -1);
        return list;
    }

    public static void main(String[] args) {
        Parser p = new Parser("http://www.html");
        p.test();
    }
}
