package xyz.raysmen.lp.cos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.raysmen.lp.common.exception.BusinessException;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.cos.service.FileService;

import java.io.IOException;
import java.io.InputStream;

/**
 * FileController
 * 文件操作控制器类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.cos.controller
 * @date 2022/06/02 19:57
 */
@RestController
@RequestMapping("/api/cos/file")
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public CustomResult upload(@RequestParam("file") MultipartFile file,
                               @RequestParam("module") String module) {
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String uploadUrl = fileService.upload(inputStream, module, originalFilename);

            return CustomResult.ok().message("文件上传成功").data("url", uploadUrl);
        } catch (IOException e) {
            throw new BusinessException(e, ResponseEnum.UPLOAD_ERROR);
        }
    }

    @DeleteMapping("/remove")
    public CustomResult remove(@RequestParam("url") String url) {
        fileService.removeFile(url);
        return CustomResult.ok().message("删除成功");
    }
}
