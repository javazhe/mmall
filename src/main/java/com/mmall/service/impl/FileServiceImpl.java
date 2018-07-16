package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by jia on 2018/6/10.
 */
@Service("iFileServic")
public class FileServiceImpl implements IFileService {
    //加日志
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    //把上传后的文件,名返回回去
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //获取扩展名jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //为了避免相同文件名出现被覆盖的情况出现
        String uploadFileName = UUID.randomUUID().toString()+"."+ fileExtensionName;
        logger.info("开始上传文件,上传的文件按名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);
        File filedir = new File(path);
        if (!filedir.exists()){
            filedir.setWritable(true);
            filedir.mkdirs();
        }
        File torgetFile = new File(path,uploadFileName);

        try {
            file.transferTo(torgetFile);
            // 将torgetFile 上传到FTP服务器上

            FTPUtil.uploadFile(Lists.newArrayList(torgetFile));

            //上传完后删除upload下面的文件
            torgetFile.delete();

        } catch (IOException e) {
           logger.info("上传文件异常",e);
             return null;
        }
        return torgetFile.getName();
    }
}
