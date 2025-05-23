package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    private static String FILE_UPLOAD_PATH = "/home/xxxxxl/java/hm_ziliao/cqwm/sky-take-out/sky-server/src/main/resources/upload/";

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        File dir = new File(FILE_UPLOAD_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            boolean created = dir.mkdirs();
            if(created) {
                log.info("创建文件夹成功: {}", FILE_UPLOAD_PATH);
            } else {
                log.warn("创建文件夹失败或已经存在: {}", FILE_UPLOAD_PATH);
            }
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error("文件名无效");
        }
        // 获取后缀
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!extension.equalsIgnoreCase(".png") && !extension.equalsIgnoreCase(".jpg") && !extension.equalsIgnoreCase(".jpeg")) {
            return Result.error("文件格式不支持");
        }
        // 拼接新的随机名称
        originalFilename = UUID.randomUUID().toString() + extension;


        // 确保文件路径安全，避免路径遍历攻击
        Path targetLocation = Paths.get(FILE_UPLOAD_PATH).resolve(originalFilename).normalize();
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("文件上传成功: {}", originalFilename);
        } catch (IOException e) {
            log.error("文件上传失败: {}", originalFilename, e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

        // 你可以根据实际情况调整返回的文件访问链接
        String fileUrl = "http://127.0.0.1:8080/upload/" + originalFilename;
        return Result.success(fileUrl);
    }

}
