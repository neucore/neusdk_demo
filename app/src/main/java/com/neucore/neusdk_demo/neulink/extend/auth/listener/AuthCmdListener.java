package com.neucore.neusdk_demo.neulink.extend.auth.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.AuthActionResultData;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.AuthActionResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.AuthItemResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.DeviceResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.DomainResult;
import com.neucore.neusdk_demo.neulink.extend.auth.listener.result.data.LinkResult;
import com.neucore.neusdk_demo.neulink.extend.auth.request.AuthSyncCmd;

/**
 * 协议可以参考：授权下发 https://project.neucore.com/zentao/doc-view-82.html
 * 云端下发至设备端的命令侦听器
 * 所有业务处理都在这地方处理
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

        AuthActionResultData data = new AuthActionResultData();/*@TODO 构造返回数据*/
        data.add(deviceResult);
        data.add(domainResult);
        data.add(linkResult);
        data.add(authItemResult);

        result.setData(data);

        return result;
    }
}
