package com.e2buy.upload.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/9 15:57
 * @Desc:
 **/
public interface UploadService {
    /**
     * 上传图片
     * @param file
     * @return
     */
    String uploadImage(MultipartFile file);
}
