package com.cdp.zwy.buildbody.common.result;
import lombok.Data;


/**
 * @author zwy
 * @version 1.0
 * @description: Result
 * @date 2026/2/14 17:10
 */


@Data
public class Result<T> {
    private Integer code; // 200成功, 500失败, 401未登录
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    // 可扩展其他状态码方法...
}