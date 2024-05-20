package edu.bjtu.kgbridge.controller;

import edu.bjtu.kgbridge.enums.ResultCodeEnum;
import edu.bjtu.kgbridge.model.Result;
import edu.bjtu.kgbridge.util.PythonCaller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
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

    @Operation(summary = "设置Python路径", description = "设置系统中Python解释器的路径")
    @PostMapping("/setPythonPath")
    public Result<String> setPythonPath(
            @Parameter(description = "Python解释器的路径", required = true) @RequestParam("pythonPath") String pythonPath) {
        PythonCaller.setPythonPath(pythonPath);
        return Result.success("Python path set to: " + pythonPath);
    }

    @Operation(summary = "形成桥梁图谱", description = "运行根据标准规范图谱，解析ifc模型，形成桥梁图谱的Python脚本")
    @PostMapping("/run")
    public Result<String> runIfcScript(
            @Parameter(description = "IFC模型文件名称", required = true) @RequestParam("fileName") String fileName
    ) {
        Path filePath = IFC_DIR.resolve(fileName);
        if (!Files.exists(filePath)) {
            return Result.fail(ResultCodeEnum.NOT_FOUND, "File not found: " + fileName);
        }
        return PythonCaller.callPythonScript(SCRIPT_PATH.toString(), filePath.toString());
    }

    @Operation(summary = "执行ner模块", description = "运行命名实体识别的Python脚本")
    @PostMapping("/ner")
    public Result<String> runNerScript() {
        return PythonCaller.callPythonScript(SCRIPT_PATH.toString(), "ner");
    }

    @Operation(summary = "执行标准规范本体生成", description = "运行标准规范本体生成的Python脚本")
    @PostMapping("/std")
    public Result<String> runStdScript() {
        return PythonCaller.callPythonScript(SCRIPT_PATH.toString(), "standard");
    }

    @Operation(summary = "形成标准规范图谱", description = "运行在标准规范本体中插入数据的Python脚本")
    @PostMapping("/standard")
    public Result<String> runStandardScript() {
        return PythonCaller.callPythonScript(SCRIPT_PATH.toString(), "standard_data");
    }

    @Operation(summary = "执行桥梁本体生成", description = "运行桥梁本体生成的Python脚本")
    @PostMapping("/bridge")
    public Result<String> runBridgeScript() {
        return PythonCaller.callPythonScript(SCRIPT_PATH.toString(), "bridge");
    }

    @Operation(summary = "执行标准规范图谱转Jena规则", description = "运行标准规范图谱转Jena规则的Python脚本")
    @PostMapping("/rule")
    public Result<String> runRuleScript() {
        return PythonCaller.callPythonScript(SCRIPT_PATH.toString(), "rule");
    }

}