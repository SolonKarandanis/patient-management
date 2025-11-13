package com.pm.authservice.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class CollectionUtil {

    private CollectionUtil() {
        super();
    }

    public static <T> List<List<T>> splitList(final List<T> pOriginalList, final int iLength) {
        List<List<T>> output = new ArrayList<List<T>>();

        if (iLength <= 0 || iLength >= pOriginalList.size()) {
            output.add(pOriginalList);
        } else {
            int iTotal = (pOriginalList.size() % iLength != 0
                    ? (pOriginalList.size() / iLength) + 1 : pOriginalList.size() / iLength);
            for (int i = 0; i < iTotal; i++) {
                int fromIndex = i * iLength;
                int toIndex = (Math.min((i + 1) * iLength, pOriginalList.size()));
                List<T> pPage = pOriginalList.subList(fromIndex, toIndex);
                output.add(pPage);
            }
        }
        return output;
    }

    public static <T> T safeGet(final List<T> pOriginalList, final int iIndex) {
        return pOriginalList != null && iIndex >= 0 && iIndex < pOriginalList.size() ? pOriginalList.get(iIndex) : null;
    }

    public static <K, V> byte[] convertMapToPropertiesBytes(final Map<K, V> inputAsMap) {
        byte[] outputAsBytes = null;

        Properties objProps = new Properties();

        Optional.ofNullable(inputAsMap).ifPresent(ofInputAsMap -> ofInputAsMap.forEach((p, v) -> objProps.put(p, v)));

        ByteArrayOutputStream obOutput = new ByteArrayOutputStream();
        try {
            objProps.store(obOutput, null);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        outputAsBytes = obOutput.toByteArray();

        return outputAsBytes;
    }

}
