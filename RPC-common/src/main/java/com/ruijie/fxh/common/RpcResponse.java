package com.ruijie.fxh.common;

/**
 * Created by Ruijie on 2015/12/2.
 * 对Rpc相应内容的封装
 */
public class RpcResponse {
    private String requestId;
    private Throwable error;
    private Object result;
    public boolean isError() {
        return error != null;
    }
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


}
