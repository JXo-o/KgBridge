package edu.bjtu.kgbridge.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ClassName: ResultCodeEnum
 * Package: edu.bjtu.kgbridge.enums
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/17 23:53
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    /**
     * 请求成功，状态码为 200
     */
    SUCCESS(200, "请求成功"),

    /**
     * 请求失败，状态码为 1000
     */
    FAIL(1000, "请求失败"),

    // 客户端错误
    FAILED(400, "客户端错误"),
    UNAUTHORIZED(401, "当前请求未授权访问"),
    FORBIDDEN(403, "权限不足，服务器拒绝访问"),
    NOT_FOUND(404, "资源未找到"),
    CONFIGURATION_ERROR(410, "系统配置出错"),
    RABBITMQ_CONFIGURATION_ERROR(411, "RabbitMQ配置出错"),
    RABBITMQ_DATA_CONVERT_ERROR(412, "RabbitMQ数据转发出错"),
    SERVER_ERROR(500, "服务器内部错误"),
    EMAIL_TEMPLATE_NOT_FOUNT_ERROR(413, "未找到相应邮件模板"),

    // 错误参数
    PARAM_IS_INVALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述信息
     */
    private final String message;
}
