package cn.van.distributed.packet.util;

import lombok.Data;

/**
 * Copyright (C), 2015-2020, 风尘博客
 * 公众号 : 风尘博客
 * FileName: HttpResult
 *
 * @author: Van
 * Date:     2020-02-11 22:27
 * Description: ${DESCRIPTION}
 * Version： V1.0
 */
@Data
public class HttpResult {

    /**
     * 响应状态
     */
    protected boolean status;

    /**
     * 响应代码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应结果
     */
    private Object data;

    public HttpResult() {
        this.status = true;
    }


    /**
     * 成功
     * @param data
     * @return
     */
    public static HttpResult success(Object data) {
        HttpResult httpResult = new HttpResult();
        httpResult.setStatus(true);
        httpResult.setCode(200);
        httpResult.setData(data);
        return httpResult;
    }

    public static  HttpResult success() {
        HttpResult httpResult = new HttpResult();
        httpResult.setStatus(true);
        httpResult.setCode(200);
        return httpResult;
    }

    public static  HttpResult failure(int code, String msg) {
        HttpResult result = new HttpResult();
        result.setStatus(false);
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }
}
