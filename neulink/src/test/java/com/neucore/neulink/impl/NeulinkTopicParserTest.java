package com.neucore.neulink.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class NeulinkTopicParserTest {

    private NeulinkTopicParser parser = NeulinkTopicParser.getInstance();

    // ==================== cloud2EndParser (云端→设备) ====================

    @Test
    public void testCloud2End_NewFormat_Req() {
        // {productId}/req/{devId}/{biz}/{requesterClientId}
        String topicStr = "smarthome/req/dev001/devinfo/cloudClient01";
        NeulinkTopicParser.Topic t = parser.cloud2EndParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("req", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("dev001", t.getDevId());
        assertEquals("devinfo", t.getBiz());
        assertEquals("cloudClient01", t.getRequestorClientId());
    }

    @Test
    public void testCloud2End_NewFormat_Bcst() {
        // {productId}/bcst/{biz}
        String topicStr = "smarthome/bcst/qlog";
        NeulinkTopicParser.Topic t = parser.cloud2EndParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("bcst", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("qlog", t.getBiz());
    }

    @Test
    public void testCloud2End_NewFormat_Req_NoRequester() {
        // {productId}/req/{devId}/{biz} （无requesterClientId）
        String topicStr = "smarthome/req/dev001/devinfo";
        NeulinkTopicParser.Topic t = parser.cloud2EndParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("req", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("dev001", t.getDevId());
        assertEquals("devinfo", t.getBiz());
        assertNull(t.getRequestorClientId());
    }

    @Test
    public void testCloud2End_OldFormat_NoProduct() {
        // rmsg/req/{devId}/sys_ctrl/v1.0/{reqNo}/{md5}
        String topicStr = "rmsg/req/dev001/sys_ctrl/v1.0/req001/md5abc";
        NeulinkTopicParser.Topic t = parser.cloud2EndParser(topicStr);
        assertNotNull(t);
        assertEquals("rmsg", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("sys_ctrl", t.getBiz());
        assertEquals("v1.0", t.getVersion());
        assertEquals("req001", t.getReqId());
        assertEquals("md5abc", t.getMd5());
    }

    @Test
    public void testCloud2End_V12Format_BcstReq_WithScopeId() {
        // ${productId}/bcst/req/[${scopeId}]/vx.x
        String topicStr = "smarthome/bcst/req/scope001/v1d0";
        NeulinkTopicParser.Topic t = parser.cloud2EndParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("bcst", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("scope001", t.getCustId());
        assertEquals("v1d0", t.getVersion());
    }

    @Test
    public void testCloud2End_V12Format_BcstReq_WithoutScopeId() {
        // ${productId}/bcst/req/[${scopeId}]/vx.x 中scopeId缺省
        String topicStr = "smarthome/bcst/req/v1d0";
        NeulinkTopicParser.Topic t = parser.cloud2EndParser(topicStr);
        assertNotNull(t);
        assertEquals("bcst", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertNull(t.getCustId());
        assertEquals("v1d0", t.getVersion());
    }

    @Test
    public void testCloud2End_V12Format_BcstRes() {
        // ${productId}/bcst/res/[${biz}]/vx.x[/${devId}][/${reqNo}]
        String topicStr = "smarthome/bcst/res/qlog/v1d0/dev001/rn001";
        NeulinkTopicParser.Topic t = parser.cloud2EndParser(topicStr);
        assertNotNull(t);
        assertEquals("bcst", t.getGroup());
        assertEquals("res", t.getReq$res());
        assertEquals("qlog", t.getBiz());
        assertEquals("v1d0", t.getVersion());
        assertEquals("dev001", t.getDevId());
        assertEquals("rn001", t.getReqId());
    }

    // ==================== end2cloudParser (设备→云端) ====================

    @Test
    public void testEnd2Cloud_NewFormat_Evt_Connect() {
        // {productId}/evt/{devId}/connect
        String topicStr = "smarthome/evt/dev001/connect";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("evt", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("dev001", t.getDevId());
        assertEquals("connect", t.getBiz());
    }

    @Test
    public void testEnd2Cloud_NewFormat_Evt_Disconnect() {
        // {productId}/evt/{devId}/disconnect
        String topicStr = "smarthome/evt/dev001/disconnect";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertEquals("dev001", t.getDevId());
        assertEquals("disconnect", t.getBiz());
    }

    @Test
    public void testEnd2Cloud_NewFormat_Evt_Lwt() {
        // {productId}/evt/{devId}/lwt
        String topicStr = "smarthome/evt/dev001/lwt";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertEquals("dev001", t.getDevId());
        assertEquals("lwt", t.getBiz());
    }

    @Test
    public void testEnd2Cloud_NewFormat_Res() {
        // {productId}/res/{devId}/{biz}/{requesterClientId}
        String topicStr = "smarthome/res/dev001/qlog/cloudClient01";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("res", t.getGroup());
        assertEquals("res", t.getReq$res());
        assertEquals("dev001", t.getDevId());
        assertEquals("qlog", t.getBiz());
        assertEquals("cloudClient01", t.getRequestorClientId());
    }

    @Test
    public void testEnd2Cloud_NewFormat_Msg() {
        // {productId}/msg/{devId}/{biz}
        String topicStr = "smarthome/msg/dev001/alog";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("msg", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("dev001", t.getDevId());
        assertEquals("alog", t.getBiz());
    }

    @Test
    public void testEnd2Cloud_NewFormat_Upld() {
        // {productId}/upld/{devId}/{fileId}
        String topicStr = "smarthome/upld/dev001/file001";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("upld", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("dev001", t.getDevId());
        assertEquals("file001", t.getBiz());
    }

    @Test
    public void testEnd2Cloud_OldFormat_NoProduct() {
        // msg/req/devinfo/v1.0/{reqNo}/{md5}
        String topicStr = "msg/req/devinfo/v1.0/req001/md5abc";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertNotNull(t);
        assertEquals("msg", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("devinfo", t.getBiz());
        assertEquals("v1.0", t.getVersion());
        assertEquals("req001", t.getReqId());
        assertEquals("md5abc", t.getMd5());
    }

    @Test
    public void testEnd2Cloud_OldFormat_WithProduct() {
        // {productId}/msg/req/devinfo/v1.0/{reqNo}/{md5}
        String topicStr = "smarthome/msg/req/devinfo/v1.0/req001/md5abc";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("msg", t.getGroup());
        assertEquals("req", t.getReq$res());
        assertEquals("devinfo", t.getBiz());
        assertEquals("v1.0", t.getVersion());
        assertEquals("req001", t.getReqId());
        assertEquals("md5abc", t.getMd5());
    }
}
