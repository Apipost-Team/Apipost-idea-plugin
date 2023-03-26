package com.wwr.apipost.handle.apipost.domain;

/**
 * <p>
 *
 * </p>
 *
 * @author wwr
 * @version 1.0
 * @date 2023/3/25
 * 
 * @since 1.0.1
 */
public class ApiPostSyncResponseVO<T> {

    private Integer code;

    private String msg;

    private T data;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public Boolean isSuccess() {
        return this.code == 0;
    }


    @Override
    public String toString() {
        return "ApiPostSyncResponseVO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
