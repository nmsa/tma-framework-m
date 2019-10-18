package eu.atmosphere.tmaf.probe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {

    private static PropertiesManager instance = null;
    private static Properties props = null;

    public static final int INVALID_INTEGER_ERROR_CODE = Integer.MIN_VALUE;

    private PropertiesManager() {
        InputStream inputStream = PropertiesManager.class.getResourceAsStream("/environment.properties");
        props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesManager getInstance() {
        if (instance == null) {
            instance = new PropertiesManager();
        }
        return instance;
    }

    /**
     *
     * @param key
     * @return null if the key is not found on the properties, a valid String
     *         otherwise.
     */
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    /**
     *
     * @return INVALID_INTEGER_ERROR_CODE in case of error, a valid integer
     *         otherwise.
     */
    public int getIntegerProperty(String key) {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (NullPointerException | NumberFormatException e) {
            return INVALID_INTEGER_ERROR_CODE;
        }
    }
}
