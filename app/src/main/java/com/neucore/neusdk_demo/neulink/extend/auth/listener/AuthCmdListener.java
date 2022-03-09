package com.neucore.neusdk_demo.neulink.extend.auth.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthActionResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthItemResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.DeviceResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.DomainResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.LinkResult;
import com.neucore.neusdk_demo.neulink.extend.auth.request.AuthSyncCmd;

/**
 * 协议可以参考：授权下发 https://project.neucore.com/zentao/doc-view-82.html
 */
public class AuthCmdListener implements ICmdListener<AuthActionResult, AuthSyncCmd> {
    @Override
    public AuthActionResult/*Auth Action 返回处理结果*/ doAction(NeulinkEvent<AuthSyncCmd/*授权指令*/> event) {
        AuthSyncCmd cmd = event.getSource();
        /**
         * @TODO: 实现业务。。。
         */
        DeviceResult deviceResult = new DeviceResult();/*@TODO: 构造返回结果*/
        DomainResult domainResult = new DomainResult();/*@TODO: 构造返回结果*/
        LinkResult linkResult = new LinkResult();/*@TODO: 构造返回结果*/
        AuthItemResult authItemResult = new AuthItemResult();/*@TODO: 构造返回结果*/
        AuthActionResult result = new AuthActionResult();/*@TODO: 构造返回结果*/
        /**
         * @TODO: 构造返回结果
         */
        result.add(deviceResult);
        result.add(domainResult);
        result.add(linkResult);
        result.add(authItemResult);
        return result;
    }
}
