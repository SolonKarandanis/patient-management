package com.pm.notificationservice.file.service;

import com.pm.notificationservice.file.dto.FileDTO;
import com.pm.notificationservice.file.model.FileInfo;
import com.pm.notificationservice.shared.exception.NotificationServiceException;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface FileService {

    /**
     * @param content the content of the file in bytes
     * @param fileInfo the fileInfo entity
     * @return the ID from the sequencer of the created file
     * @throws NotificationServiceException
     */
    FileInfo createFile(byte[] content, FileInfo fileInfo) throws NotificationServiceException;
    /**
     * Method stores group of files on file system.
     *
     * @param dataMap
     *            Collection containing files id and content.
     * @return Number of files stored.
     * @throws NotificationServiceException
     */
    int createFiles(Map<FileInfo, byte[]> dataMap) throws NotificationServiceException;

    /**
     * Method checks if file exists.
     *
     * @param fid
     *            Unique file identifier.
     * @return true if exists, false otherwise.
     */
    boolean fileExistsInStorage(BigInteger fid);

    /**
     * Method retrieves file from disk and returns content.
     *
     * @param fid
     *            Unique file identifier.
     * @return File content as array of bytes.
     * @throws NotificationServiceException
     */
    byte[] getFileContentById(BigInteger fid) throws NotificationServiceException;
    /**
     * Deletes file from storage
     *
     * @param fid
     *            Unique file identifier.
     * @return true if success.
     */
    boolean deleteFile(BigInteger fid);

    boolean deleteFileInfo(BigInteger fid);

    boolean deleteOnFileSystem(BigInteger fid);
    /**
     * Unzips an existing saved file and returns a map with keys the zip entry names
     * (i.e. the filenames inside the zip) and value the fid of the saved entry.
     * The entries in the zip file have to be in a flat structure (no subfolders).
     *
     * @param fid
     *            Unique file identifier of the zip file that we would like to unzip.
     * @return A Map with keys the zip entry names (i.e. the filenames inside the zip) and value the fid of the saved
     *         entry.
     * @throws NotificationServiceException
     */
    Map<String, FileInfo> unzipFile(BigInteger fid) throws NotificationServiceException;

    FileInfo createFileInfo(FileInfo fileInfo) throws NotificationServiceException;

    /**
     * @param fileSystemPath
     *            The path that should be checked for files
     * @return Returns all files included in the specified path and subdirectories, excluding directories specified in
     *         constants
     */
    Map<String, FileDTO> getAllFilesInFileSystem(String fileSystemPath);
    /**
     * @param fid
     *            BigInteger Id of the file
     * @return Returns the 12 digit string of the Id
     */
    String getFileId(BigInteger fid);

    /**
     * @param path
     *            The path to start crawling
     * @param pathId
     *            The id of the file until this path
     * @param files
     *            The map of files.
     * @return Returns all the files under the initial path
     */
    Map<String, FileDTO> crawlDirectoryForFiles(String path, String pathId, Map<String, FileDTO> files);
    /**
     * Deletes all redundant files from the filesystem.
     *
     * @param ccmFilesToDelete
     *            Map that contains the redundant files in the filesystem
     */
    void deleteRedundantFiles(Map<String, FileDTO> ccmFilesToDelete);

    /**
     * This method finds which ccm files in file storage are not referenced in the db and deletes them.
     */
    void filesystemCleanUp();

    /**
     * Method to start the scheduler for filesystem cleanup
     *
     * @throws Exception
     */
    void scheduleStartCronJobFilesystemCleanUp() throws Exception;

    /**
     * Method to delete filesystem cleanup job
     *
     * @throws Exception
     */
    void scheduleDeleteCronJobFilesystemCleanUp() throws Exception;

    void deleteFiles(List<BigInteger> fileReferencesToDelete) throws Exception;

    FileInfo getFileInfoByFileReferenceId(Long fileReferenceId);
}
