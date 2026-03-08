package com.pm.notificationservice.file.repository;

import com.pm.notificationservice.file.model.FileInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoRepository extends BaseRepository<FileInfo, Long>{
}
