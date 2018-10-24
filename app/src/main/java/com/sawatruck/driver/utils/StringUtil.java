package com.sawatruck.driver.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by royal on 8/19/2017.
 */


public class StringUtil {
    /**
     *  Escape Carriage Return
     * @return
     */
    public static String escapeString(String text){
        text = text.replace("\r", "");
        text = text.replace("\n", "");
        text = text.replaceAll("\\\\", "");
        return text;
    }

    public static String getStringFromInputStream(InputStream inputStream){
        StringBuilder textBuilder = new StringBuilder();
        Reader reader = new BufferedReader(new InputStreamReader
                (inputStream));
        {
            int c = 0;
            try {
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return textBuilder.toString();
    }
}

