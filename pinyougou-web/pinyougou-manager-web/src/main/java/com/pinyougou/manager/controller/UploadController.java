package com.pinyougou.manager.controller;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NTP
 * @date 2018/11/24
 */
@RestController
public class UploadController {

    @Value("${fileServerUrl}")
    private String fileServerUrl;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @return Map<String,Object>
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile multipartFile) {

        Map<String, Object> data = new HashMap<>();
        data.put("status", 500);
        try {
            //1. 接收文件
            //1.1 原文件名
            String originalFilename = multipartFile.getOriginalFilename();
            //1.2 获取如片的字节数组
            byte[] fileBytes = multipartFile.getBytes();

            //2. 把文件上传到fastdfs服务器
            //2.1.加载配置文件，得到文件路径
            String confFileName = this.getClass().getResource("/fastdfs-client.conf").getPath();

            //2.2.初始化客户端全局对象
            ClientGlobal.init(confFileName);

            //2.3.创建存储客户端对象
            StorageClient storageClient = new StorageClient();

            //2.4.上传文件
            String[] arr = storageClient.upload_file(fileBytes, FilenameUtils.getExtension(originalFilename), null);
            //访问：http://192.168.12.131/group1/M00/00/01/wKgMg1v44-CAFjlLAAlIz57uzS0024.gif
            //[group1, M00/00/01/wKgMg1v44-CAFjlLAAlIz57uzS0024.gif]
            //第一个元素：组名
            //第二个元素：远程文件名称

            //3. 封装返回数据
            StrBuilder url = new StrBuilder(fileServerUrl);
            for (String str : arr) {
                url.append("/" + str);
            }
            data.put("url", url.toString());
            data.put("status", 200);

            return data;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
