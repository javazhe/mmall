package com.mmall.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by jia on 2018/6/10.
 */
public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);
    private static final String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static final String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static final String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private Integer port;
    private String username;
    private String pass;
    private FTPClient ftpClient;

    public FTPUtil(String ip, Integer port, String username, String pass) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.pass = pass;
    }

    public static boolean uploadFile(List<File> listFile) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始链接FTP服务器");
        boolean result = ftpUtil.uploadFile("img", listFile);
        logger.info("上传文件结束,状态:{}",result);
        return result;

    }

    private boolean uploadFile(String remotePath,List<File> listFile) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        if (connectService(this.ip,this.port,this.username,this.pass)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();//配置了被动模式 端口范围
                for (File item : listFile){
                    fis = new FileInputStream(item);
                    ftpClient.storeFile(item.getName(),fis);
                }
            } catch (IOException e) {
                uploaded = false;
                logger.info("上传文件出现异常",e);
                e.printStackTrace();
            }finally{
                ftpClient.disconnect();
                fis.close();
            }
        }
        return uploaded;
    }

    private boolean connectService(String ip, Integer port, String username, String pass){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
           ftpClient.connect(ip);
           isSuccess =  ftpClient.login(username,pass);
        } catch (IOException e) {
            logger.info("连接ＦＴＰ服务器出现异常",e);
        }
        return isSuccess;

    }



    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
