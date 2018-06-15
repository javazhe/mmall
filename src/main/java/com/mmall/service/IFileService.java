package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by jia on 2018/6/10.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);

}
