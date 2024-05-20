package edu.bjtu.kgbridge.controller;

import edu.bjtu.kgbridge.enums.ResultCodeEnum;
import edu.bjtu.kgbridge.model.Result;
import edu.bjtu.kgbridge.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * ClassName: InputDataController
 * Package: edu.bjtu.kgbridge.controller
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/20 18:21
 */
@RestController
@RequestMapping("/input")
@Tag(name = "InputDataController", description = "管理input_data")
public class InputDataController {

    private static final Path BASE_DIR = Path.of(
            System.getProperty("user.dir"),
            "python-service",
            "input_data"
    );

    /**
     * 列出input_data中文件
     *
     * @return 文件名列表
     */
    @Operation(summary = "列出input_data中文件", description = "列出input_data中文件")
    @GetMapping("/list")
    public Result<List<String>> listFiles() {
        return new FileUtil(BASE_DIR).listFiles("");
    }

    /**
     * 读取指定文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     */
    @Operation(summary = "读取文件内容", description = "读取服务器上指定文件的内容")
    @GetMapping("/read")
    public Result<List<String>> readFile(@RequestParam String fileName) {
        return new FileUtil(BASE_DIR).readFile(fileName);
    }


    /**
     * 修改指定ner_label文件内容
     *
     * @param lineNumbers 行号列表
     * @param elementIndexes 元素索引列表
     * @param newValues 新值列表
     * @return 修改结果
     */
    @Operation(summary = "修改文件内容", description = "修改服务器上ner_label文件的内容")
    @PostMapping("/modify")
    public Result<Void> modifyFile(@RequestParam List<Integer> lineNumbers,
                                   @RequestParam List<Integer> elementIndexes,
                                   @RequestParam List<String> newValues) {
        try {
            new FileUtil(BASE_DIR).modifyFile(lineNumbers, elementIndexes, newValues);
            return Result.success(null);
        } catch (IOException e) {
            return Result.fail(ResultCodeEnum.SERVER_ERROR, "Could not modify file: ner_label");
        } catch (IllegalArgumentException e) {
            return Result.fail(ResultCodeEnum.FAILED, e.getMessage());
        }
    }

}