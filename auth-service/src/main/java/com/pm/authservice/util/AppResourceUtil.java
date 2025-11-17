package com.pm.authservice.util;

import com.pm.authservice.exception.BusinessException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

public class AppResourceUtil {

    /** Logger for this class. */
    private static final Logger LOG = LoggerFactory.getLogger(AppResourceUtil.class);

    private static final String ERROR_RESOURCE_COULD_NOT_BE_LOADED = " ERROR: Resource could not be loaded: {}: {}";

    private static final String LOCAL_CLASSPATH = "classpath:";

    private AppResourceUtil() {
        super();
    }

    /**
     * @param resourceLoader
     *            <code>ResourceLoader</code>
     * @param resourceName
     *            <code>String</code>
     * @return <code>byte[]</code>
     */
    public static byte[] loadResourceBytes(final ResourceLoader resourceLoader, final String resourceName)
            throws BusinessException {
        byte[] outputAsBytes = null;
        Resource appResource = resourceLoader.getResource(LOCAL_CLASSPATH + resourceName);
        try (InputStream appResourceAsStream = appResource.getInputStream();) {
            outputAsBytes = IOUtils.toByteArray(appResourceAsStream);
        } catch (FileNotFoundException e) {
            /* Resource not found, not fatal error, simply return NULL bytes and handle NULL outside method. */
            LOG.error(ERROR_RESOURCE_COULD_NOT_BE_LOADED, e.getClass().toString(), e.getMessage());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        Optional.ofNullable(outputAsBytes).ifPresent(ofBytes -> LOG.debug(" Loaded Resource {}: {} bytes ", resourceName, ofBytes.length));
        return outputAsBytes;
    }

    /**
     * @param appContext
     *            <code>ApplicationContext</code>
     * @param resourceName
     *            <code>String</code>
     * @return <code>byte[]</code>
     */
    public static byte[] loadResourceBytes(final ApplicationContext appContext, final String resourceName)
            throws BusinessException {
        return loadResourceBytes((ResourceLoader) appContext, resourceName);
    }

    /**
     * @param resourceLoader
     *            <code>ResourceLoader</code>
     * @param resourceName
     *            <code>String</code>
     * @return <code>byte[]</code>
     */
    public static InputStream loadResourceAsStream(final ResourceLoader resourceLoader, final String resourceName)
            throws BusinessException {
        InputStream appResourceAsStream = null;
        Resource appResource = resourceLoader.getResource(LOCAL_CLASSPATH + resourceName);
        try {
            appResourceAsStream = appResource.getInputStream();
        } catch (FileNotFoundException e) {
            /*
             * Resource not found, not fatal error, simply return NULL bytes and
             * handle NULL outside method.
             */
            LOG.error(ERROR_RESOURCE_COULD_NOT_BE_LOADED, e.getClass().toString(), e.getMessage());
            return null;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        return appResourceAsStream;
    }

    /**
     * @param appContext
     *            <code>ApplicationContext</code>
     * @param resourceName
     *            <code>String</code>
     * @return <code>byte[]</code>
     */
    public static InputStream loadResourceAsStream(final ApplicationContext appContext, final String resourceName)
            throws BusinessException {
        return loadResourceAsStream((ResourceLoader) appContext, resourceName);
    }

    /**
     * @param resourceLoader
     *            <code>ResourceLoader</code>
     * @param resourceName
     *            <code>String</code>
     * @return <code>byte[]</code>
     */
    public static String loadResourceAsUtf8String(final ResourceLoader resourceLoader, final String resourceName)
            throws BusinessException {
        String outputAsString = null;
        Resource appResource = resourceLoader.getResource(LOCAL_CLASSPATH + resourceName);
        try (InputStream appResourceAsStream = appResource.getInputStream();) {
            outputAsString = IOUtils.toString(appResourceAsStream, StandardCharsets.UTF_8);

        } catch (FileNotFoundException e) {
            /* Resource not found, not fatal error, simply return NULL bytes and handle NULL outside method. */
            LOG.error(ERROR_RESOURCE_COULD_NOT_BE_LOADED, e.getClass().toString(), e.getMessage());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        Optional.ofNullable(outputAsString).ifPresent(ofString -> LOG.debug(" Loaded Resource {}: {} length ", resourceName, ofString.length()));
        return outputAsString;
    }

    /**
     * @param appContext
     *            <code>ApplicationContext</code>
     * @param resourceName
     *            <code>String</code>
     * @return <code>byte[]</code>
     */
    public static String loadResourceAsUtf8String(final ApplicationContext appContext, final String resourceName)
            throws BusinessException {
        return loadResourceAsUtf8String((ResourceLoader) appContext, resourceName);
    }

    /**
     * @param resourceLoader
     * @param resourceName
     * @param charset
     * @return
     */
    public static Properties loadResourceAsProperties(final ResourceLoader resourceLoader, final String resourceName, final Charset charset)
            throws BusinessException {
        Properties props = new Properties();
        InputStream appResourceAsStream = AppResourceUtil.loadResourceAsStream(resourceLoader, resourceName);

        if (appResourceAsStream != null) {
            try (InputStream appResExistsAsStream = appResourceAsStream; InputStreamReader appResRead = new InputStreamReader(appResExistsAsStream, String.valueOf(charset));) {
                props.load(appResRead);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return props;
    }

    /**
     * @param appContext
     * @param resourceName
     * @param charset
     * @return
     */
    public static Properties loadResourceAsProperties(final ApplicationContext appContext, final String resourceName, final Charset charset)
            throws BusinessException {
        return loadResourceAsProperties((ResourceLoader) appContext, resourceName, charset);
    }
}
