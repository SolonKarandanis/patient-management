package com.pm.authservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    
    /**
     * method get the WFP Regulations key for display
     *
     * @param key
     * @return
     */
    public static int getWFPRegulationPart2Key(String key) {
        int result = 0;
        if (key != null) {
            result = Integer.parseInt(key) - 100;
        }
        return result;
    }

    /**
     * method get the WFP Regulations key for display
     *
     * @param key
     * @return
     */
    public static int getWFPRegulationKey(String key) {
        int result = 0;

        if (key != null) {
            int keyVal = Integer.parseInt(key);
            result = keyVal > 100 ? keyVal - 100 : keyVal;
        }
        return result;
    }
    
    /**
     * method to get the quarter month(1-3)
     *
     * @param month(1-12)
     * @return int
     */
    public static int getQuarterMonth(int month) {
        switch (month) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 1;
            case 8:
                return 2;
            case 9:
                return 3;
            case 10:
                return 1;
            case 11:
                return 2;
            case 12:
                return 3;
        }
        return 1;

    }
    
    /**
     * method to get the quarter month(1-3)
     *
     * @param month(1-12)
     * @return int
     */
    public static String getQuarterForMonth(int month) {
        switch (month) {
            case 1:
                return getQuarter(1);
            case 2:
                return getQuarter(1);
            case 3:
                return getQuarter(1);
            case 4:
                return getQuarter(2);
            case 5:
                return getQuarter(2);
            case 6:
                return getQuarter(2);
            case 7:
                return getQuarter(3);
            case 8:
                return getQuarter(3);
            case 9:
                return getQuarter(3);
            case 10:
                return getQuarter(4);
            case 11:
                return getQuarter(4);
            case 12:
                return getQuarter(4);
        }
        return "Q5";

    }
    
    /**
     * method to get the QuarterName
     *
     * @param quarter
     * @return String
     */
    public static String getQuarter(Integer quarter) {
        return "Q" + quarter.intValue();
    }
    
    /**
     * Returns the ip of the remote address.
     * The result is a concatenation of he request header "x-forwarded-for"
     * and the result of the <code>request.getRemoteAddr()</code> in all cases.
     *
     * @param request
     *            <code>HttpServletRequest</code> The request object.
     * @return <code>String</code> The ip.
     */
    public static String getIP(HttpServletRequest request) {

        String ip = "";

        if (request != null) {
            String forwardedFor = request.getHeader("x-forwarded-for");
            String remoteAddress = request.getRemoteAddr();
            // note that when not behind a reverse proxy null will be used
            ip = forwardedFor + "/" + remoteAddress;
        }
        return ip;
    }
    
    /**
     * Returns the server name.
     *
     * @return <code>String</code> The protocol.
     */
    public static String getServerName() {
        String name = "";

        try {
            name = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        return name;
    }

    public static String getCurrentClientIpAddress() {
        String remoteClient = null;
        String currentThreadName = Thread.currentThread().getName();
        int begin = currentThreadName.indexOf('[') + 1;
        int end = currentThreadName.indexOf(':');
        if (begin > 0 && end > 0 && end > begin) {
            remoteClient = currentThreadName.substring(begin, end);
        }
        return remoteClient;
    }

    public static boolean isNullOrEmpty(Collection<? extends Object> coll) {
        return (coll == null || coll.isEmpty());
    }

    public static boolean isMapNullOrEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
    
    public static boolean areBothListsSame(List<?> list1, List<?> list2) {

        boolean retVal = false;

        if ((list1.size() == list2.size()) && (list1.containsAll(list2))) {
            retVal = true;
        }

        return retVal;

    }

    public static <T> boolean isNull(T t) {
        return t == null;
    }

    /**
     * @param o1
     * @param o2
     * @return o1 if o1 is not null, otherwise o2
     */
    public static <T> T nvl(T o1, T o2) {
        return o1 != null ? o1 : o2;
    }

    public static boolean isNullOrFalse(Boolean value) {
        return value == null || value.equals(false);
    }



}
