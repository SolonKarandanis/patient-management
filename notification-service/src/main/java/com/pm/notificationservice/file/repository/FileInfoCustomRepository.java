package com.pm.notificationservice.file.repository;

import com.pm.notificationservice.file.model.FileInfo;
import com.pm.notificationservice.shared.exception.NotificationServiceException;

import java.math.BigInteger;
import java.util.List;

public interface FileInfoCustomRepository {

    BigInteger generateIdFromSequencer(String sequencerName);

    FileInfo findByFileRefId(Long fid);

    void deleteByFileRefIds(List<Long> fileReferencesToDelete);

    public List<BigInteger> getFileReferenceIdsForTableColumn(String columnName, String tableName)
            throws NotificationServiceException;
}
