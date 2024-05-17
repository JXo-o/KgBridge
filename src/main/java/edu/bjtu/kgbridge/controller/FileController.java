package edu.bjtu.kgbridge.controller;

import edu.bjtu.kgbridge.enums.ResultCodeEnum;
import edu.bjtu.kgbridge.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: FileController
 * Package: edu.bjtu.kgbridge.controller
 * Description: 用于处理ifc文件的上传等操作
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/18 0:08
 */
@RestController
@RequestMapping("/ifc")
@Api(value = "FileController", tags = {"IFC文件操作接口"})
public class FileController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "model";

    /**
     * 上传ifc模型文件到服务器，方便后续处理
     *
     * @param file ifc模型文件
     * @return 上传结果
     */
    @ApiOperation(value = "上传IFC模型文件", notes = "上传IFC模型文件到服务器，文件大小限制在配置中设置")
    @PostMapping("/upload")
    public ResponseEntity<Result<String>> uploadFile(
            @ApiParam(value = "IFC模型文件", required = true) @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(ResultCodeEnum.PARAM_IS_BLANK.getCode())
                    .body(Result.fail(ResultCodeEnum.PARAM_IS_BLANK));
        }

        try {
            // 创建目录如果不存在
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 获取文件并保存到指定目录
            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return ResponseEntity.status(ResultCodeEnum.SUCCESS.getCode())
                    .body(Result.success("File uploaded successfully: " + fileName));
        } catch (IOException e) {
            return ResponseEntity.status(ResultCodeEnum.SERVER_ERROR.getCode())
                    .body(Result.fail(ResultCodeEnum.SERVER_ERROR, e.getMessage()));
        }
    }

    /**
     * 列出服务器当前的ifc模型文件
     *
     * @return 文件名列表
     */
    @ApiOperation(value = "列出IFC模型文件", notes = "列出服务器上已上传的所有IFC模型文件")
    @GetMapping("/list")
    public Result<List<String>> listFiles() {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists() || !directory.isDirectory()) {
            return Result.fail(ResultCodeEnum.NOT_FOUND, "Directory not found");
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".ifc"));
        if (files == null) {
            return Result.fail(ResultCodeEnum.SERVER_ERROR, "Could not list files");
        }

        List<String> fileNames = Arrays.stream(files)
                .map(File::getName)
                .collect(Collectors.toList());

        return Result.success(fileNames);
    }
}