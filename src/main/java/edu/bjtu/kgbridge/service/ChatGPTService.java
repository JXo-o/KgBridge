package edu.bjtu.kgbridge.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * ClassName: ChatGPTService
 * Package: edu.bjtu.kgbridge.service
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/21 20:29
 */
@Service
public class ChatGPTService {

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    /**
     * 调用ChatGPT接口进行处理
     * @param prompt 用户输出
     * @return gpt输出
     */
    public String callChatGPT(String prompt) {

        if (prompt.startsWith("###"))
            prompt = wrapPrompt(prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", prompt)));
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.5);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        JSONObject responseObject = new JSONObject(response.getBody());
        return responseObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
    }

    private String wrapPrompt(String prompt) {
        prompt = """
            请将以下描述的建筑标准和要求拆分为多个四元组。每个四元组应该包括“对象”、“属性”、“比较符”和“值”。
            如果不存在“比较符”，请按照主谓宾的三元组形式拆分。
            多个四元组之间使用#隔开。
    
            定义:
            - 对象: 具体的实体或事物
            - 属性: 对象的特征或描述
            - 比较符: 对属性的比较或约束关系
            - 值: 属性的具体数值或状态
    
            例子：
            输入："泄水孔的直径不应小于50mm"
            输出：(泄水孔, 直径, 不小于, 50)
            
            例子：
            输入："桥面应采用钢筋混凝土结构"
            输出：(桥面, 结构, 钢筋混凝土)
    
            输入："顶帽的混凝土强度不宜小于C35，厚度不应低于0.4m"
            输出：(顶帽, 强度, 不小于, C35)#(顶帽, 厚度, 不小于, 0.4)
            
            输入："桥上栏杆踏面以上的高度不宜小于1.1m。"
            输出：(栏杆, 高度, 不小于, 1.1)
    
            请处理以下内容：
            """ + prompt.substring(3) + """
            
            输出应为：""";
        return prompt;
    }

}