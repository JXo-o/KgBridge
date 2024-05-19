package edu.bjtu.kgbridge.controller;

import edu.bjtu.kgbridge.enums.ResultCodeEnum;
import edu.bjtu.kgbridge.model.Result;
import edu.bjtu.kgbridge.service.JenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * ClassName: JenaInferenceController
 * Package: edu.bjtu.kgbridge.controller
 * Description: 用于处理Jena推理操作
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/18 23:02
 */
@RestController
@RequestMapping("/jena")
@Tag(name = "JenaInferenceController", description = "Jena推理操作接口")
public class JenaInferenceController {

    private final JenaService jenaService;

    public JenaInferenceController(JenaService jenaService) {
        this.jenaService = jenaService;
    }

    /**
     * 执行推理并返回推理结果
     *
     * @return 推理结果
     */
    @Operation(summary = "执行推理并返回推理结果", description = "使用Jena自定义规则对本体文件进行推理，并返回推理结果")
    @GetMapping("/infer")
    public Result<String> performInference() {
        try {
            String inferenceResults = jenaService.performInference();
            return Result.success(inferenceResults);
        } catch (IOException e) {
            return Result.fail(ResultCodeEnum.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return Result.fail(ResultCodeEnum.SERVER_ERROR, "Inference error: " + e.getMessage());
        }
    }

    /**
     * 查看Jena规则文件中的规则
     *
     * @return 规则文件内容
     */
    @Operation(summary = "查看Jena规则文件中的规则", description = "读取并返回Jena规则文件中的规则")
    @GetMapping("/rules")
    public Result<String> getRules() {
        try {
            String rulesContent = jenaService.getRules();
            return Result.success(rulesContent);
        } catch (IOException e) {
            return Result.fail(ResultCodeEnum.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return Result.fail(ResultCodeEnum.SERVER_ERROR, "Error reading rules: " + e.getMessage());
        }
    }

}