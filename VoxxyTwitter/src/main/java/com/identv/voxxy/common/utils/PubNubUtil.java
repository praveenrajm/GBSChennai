package com.identv.voxxy.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Praveen on 31-12-2015.
 */
public class PubNubUtil {
    final String PROPERTIES_FILE_NAME;


    public PubNubUtil(String propertiesFilePath) {
        PROPERTIES_FILE_NAME = propertiesFilePath;
    }

    public String getProperty(String key) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = PubNubUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
