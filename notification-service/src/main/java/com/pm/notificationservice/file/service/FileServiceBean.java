package com.pm.notificationservice.file.service;

import com.pm.notificationservice.file.common.FileConstants;
import com.pm.notificationservice.file.common.FileUtil;
import com.pm.notificationservice.file.dto.FileDTO;
import com.pm.notificationservice.file.model.FileInfo;
import com.pm.notificationservice.file.repository.FileInfoRepository;
import com.pm.notificationservice.shared.exception.NotificationServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Service("fileService")
@Transactional
public class FileServiceBean implements FileService{
    private static final Logger log = LoggerFactory.getLogger(FileServiceBean.class);

    @Value("${file.storage.path}")
    private static String fileStoragePath;

    private static File TEMP_DIR = new File(fileStoragePath, "__tmp__");

    static {
        TEMP_DIR.mkdirs();
    }

    private final FileInfoRepository fileInfoRepository;

    public FileServiceBean(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository= fileInfoRepository;
    }

    /* **********************A T T E N T I O N********** I M P O R T A N T ****************************
     * If you have introduced a new file ( new column ) it should be referenced in "fileUsages" list  *
     * in "FileInfo" class. Files in columns not referenced in fileUsages list will be deleted during *
     * file system clean up daily process.                                                            *
     * ************************************************************************************************
     */
    @Override
    public BigInteger createFileInfo(FileInfo fileInfo)
            throws NotificationServiceException {
        try {
            if (fileInfo.getFileRefId() == null) {
                BigInteger fid = fileInfoRepository.generateIdFromSequencer("file_info_generator");
                fileInfo.setFileRefId(fid.longValue());
            }
            fileInfoRepository.save(fileInfo);
            return BigInteger.valueOf(fileInfo.getFileRefId());
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
            throw new NotificationServiceException("error.create.file", ex);
        }
    }

    /* **********************A T T E N T I O N********** I M P O R T A N T ****************************
     * If you have introduced a new file ( new column ) it should be referenced in "fileUsages" list  *
     * in "FileInfo" class. Files in columns not referenced in fileUsages list will be deleted during *
     * file system clean up daily process.                                                            *
     * ************************************************************************************************
     *
     * NOTE! Returns the fileReferenceId (which maps to the file system) not the FileInfo id
     * (non-Javadoc)
     * @see com.ed.ccm.service.FileService#createFile(byte[], com.ed.ccm.domain.FileInfo)
     */
    @Override
    public BigInteger createFile(byte[] content, FileInfo fileInfo)
            throws NotificationServiceException {
        try {
            createFileInfo(fileInfo);
            storeFile(fileInfo, content);
            return BigInteger.valueOf(fileInfo.getFileRefId());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new NotificationServiceException("error.create.file", ex);
        }
    }

    /* **********************A T T E N T I O N********** I M P O R T A N T ****************************
     * If you have introduced a new file ( new column ) it should be referenced in "fileUsages" list  *
     * in "FileInfo" class. Files in columns not referenced in fileUsages list will be deleted during *
     * file system clean up daily process.                                                            *
     * ************************************************************************************************
     *
     * (non-Javadoc)
     *
     * @see com.ed.ccm.service.FileService#storeFiles(java.util.Map)
     */
    @Override
    public int createFiles(Map<FileInfo, byte[]> dataMap)
            throws NotificationServiceException {
        int res = 0;
        for (Map.Entry<FileInfo, byte[]> dataMapEntry : dataMap.entrySet()) {
            byte[] f = dataMapEntry.getValue();
            createFile(f, dataMapEntry.getKey());
            res++;
        }
        return res;
    }

    @Override
    public boolean fileExistsInStorage(BigInteger fid) {
        String[] paths = convertIdToPath(fid);
        File res = new File(paths[0], paths[1]);
        return res.isFile();
    }

    @Override
    public byte[] getFileContentById(BigInteger fid)
            throws NotificationServiceException {
        String[] paths = convertIdToPath(fid);
        File res = new File(paths[0], paths[1]);
        try {
            return FileUtil.getBytesFromFile(res);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new NotificationServiceException("error.reading.file", ex);
        }
    }

    @Override
    public boolean deleteFile(BigInteger fid) {
        FileInfo fileInfo = fileInfoRepository.findByFileRefId(fid.longValue());
        if (fileInfo != null) { //if not its an old file reference without a file info entry
            fileInfoRepository.delete(fileInfo);
        }
        String[] paths = convertIdToPath(fid);
        File res = new File(paths[0], paths[1]);
        return res.delete();
    }

    @Override
    public boolean deleteFileInfo(BigInteger fid) {
        boolean isDeleted = false;
        FileInfo fileInfo = fileInfoRepository.findByFileRefId(fid.longValue());
        if (fileInfo != null) { //if not its an old file reference without a file info entry
            fileInfoRepository.delete(fileInfo);
            isDeleted = true;
        }
        return isDeleted;
    }

    @Override
    public boolean deleteOnFileSystem(BigInteger fid) {
        String[] paths = convertIdToPath(fid);
        File res = new File(paths[0], paths[1]);
        return res.delete();
    }

    @Override
    public void deleteFiles(List<BigInteger> fileReferencesToDelete) throws Exception {
        List<Long> refs = new ArrayList<>();
        for (BigInteger fid : fileReferencesToDelete) {
            refs.add(fid.longValue());
        }
        fileInfoRepository.deleteByFileRefIdsInBatch(refs);
        for (BigInteger fid : fileReferencesToDelete) {
            String[] paths = convertIdToPath(fid);
            File res = new File(paths[0], paths[1]);
            res.delete();
        }
    }

    @Override
    public Map<String, FileInfo> unzipFile(BigInteger fid) throws NotificationServiceException {
        return Map.of();
    }

    /**
     * @param fileSystemPath
     *            The path that should be checked for files
     * @return Returns all files included in the specified path and subdirectories, excluding directories specified in
     *         constants
     */
    @Override
    public Map<String, FileDTO> getAllFilesInFileSystem(String fileSystemPath) {
        Map<String, FileDTO> retVal = new HashMap<>();
        File fl = new File(fileSystemPath);
        if (fl.exists() && fl.isDirectory()) {
            File[] subFiles = fl.listFiles();
            for(int i = 0; i < Objects.requireNonNull(subFiles).length; i++) {
                File subFile = subFiles[i];
                //if File is a directory and it's name starts with a Digit ( dut files directories start with digit )
                if ((subFile.isDirectory()) && (Character.isDigit(subFile.getName().charAt(0)))) {
                    log.trace("Crawl for files in directory: {}" , subFile.getAbsolutePath());
                    retVal.putAll(crawlDirectoryForFiles(subFile.getAbsolutePath(), "", retVal));
                }
            }
        }
        return retVal;
    }

    @Override
    public String getFileId(BigInteger fid) {
        return String.format("%012d", fid);
    }

    /**
     * @param path
     *            The path to start crawling
     * @param pathId
     *            The id of the file until this path
     * @param files
     *            The map of files.
     * @return Returns all the files under the initial path
     */
    @Override
    public Map<String, FileDTO> crawlDirectoryForFiles(String path, String pathId, Map<String, FileDTO> files) {
        File fl = new File(path);
        if (fl.isDirectory()) {
            File[] subFiles = fl.listFiles();
            assert subFiles != null;
            for (File subFile : subFiles) {
                String newPath = addToPath(path, subFile.getName());
                String newPathId = pathId + fl.getName();
                files = crawlDirectoryForFiles(newPath, newPathId, files);
            }
        }
        else if (fl.isFile()) {
            String fileId = pathId + fl.getName();
            String fullPath = fl.getAbsolutePath();
            Long dateLastModified = fl.lastModified();
            Long fileSize = fl.length();
            FileDTO ccmFile = new FileDTO(fileId, fullPath, dateLastModified, fileSize);
            files.put(fileId, ccmFile);
        } else {
            log.info("Unknown type");
        }
        return files;
    }

    /**
     * Deletes all redundant files from the filesystem.
     *
     * @param ccmFilesToDelete
     *            Map that contains the redundant files in the filesystem
     */
    @Override
    public void deleteRedundantFiles(Map<String, FileDTO> ccmFilesToDelete) {
        log.info("---Started Delete Redundant Files");
        for (Map.Entry<String, FileDTO> entry : ccmFilesToDelete.entrySet()) {
            FileDTO fileToDelete = entry.getValue();
            //Convert File id to BigInteger
            Long fileId = Long.valueOf(fileToDelete.getFileId());
            BigInteger fid = BigInteger.valueOf(fileId);
            deleteFile(fid);
        }
        log.info("---Finished Delete Redundant files");
    }

    /**
     * This method finds which dut files in file storage are not referenced in the db and deletes them.
     */
    @Override
    public void filesystemCleanUp() {
        log.info("Started file system clean up");
        log.info("FILE SYSTEM CLEAN UP - QUERIES COUNT: {}", FileConstants.fileUsages.size());
        //Get all file IDs referenced in the db. The list of the columns that contain File IDs is
        List<BigInteger> filesInDB = new ArrayList<>();
        try {
            for(Map.Entry<String, String> entry: FileConstants.fileUsages.entrySet()) {
                String tableName = entry.getKey();
                String columnName = entry.getValue();
                List<BigInteger> filesList = fileInfoRepository.getFileReferenceIdsForTableColumn(columnName,tableName);
                filesInDB.addAll(filesList);
            }
            //Make the ids in the list unique
            Set<BigInteger> set = new HashSet<>(filesInDB);
            filesInDB = new ArrayList<>(set);
            log.debug("FILE SYSTEM CLEAN UP - FILES REFERENCED IN DB COUNT: {}",filesInDB.size());
            //get all dut files from file storage
            Map<String, FileDTO> filesInFileSystem = getAllFilesInFileSystem(fileStoragePath);
            if(!CollectionUtils.isEmpty(filesInFileSystem)) {
                //Find which files are redundant
                Map<String, FileDTO> redundantFiles = new HashMap<>(filesInFileSystem);
                for (BigInteger fileID : filesInDB) {
                    //Get the 12 Characters file ID String
                    String fileId = getFileId(fileID);
                    redundantFiles.remove(fileId);
                }
                deleteRedundantFiles(redundantFiles);
            }
            else {
                log.debug("No files found referenced in the db. Clean up ABORTED.");
                throw new NotificationServiceException("no.files.referenced.in.db");
            }
            log.debug("Finished file system clean up");
        }
        catch (Exception e) {
            log.error("File System Clean up FAILED.", e);
        }
    }

    @Override
    public void scheduleStartCronJobFilesystemCleanUp() throws Exception {

    }

    @Override
    public void scheduleDeleteCronJobFilesystemCleanUp() throws Exception {

    }

    @Override
    public FileInfo getFileInfoByFileReferenceId(Long fileReferenceId) {
        return fileInfoRepository.findByFileRefId(fileReferenceId);
    }

    /* **********************A T T E N T I O N********** I M P O R T A N T ****************************
     * If you have introduced a new file ( new column ) it should be referenced in "fileUsages" list  *
     * in "FileInfo" class. Files in columns not referenced in fileUsages list will be deleted during *
     * file system clean up daily process.                                                            *
     * ************************************************************************************************
     *
     * (non-Javadoc)
     *
     * @see com.ed.ccm.service.FileService#storeFile(java.lang.BigInteger,
     * byte[])
     */
    private int storeFile(FileInfo fileInfo, byte[] content) throws NotificationServiceException{
        BigInteger fid = null;
        if (fileInfo.getId() <= 0) {
            createFileInfo(fileInfo).longValue();
        }
        fid = BigInteger.valueOf(fileInfo.getFileRefId());
        String[] paths = convertIdToPath(BigInteger.valueOf(fileInfo.getFileRefId()));
        File dir = new File(paths[0]);
        dir.mkdirs();
        // First we write to temp file and at the succesful end we rename to the target file.
        // So, we will not corrupt any existing file, or even have any corrupted new files
        File outFile = new File(dir, paths[1]);
        File tempFile = new File(TEMP_DIR, fid.toString());
        try {
            FileUtil.writeBytesToFile(tempFile, content);
            boolean success;
            outFile.delete();
            success = tempFile.renameTo(outFile);
            if (!success) {
                throw new IOException("Could not rename file: " + fid);
            }
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
            throw new NotificationServiceException("error.writing.file", ex);
        }
        return 1;
    }
    /**
     * Helper method for id to path transformation.
     *
     * @param fid
     *            Unique file identifier.
     * @return Relative path as array containing two elements:{absolute path,
     *         file name}
     */
    private String[] convertIdToPath(BigInteger fid) {
        String[] res = new String[2];
        String fileId = String.format("%012d", fid);
        //StringBuilder will allocate input.length + 16 bytes
        String sb = fileStoragePath + "/" + fileId.substring(0, 3) +
                "/" + fileId.substring(3, 6) +
                "/" + fileId.substring(6, 9);
        res[0] = sb;
        res[1] = fileId.substring(9);
        return res;
    }

    /**
     * @param path
     *            Path to which the input will be added
     * @param input
     *            the string that should be added to the path
     * @return Returns a final path with concatenated the path and the input
     */
    private String addToPath(String path, String input) {
        String retVal = "";
        if (path.endsWith(File.separator)) {
            retVal = path + input;
        } else {
            retVal = path + File.separator + input;
        }
        return retVal;
    }
}
