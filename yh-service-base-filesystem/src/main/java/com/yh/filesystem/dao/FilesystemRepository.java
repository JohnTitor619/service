package com.yh.filesystem.dao;

import com.lxw.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @AUTHOR: yadong
 * @DATE: 2021/7/19 16:12
 * @DESC:
 */
public interface FilesystemRepository extends MongoRepository<FileSystem,String> {
}
