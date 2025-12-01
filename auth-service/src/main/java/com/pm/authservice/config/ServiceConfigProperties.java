package com.pm.authservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

public class ServiceConfigProperties {

    protected final static Logger log = LoggerFactory.getLogger(ServiceConfigProperties.class);

    protected static Properties APPLICATION_PROPS = initProps("service.properties");

    public static Boolean I18N_RESOURCES_DB_ENABLED = getBooleanProperty("i18n.resources.DB.enabled", false);
    public static Boolean CLUSTER_ENABLED = getBooleanProperty("cluster.enabled", false);
    public static Boolean WEBSOCKETS_ENABLED = getBooleanProperty("websockets.enabled", false);


    protected static Properties initProps(String propertyName) {
        Properties retVal = null;
        try {
            URL propertyFileUrl = ServiceConfigProperties.class.getResource("/" + propertyName);
            assert propertyFileUrl != null;
            InputStream in = propertyFileUrl.openStream();
            retVal = new Properties();
            retVal.load(in);
            in.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            retVal = null;
        }
        return retVal;
    }

    protected static int getIntegerProperty(String key) {
        String property = APPLICATION_PROPS.getProperty(key);
        if (!StringUtils.hasLength(property)) {
            property = "0";
        }
        return Integer.parseInt(property);
    }

    protected static int getIntegerProperty(String key, int def) {
        String property = APPLICATION_PROPS.getProperty(key);
        if (!StringUtils.hasLength(property)) {
            return def;
        }
        return Integer.parseInt(property);
    }

    protected static long getLongProperty(String key) {
        String property = APPLICATION_PROPS.getProperty(key);
        if (!StringUtils.hasLength(property)) {
            property = "0";
        }
        return Long.parseLong(property);
    }

    protected static Boolean getBooleanProperty(String key) {
        String strProp = APPLICATION_PROPS.getProperty(key);
        return Boolean.parseBoolean(strProp);
    }

    protected static Boolean getBooleanProperty(String key, boolean def) {
        String strProp = APPLICATION_PROPS.getProperty(key);
        if (!StringUtils.hasLength(strProp)){
            return def;
        }
        return Boolean.parseBoolean(strProp);
    }

    protected static String getLettersHostName() {
        String hostName = "";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        int dotIndex = hostName.indexOf('.');
        if (dotIndex == -1) {
            dotIndex = hostName.length();
        }
        return hostName.substring(dotIndex - 2, dotIndex);
    }

    protected static String[] getPropertyValueAsStringArray(String key) {
        String strProp = APPLICATION_PROPS.getProperty(key);
        return strProp.split(",");
    }

    protected static Integer[] getPropertyValueAsIntArray(String key) {
        String[] strProp = getPropertyValueAsStringArray(key);
        Integer[] intProp = new Integer[strProp.length];
        for (int i = 0; i < strProp.length; ++i) {
            String str = strProp[i].trim();
            intProp[i] = Integer.parseInt(str);
        }
        return intProp;
    }
}
