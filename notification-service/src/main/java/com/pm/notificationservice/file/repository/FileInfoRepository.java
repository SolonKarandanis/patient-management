package com.pm.notificationservice.file.repository;

import com.pm.notificationservice.file.model.FileInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface FileInfoRepository extends BaseRepository<FileInfo, Long>{

    @Query(nativeQuery = true,value = "SELECT nextval(:sequencerName)")
    BigInteger generateIdFromSequencer(String sequencerName);

    @Query("SELECT fileInfo FROM FileInfo fileInfo WHERE fileInfo.fileRefId= :fid")
    FileInfo findByFileRefId(Long fid);

    @Query(nativeQuery = true,value = "SELECT o.:columnName as filereference FROM :tableName o WHERE o.:columnName IS NOT NULL")
    List<BigInteger> getFileReferenceIdsForTableColumn(String columnName, String tableName);

    default void deleteByFileRefIdsInBatch(List<Long> fileReferencesToDelete){
        if(!CollectionUtils.isEmpty(fileReferencesToDelete)){
            int batchSize = 1000;
            int size = fileReferencesToDelete.size();
            int times = (size / batchSize);
            times = (size % batchSize > 0) ? times + 1 : times;
            int index = 0;
            int toIndex = 0;
            for (int i = 1; i <= times; i++){
                toIndex = (i == times) ? size : index + batchSize;
                List<Long> sublist = fileReferencesToDelete.subList(index, toIndex);
                deleteByFileRefIds(sublist);
            }
        }
    }

    @Modifying
    @Query("DELETE FROM FileInfo fi WHERE fi.fileRefId in (:fileReferencesToDelete)")
    void deleteByFileRefIds(List<Long> fileReferencesToDelete);
}
