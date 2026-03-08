package com.pm.notificationservice.file.repository;

import com.pm.notificationservice.file.model.FileInfo;
import com.pm.notificationservice.shared.exception.NotificationServiceException;

import java.math.BigInteger;
import java.util.List;

public class FileInfoCustomRepositoryImpl implements  FileInfoCustomRepository{
    @Override
    public BigInteger generateIdFromSequencer(String sequencerName) {
        return null;
    }

    @Override
    public FileInfo findByFileRefId(Long fid) {
        return null;
    }

    @Override
    public void deleteByFileRefIds(List<Long> fileReferencesToDelete) {

    }

    @Override
    public List<BigInteger> getFileReferenceIdsForTableColumn(String columnName, String tableName) throws NotificationServiceException {
        return List.of();
    }
}
