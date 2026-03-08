package com.pm.notificationservice.file.common;

import com.pm.notificationservice.shared.exception.NotificationServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileUtil {
    private FileUtil() {

    }

    private static int READ_BUFFER_SIZE = 1024 * 8; //8k
    private static int WRITE_BUFFER_SIZE = 1024 * 8; //8k
    public static final String ZIP_EXT = "zip";

    private static final Map<String, String> contentTypesExtensionsMap = new HashMap<>();

    static {
        contentTypesExtensionsMap.put("pdf", "application/pdf");
        contentTypesExtensionsMap.put("xls", "application/excel");
        contentTypesExtensionsMap.put("txt", "text/plain");
        contentTypesExtensionsMap.put("jpg", "image/jpeg");
        contentTypesExtensionsMap.put("jpeg", "image/jpeg");
        contentTypesExtensionsMap.put("png", "image/png");
        contentTypesExtensionsMap.put("gif", "image/gif");
        contentTypesExtensionsMap.put("doc", "application/msword");
        contentTypesExtensionsMap.put("zip", "application/zip");
        contentTypesExtensionsMap.put("csv", "application/csv");
    }

    /**
     * Returns the contents of the file as a byte array.
     *
     * @param file
     * @return Array of bytes.
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = null;
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        IOException thrown = null;

        try {
            is = new FileInputStream(file);
            // Read the bytes
            int offset = 0;
            int numRead = 0;
            int bytesLen = Math.min(READ_BUFFER_SIZE, length - offset);

            while ((numRead = is.read(bytes, offset, bytesLen)) == READ_BUFFER_SIZE) {
                offset += numRead;
                bytesLen = Math.min(READ_BUFFER_SIZE, length - offset);
            }
            offset += numRead;
            // Ensure all bytes have been read
            if (offset != length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
        } catch (IOException e) {
            thrown = e;
        } finally {
            closeSilently(is);
        }

        if (thrown != null) {
            throw thrown;
        } else {
            return bytes;
        }
    }

    /**
     * @param file    File to write to
     * @param content Array of bytes
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeBytesToFile(File file, byte[] content) throws FileNotFoundException, IOException {

        FileOutputStream fos = null;
        IOException thrown = null;
        try {
            fos = new FileOutputStream(file);
            int offset = 0;
            int len_minus_buf_len = content.length - WRITE_BUFFER_SIZE;
            int remaining;
            while (offset < len_minus_buf_len) {
                fos.write(content, offset, WRITE_BUFFER_SIZE);
                fos.flush();
                offset += WRITE_BUFFER_SIZE;
            }
            remaining = content.length - offset;
            if (remaining > 0) {
                fos.write(content, offset, remaining);
                fos.flush();
            }

        } catch (IOException e) {
            thrown = e;
        } finally {
            closeSilently(fos);
        }

        if (thrown != null) {
            throw thrown;
        }
    }

    /**
     * Unpacks zip file and stores files to specified location on disk.
     *
     * @param f       Zip file.
     * @param destDir Absolute path to unpacked folder
     * @throws NotificationServiceException
     */
    public static List<File> unZip(File f, File destDir) throws NotificationServiceException {
        List<File> retVal = new ArrayList<>(1024);
        FileInputStream fileIn = null;
        FileOutputStream unzippedOut = null;
        ZipInputStream zis = null;
        try {
            destDir.mkdir();

            fileIn = new FileInputStream(f);
            zis = new ZipInputStream(new BufferedInputStream(fileIn));

            ZipEntry entry;
            File unzippedFile;

            while ((entry = zis.getNextEntry()) != null) {
                log.debug("Extracting: {}", entry);
                if (!entry.isDirectory()) {
                    //strip directory from entry name
                    String fileName = stripDirectorySlashes(entry.getName());

                    int count;
                    byte[] data = new byte[READ_BUFFER_SIZE];
                    // write files to the disk
                    unzippedFile = new File(destDir, fileName);
                    unzippedOut = new FileOutputStream(unzippedFile);
                    while ((count = zis.read(data, 0, READ_BUFFER_SIZE)) != -1) {
                        unzippedOut.write(data, 0, count);
                    }
                    unzippedOut.flush();
                    unzippedOut.close();
                    retVal.add(unzippedFile);
                }
            }

        } catch (Exception ex) {
            throw new NotificationServiceException("error.unpacking.zip", ex);
        } finally {
            closeSilently(fileIn);
            closeSilently(unzippedOut);
            closeSilently(zis);
        }

        return retVal;
    }

    /**
     * Deletes directory specified by path. Recursively deletes all folders and
     * files in root folder.
     *
     * @param file Absolute path to unpack folder
     * @return true if successfully deleted
     */
    public static boolean delete(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
                boolean success = delete(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static String getContentTypeByFileName(String filename) {
        String contentType = null;
        if (filename.lastIndexOf(".") != -1) {
            String extension = filename.substring(filename.lastIndexOf(".") + 1);
            //contentType can be null
            contentType = contentTypesExtensionsMap.get(extension.toLowerCase());
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        log.info("contentType is {}", contentType);
        return contentType;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static String getFileType(String filename) {
        String ftype = null;
        if (!StringUtils.hasLength(filename) && filename.lastIndexOf(".") != -1) {
            ftype = filename.substring(filename.lastIndexOf(".") + 1);
        }
        return ftype;
    }

    public static HashSet<String> getFileNamesFromZip(byte[] bytesZip) throws NotificationServiceException {
        ZipEntry entry;
        HashSet<String> retSet = new HashSet<>();
        try {
            ByteArrayInputStream inputStreamZip = new ByteArrayInputStream(bytesZip);
            ZipInputStream zis = new ZipInputStream(inputStreamZip);
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    //remove the directory slashes
                    String fileName = stripDirectorySlashes(entry.getName());
                    retSet.add(fileName);
                }
            }
        } catch (IOException e) {
            throw new NotificationServiceException("error.unpacking.zip", e);
        }
        return retSet;
    }

    public static Map<String, ZipEntry> getZipEntriesFromZip(byte[] bytesZip) throws NotificationServiceException {
        ZipEntry entry;
        Map<String, ZipEntry> retSet = new HashMap<>();
        try {
            ByteArrayInputStream inputStreamZip = new ByteArrayInputStream(bytesZip);
            ZipInputStream zis = new ZipInputStream(inputStreamZip);
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    //remove the directory slashes
                    String fileName = stripDirectorySlashes(entry.getName());
                    retSet.put(fileName, entry);
                }
            }
        } catch (IOException e) {
            throw new NotificationServiceException("error.unpacking.zip", e);
        }
        return retSet;
    }

    /**
     * Used to strip out a zip / file entry from its directory path
     * ex. pens/lol/test.jpg -> test.jpg
     *
     * @param fileName
     * @return
     */
    private static String stripDirectorySlashes(String fileName) {
        int lastIndex = fileName.lastIndexOf("/");
        if (lastIndex >= 1) {
            fileName = fileName.substring(lastIndex + 1);
        }
        return fileName;
    }

    public static File zip(String zipName, List<File> filesToZip, String destinationDir) throws NotificationServiceException {
        return zip(zipName, filesToZip, String.valueOf(new File(destinationDir)));
    }

    /**
     * Creates a zip file from the given list of files to zip,
     * {@code zipName} will be appended with internally-generated characters to ensure uniqueness of the file name
     *
     * @param zipName        name of the zip file
     * @param filesToZip     files to zip
     * @param destinationDir destination directory
     */
    public static File zip(String zipName, List<File> filesToZip, File destinationDir) throws NotificationServiceException {
        ZipOutputStream zos = null;
        InputStream is = null;
        try {
            File zip = File.createTempFile(zipName, "." + ZIP_EXT, destinationDir);

            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip)));

            for (File file : filesToZip) {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);

                is = new FileInputStream(file);

                byte[] dataBlock = new byte[1024];
                int count = is.read(dataBlock, 0, 1024);

                while (count != -1) {
                    zos.write(dataBlock, 0, count);
                    count = is.read(dataBlock, 0, 1024);
                }

                zos.closeEntry();
                is.close();
            }

            zos.close();

            return zip;

        } catch (IOException e) {
            log.error("IO error in file zip {}", e.getMessage());
            throw new NotificationServiceException("error.zipping.files", e);
        } finally {
            IOUtils.closeQuietly(zos);
            IOUtils.closeQuietly(is);
        }
    }

    public static void deleteFile(File file) {
        if (file == null)
            return;

        try {
            file.delete();
        } catch (Exception e) {
            log.error("Couldn't delete file: {} {}", file.getAbsolutePath(), e);
        }
    }

    /**
     * Deletes the file in the specified path
     *
     * @param filePath the full path of the file to be deleted
     * @return true if file was deleted
     */
    public static Boolean deleteFileInPath(String filePath) {

        Boolean retVal = false;

        File file = new File(filePath);
        if (file.exists()) {
            retVal = file.delete();
            if (retVal) {
                log.debug("SUCCESS: Deleted file: {}", filePath);
            } else {
                log.debug("ERROR  : Could not delete file: {}", filePath);
            }
        } else {
            log.debug("ERROR  : File Does not exist in path : {}", filePath);
        }

        return retVal;
    }

    public static ByteArrayInputStream getByteArrayInputStream(InputStream inputStream) throws IOException {
        if (inputStream instanceof ByteArrayInputStream) {
            return (ByteArrayInputStream) inputStream;
        }

        byte[] data = getBytesFromStream(inputStream);
        ByteArrayInputStream stream = null;
        //Temporary fix to handle null data in DB
        if (data != null && data.length > 0) {
            stream = new ByteArrayInputStream(data);
            stream.mark(Integer.MAX_VALUE);
        }
        return stream;

    }

    public static byte[] getBytesFromStream(InputStream inputStream) throws IOException {
        byte[] data = null;
        if (inputStream != null) {
            data = new byte[2048];
            if (inputStream.markSupported()) {
                inputStream.mark(Integer.MAX_VALUE);
            }
            int c = inputStream.read(data, 0, data.length);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            while (c != -1) {
                bos.write(data, 0, c);
                c = inputStream.read(data, 0, data.length);
            }
            try {
                if (inputStream.markSupported()) {
                    inputStream.reset();
                }
            } catch (IOException _) {
                ;
            }
            data = bos.toByteArray();
        }
        return data;

    }
}
