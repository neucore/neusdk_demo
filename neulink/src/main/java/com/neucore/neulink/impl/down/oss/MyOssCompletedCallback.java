package com.neucore.neulink.impl.down.oss;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.ResumableDownloadRequest;
import com.alibaba.sdk.android.oss.model.ResumableDownloadResult;

public class MyOssCompletedCallback implements OSSCompletedCallback<ResumableDownloadRequest, ResumableDownloadResult> {

    private ResumableDownloadRequest request;
    private ResumableDownloadResult result;
    private ClientException clientException;
    private ServiceException serviceException;

    @Override
    public void onSuccess(ResumableDownloadRequest request, ResumableDownloadResult result) {
        this.request = request;
        this.result = result;
    }

    @Override
    public void onFailure(ResumableDownloadRequest request, ClientException clientException, ServiceException serviceException) {
        this.request = request;
        this.clientException = clientException;
        this.serviceException = serviceException;
    }

    public ResumableDownloadRequest getRequest() {
        return request;
    }

    public void setRequest(ResumableDownloadRequest request) {
        this.request = request;
    }

    public ResumableDownloadResult getResult() {
        return result;
    }

    public void setResult(ResumableDownloadResult result) {
        this.result = result;
    }

    public ClientException getClientException() {
        return clientException;
    }

    public void setClientException(ClientException clientException) {
        this.clientException = clientException;
    }

    public ServiceException getServiceException() {
        return serviceException;
    }

    public void setServiceException(ServiceException serviceException) {
        this.serviceException = serviceException;
    }
}
