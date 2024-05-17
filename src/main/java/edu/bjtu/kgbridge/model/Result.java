package edu.bjtu.kgbridge.model;

import edu.bjtu.kgbridge.enums.ResultCodeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName: Result
 * Package: edu.bjtu.kgbridge.model
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/18 0:13
 */
@Data
@NoArgsConstructor
public class Result<T> implements Serializable {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 描述
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;

    public Result(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
        this.data = null;
    }

    public Result(ResultCodeEnum resultCodeEnum, T data) {
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
        this.data = data;
    }

    public Result(ResultCodeEnum resultCodeEnum, String message) {
        this.code = resultCodeEnum.getCode();
        this.message = message;
        this.data = null;
    }

    public Result(ResultCodeEnum resultCodeEnum, String message, T data) {
        this.code = resultCodeEnum.getCode();
        this.message = message;
        this.data = data;
    }

    /**
     * 响应成功
     *
     * @return 对应的前端返回对象
     * @param <T> 返回参数类型
     */
    public static <T> Result<T> success() {
        return new Result<T>(ResultCodeEnum.SUCCESS);
    }

    /**
     * 响应成功
     *
     * @param message 响应信息
     * @param data 响应数据
     * @return 对应的前端返回对象
     * @param <T> 返回参数类型
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCodeEnum.SUCCESS, message, data);
    }

    /**
     * 响应成功
     *
     * @param resultCodeEnum 前端返回对象状态枚举
     * @return 对应的前端返回对象
     * @param <T> 返回参数类型
     */
    public static <T> Result<T> success(ResultCodeEnum resultCodeEnum) {
        return new Result<T>(resultCodeEnum);
    }

    /**
     * 响应成功
     *
     * @param data 传递数据
     * @return 返回对应对象
     * @param <T> 返回参数类型
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCodeEnum.SUCCESS, data);
    }

    /**
     * 响应成功
     *
     * @param resultCodeEnum 前端返回对象状态枚举
     * @param data 传递数据
     * @return 对应的前端返回对象
     * @param <T> 返回参数类型
     */
    public static <T> Result<T> success(ResultCodeEnum resultCodeEnum, T data) {
        return new Result<>(resultCodeEnum, data);
    }

    /**
     * 响应失败
     *
     * @return 对应的前端返回对象
     * @param <T> 返回参数类型
     */
    public static <T> Result<T> fail() {
        return new Result<T>(ResultCodeEnum.SERVER_ERROR);
    }

    /**
     * 响应失败
     *
     * @param resultCodeEnum 前端返回对象状态枚举
     * @return 对应的前端返回对象
     * @param <T> 返回参数类型
     */
    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum) {
        return new Result<T>(resultCodeEnum);
    }

    /**
     * 响应失败
     *
     * @param resultCodeEnum 前端返回对象状态枚举
     * @param message 响应信息
     * @return 对应的前端返回对象
     * @param <T> 返回参数类型
     */
    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum, String message) {
        return new Result<T>(resultCodeEnum, message);
    }

    /**
     * 响应失败
     *
     * @param resultCodeEnum 前端返回对象状态枚举
     * @param data 传递数据
     * @return 对应的前端返回对象
     * @param <T> 返回参数类型
     */
    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum, T data) {
        return new Result<>(resultCodeEnum, data);
    }

}