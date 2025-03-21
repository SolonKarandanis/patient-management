package com.pm.authservice.util;

public class StringUtils extends org.springframework.util.StringUtils {

    public static String replaceParametersInString(final String inputString, final Object... messageParameters) {
        StringBuilder pOutputBuild = new StringBuilder(10);
        pOutputBuild.append(inputString);

        for (int i = 0; i < messageParameters.length; i++) {
            String intermediate = pOutputBuild.toString();
            intermediate = intermediate.replace(getAsConcatenatedString("{", String.valueOf(i), "}"),
                    messageParameters[i] != null ? messageParameters[i].toString() : "");
            pOutputBuild.delete(0, pOutputBuild.length());
            pOutputBuild.append(intermediate);
        }
        return pOutputBuild.toString();
    }

    public static String getAsConcatenatedString(final Object... args) {
        StringBuilder outBuilder = new StringBuilder(getTotalLength(args));

        for (Object arg : args) {
            if (arg != null) {
                outBuilder.append(String.valueOf(arg));
            } else {
                outBuilder.append("null");
            }
        }
        return outBuilder.toString();
    }

    public static int getTotalLength(final Object... args) {
        int iTotal = 0;
        for (Object arg : args) {
            if (arg != null) {
                iTotal += String.valueOf(arg).length();
            } else {
                iTotal += "null".length();
            }
        }
        return iTotal;
    }
}
