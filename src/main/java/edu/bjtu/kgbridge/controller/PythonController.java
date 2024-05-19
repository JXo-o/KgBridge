package edu.bjtu.kgbridge.controller;

import edu.bjtu.kgbridge.enums.ResultCodeEnum;
import edu.bjtu.kgbridge.model.Result;
import edu.bjtu.kgbridge.util.PythonCaller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ClassName: PythonController
 * Package: edu.bjtu.kgbridge.controller
 * Description: 用于设置Python路径并运行Python脚本
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/18 17:48
 */
@RestController
@RequestMapping("/python")
@Tag(name = "PythonController", description = "Python脚本操作接口")
public class PythonController {

    private static final Path BASE_DIR = Paths.get(System.getProperty("user.dir"), "python-service");
    private static final Path IFC_DIR = BASE_DIR.resolve("ifc_models");
    private static final Path SCRIPT_PATH = BASE_DIR.resolve("main.py");

    /**
     * 设置Python解释器路径
     *
     * @param pythonPath Python解释器路径
     * @return 设置结果
     */
    @Operation(summary = "设置Python路径", description = "设置系统中Python解释器的路径")
    @PostMapping("/setPythonPath")
    public Result<String> setPythonPath(
            @Parameter(description = "Python解释器的路径", required = true) @RequestParam("pythonPath") String pythonPath) {
        PythonCaller.setPythonPath(pythonPath);
        return Result.success("Python path set to: " + pythonPath);
    }

    /**
     * 运行Python脚本
     *
     * @param fileName IFC模型文件名称
     * @return 脚本运行结果
     */
    @Operation(summary = "运行Python脚本", description = "运行指定IFC模型文件的Python脚本")
    @PostMapping("/run")
    public Result<String> runPythonScript(
            @Parameter(description = "IFC模型文件名称", required = true) @RequestParam("fileName") String fileName
    ) {
        Path filePath = IFC_DIR.resolve(fileName);
        String output = PythonCaller.callPythonScript(SCRIPT_PATH.toString(), filePath.toString());

        if (output.contains("#####SUCCESSFUL#####")) {
            return Result.success(output);
        }
        return Result.fail(ResultCodeEnum.SERVER_ERROR, output);
    }
}