package com.e2buy.upload.service.impl;

import com.e2buy.upload.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/9 15:57
 * @Desc:
 **/
@Service
public class UploadServiceImpl implements UploadService {


    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    @Override
    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        // 校验文件的类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)){
            // 文件类型不合法，直接返回null
            LOGGER.info("文件类型不合法：{}", originalFilename);
            return null;
        }
        try {
            // 校验文件的内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null){
                LOGGER.info("文件内容不合法：{}", originalFilename);
                return null;
            }

            // 保存到服务器
            file.transferTo(new File("C:\\Users\\Administrator\\Desktop\\image\\" + originalFilename));

            // 生成url地址，返回
            return "http://image.e2buy.com/" + originalFilename;
        } catch (IOException e) {
            LOGGER.info("服务器内部错误：{}", originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
