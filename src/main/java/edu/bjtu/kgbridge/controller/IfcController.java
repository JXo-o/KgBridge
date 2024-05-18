package edu.bjtu.kgbridge.controller;

import edu.bjtu.kgbridge.enums.ResultCodeEnum;
import edu.bjtu.kgbridge.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ClassName: IfcController
 * Package: edu.bjtu.kgbridge.controller
 * Description: 用于处理ifc文件的上传等操作
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/18 0:08
 */
@RestController
@RequestMapping("/ifc")
@Tag(name = "IfcController", description = "IFC文件操作接口")
public class IfcController {

    private static final Path BASE_DIR = Paths.get(System.getProperty("user.dir"), "python-service");
    private static final Path UPLOAD_DIR = BASE_DIR.resolve("ifc_models");

    /**
     * 上传ifc模型文件到服务器，方便后续处理
     *
     * @param file ifc模型文件
     * @return 上传结果
     */
    @Operation(summary = "上传IFC模型文件", description = "上传IFC模型文件到服务器，文件大小限制在配置中设置")
    @PostMapping("/upload")
    public ResponseEntity<Result<String>> uploadFile(
            @Parameter(description = "IFC模型文件", required = true) @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(ResultCodeEnum.PARAM_IS_BLANK.getCode())
                    .body(Result.fail(ResultCodeEnum.PARAM_IS_BLANK));
        }

        try {
            if (!Files.exists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = UPLOAD_DIR.resolve(fileName);
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
    @Operation(summary = "列出IFC模型文件", description = "列出服务器上已上传的所有IFC模型文件")
    @GetMapping("/list")
    public Result<List<String>> listFiles() {
        if (!Files.exists(UPLOAD_DIR) || !Files.isDirectory(UPLOAD_DIR)) {
            return Result.fail(ResultCodeEnum.NOT_FOUND, "Directory not found");
        }

        try (Stream<Path> paths = Files.list(UPLOAD_DIR)) {
            List<String> fileNames = paths
                    .filter(path -> path.toString().toLowerCase().endsWith(".ifc"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

            return Result.success(fileNames);
        } catch (IOException e) {
            return Result.fail(ResultCodeEnum.SERVER_ERROR, "Could not list files");
        }
    }
}