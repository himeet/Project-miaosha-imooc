package com.imooc.result;

import lombok.Getter;

@Getter
public class Result<T> {

    private int code;

    private String msg;

    private T data;

    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg cm) {
        if (cm == null) {
            return;
        }
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }

    /**
     * 成功时调用
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     * 失败时调用
     * @param cm
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(CodeMsg cm) {
        return new Result<T>(cm);
    }

}
