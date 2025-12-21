package com.pm.notificationservice.shared.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

public class MiscUtil {
	
	/** Private constructor. */
    private MiscUtil() {
    }

    /** Logger for the class. */
    private static final Logger LOG = LoggerFactory.getLogger(MiscUtil.class);

    /** The length to 8 for buffers. */
    private static final int UNIT_LENGTH = 8;

    /** The length to 24 for buffers. */
    private static final int UNIT_LENGTH_3 = 24;

    /** The length to 32 for buffers. */
    private static final int UNIT_LENGTH_4 = 32;

    /** Zero representation. */
    private static final int ZERO_VALUE = 0xff;

    /** Cached per JVM server IP. */
    private static String hexServerIP = null;

    /** The <code>SecureRandom</code> seeder for the class. */
    private static final SecureRandom SEEDER = new SecureRandom();
    
    /**
     * Generates GU ID for an <code>Object</code>.
     *
     * @param o
     *            <code>Object</code>
     *            The object to generate the GU ID.
     * @return <code>String</code>
     *         The GU ID.
     */
    public static final String generateGUID(Object o) {
        boolean breakExecution = false;
        StringBuffer tmpBuffer = new StringBuffer(2 * UNIT_LENGTH);
        String result = null;
        if (hexServerIP == null) {
            InetAddress localInetAddress = null;
            try {
                // get the inet address
                localInetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException uhe) {
                LOG.error(uhe.getMessage(), uhe);
                /* NOT the best way to handle an exception, but we are hoping we
                * will never get one here*/
                breakExecution = true;
            }
            if (!breakExecution) {
                byte[] serverIP = localInetAddress.getAddress();
                hexServerIP = hexFormat(getInt(serverIP), UNIT_LENGTH);
            }
        }
        if (!breakExecution) {
            result = getGUID(o, tmpBuffer);
        }
        return result;
    }
    
    /**
     * Helper for the generation.
     *
     * @param o
     *            <code>Object</code>
     *            The object to generate the GU ID.
     * @param tmpBuffer
     *            <code>StringBuffer</code>
     *            A temp buffer that came form caller.
     * @return <code>String</code> the GU ID.
     */
    private static String getGUID(Object o, StringBuffer tmpBuffer) {
        String hashcode = hexFormat(System.identityHashCode(o), UNIT_LENGTH);
        tmpBuffer.append(hexServerIP);
        tmpBuffer.append(hashcode);

        long timeNow = System.currentTimeMillis();
        int timeLow = (int) timeNow & 0xFFFFFFFF;
        int node = SEEDER.nextInt();

        StringBuffer guid = new StringBuffer(UNIT_LENGTH_4);
        guid.append(hexFormat(timeLow, UNIT_LENGTH));
        guid.append(tmpBuffer.toString());
        guid.append(hexFormat(node, UNIT_LENGTH));
        return guid.toString();
    }
    
    /**
     * Gets an integer representation of a <code>byte</code> array.
     *
     * @param bytes
     *            <code>byte[]</code>
     *            The byte array.
     * @return <code>int</code>
     *         The string representation for the array.
     */
    private static int getInt(byte[] bytes) {
        int i = 0;
        int j = UNIT_LENGTH_3;
        for (int k = 0; j >= 0; k++) {
            int l = bytes[k] & ZERO_VALUE;
            i += l << j;
            j -= UNIT_LENGTH;
        }
        return i;
    }
    
    /**
     * Creates a hexadecimal format of an integer and speccifies
     * a padding for it.
     *
     * @param i
     *            <code>int</code>
     *            The integer parameter to be converted to Hex string.
     * @param j
     *            <code>int</code>
     *            The integer parameter showing the length of padding.
     * @return <code>String</code>
     *         The hexadecimal format of the 2 integers.
     */
    private static String hexFormat(int i, int j) {
        String s = Integer.toHexString(i);
        return padHex(s, j) + s;
    }
    
    /**
     * Provides a padding of '0' characters to a <code>String</code>.
     *
     * @param s
     *            <code>String</code>
     *            The string to accept the padding.
     * @param i
     *            <code>int</code>
     *            Speccifies how many padding characters will be added.
     *            These are the result of subtraction: <br>
     *            i - length(s) <br>
     * @return <code>String</code>
     *         The string having been padded with '0'.
     */
    private static String padHex(String s, int i) {
        StringBuffer tmpBuffer = new StringBuffer();
        if (s.length() < i) {
            for (int j = 0; j < i - s.length(); j++) {
                tmpBuffer.append('0');
            }
        }
        return tmpBuffer.toString();
    }
}
