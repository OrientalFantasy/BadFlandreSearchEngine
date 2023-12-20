package com.badflandre.search.util;

import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class ProperConfig {
    private static String CONFIG_FILE = "config";
    private static ResourceBundle bundle;
    static {
        try {
            bundle = ResourceBundle.getBundle(CONFIG_FILE);
        } catch (MissingResourceException mre) {
            System.out.println("Cannot find config file "
                    + CONFIG_FILE + ".properties.");
        }
    }

    public static String getValue(String key) {
        return bundle.getString(key);
    }
}
