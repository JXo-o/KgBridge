package edu.bjtu.kgbridge.controller;

import edu.bjtu.kgbridge.model.Result;
import edu.bjtu.kgbridge.service.ChatGPTService;
import edu.bjtu.kgbridge.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;

/**
 * ClassName: ChatGPTController
 * Package: edu.bjtu.kgbridge.controller
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/21 20:37
 */
@RestController
@RequestMapping("/gpt")
public class ChatGPTController {

    private static final Path BASE_DIR = Path.of(
            System.getProperty("user.dir"),
            "python-service",
            "input_data"
    );

    private final ChatGPTService chatGPTService;

    @Autowired
    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @Operation(summary = "对话", description = "使用ChatGPT进行对话")
    @GetMapping("/ask")
    public Result<String> askChat(@RequestParam String prompt) {
        String response = chatGPTService.callChatGPT(prompt);
        System.out.println(response.getClass());
        System.out.println(response);
        return Result.success(response);
    }

    /**
     * 利用ChatGPT对输入标准规范进行划分
     *
     * @param prompt 提示词
     * @param fileName 保存至的文件夹
     * @return 划分后的结果
     */
    @Operation(summary = "划分标准规范", description = "利用ChatGPT对输入标准规范进行划分")
    @GetMapping("/divide")
    public Result<String> divideInput(
            @Parameter(description = "提示词", required = true) @RequestParam String prompt,
            @RequestParam(defaultValue = "ner_label", required = false) String fileName
    ) {
        String response = chatGPTService.callChatGPT("###" + prompt);

        return new FileUtil(BASE_DIR).writeTuplesToFile(response, fileName);
    }

    /**
     * 读取标准规范输入文件，利用ChatGPT进行划分
     *
     * @param fileName 保存至的文件
     * @return 划分后的结果
     */
    @Operation(summary = "划分标准规范", description = "读取标准规范输入文件，利用ChatGPT进行划分")
    @GetMapping("/divide-file")
    public Result<String> divideNerInput(
            @RequestParam(defaultValue = "ner_label", required = false) String fileName
    ) {
        FileUtil fileUtil = new FileUtil(BASE_DIR);
        List<String> result = fileUtil.readFile("ner_input").getData();
        String prompt = String.join(", ", result);
        System.out.println(prompt);
        String response = chatGPTService.callChatGPT("###" + prompt);

        return new FileUtil(BASE_DIR).writeTuplesToFile(response, fileName);
    }

}