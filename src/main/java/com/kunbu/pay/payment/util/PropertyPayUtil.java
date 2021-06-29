package com.kunbu.pay.payment.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class PropertyPayUtil {

    public static final String PROPERTY_PREFIX = "pay.properties";

    private static Map<String, String> propMap = new HashMap<>();

    static {
        InputStreamReader reader = null;
        try {
            InputStream in = PropertyPayUtil.class.getClassLoader().getResourceAsStream(PROPERTY_PREFIX);
            reader = new InputStreamReader(in);

            Properties prop = new Properties();
            prop.load(reader);

            for (String key : prop.stringPropertyNames()) {
                propMap.put(key, prop.getProperty(key, key));
            }
            log.info(">>> PropertyPayUtil load finish：{}", propMap);
        } catch (Exception e) {
            log.error(">>> PropertyPayUtil load error", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error(">>> PropertiesUtil close resource error", e);
                }
            }
        }
    }

    public static String getValue(String key) {
        if (propMap.containsKey(key)) {
            return propMap.get(key);
        } else {
            return null;
        }
    }

}
