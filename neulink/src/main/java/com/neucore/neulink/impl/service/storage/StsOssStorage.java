package com.neucore.neulink.impl.service.storage;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;

public class StsOssStorage extends OSSStorage{

    private OSS getOSSClient(String endpoint,String accessKey,String accessSecret,String securityToken) {
        OSSCredentialProvider credentialProvider =
                new OSSStsTokenCredentialProvider(accessKey ,accessSecret,securityToken);
        ClientConfiguration clientConfiguration = ClientConfiguration.getDefaultConf();

        int connectTimeOut = ConfigContext.getInstance().getConfig(ConfigContext.CONN_TIME_OUT,15*1000);
        int readTimeOut = ConfigContext.getInstance().getConfig(ConfigContext.READ_TIME_OUT,15*1000);

        clientConfiguration.setConnectionTimeout(connectTimeOut);
        clientConfiguration.setSocketTimeout(readTimeOut);

        return new OSSClient(ContextHolder.getInstance().getContext(), endpoint, credentialProvider, clientConfiguration);
    }
}
