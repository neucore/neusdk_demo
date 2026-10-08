package com.neucore.neulink.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class NeulinkTopicParserTest {

    private NeulinkTopicParser parser = NeulinkTopicParser.getInstance();

    // ==================== cloud2EndParser (云端→设备) ====================

    @Test
    public void testCloud2End_NewFormat_Req() {
        // {productId}/req/{biz}/{devId}/{requesterClientId}
        String topicStr = "smarthome/req/devinfo/dev001/cloudClient01";
        NeulinkTopicParser.Topic t = parser.cloud2EndParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("req", t.getGroup());
        assertEquals("req", t.getReq$res());
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
        // {productId}/req/{biz}/{devId} （无requesterClientId）
        String topicStr = "smarthome/req/devinfo/dev001";
        NeulinkTopicParser.Topic t = parser.cloud2EndParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("req", t.getGroup());
        assertEquals("req", t.getReq$res());
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
        assertEquals("connect", t.getBiz());
    }

    @Test
    public void testEnd2Cloud_NewFormat_Evt_Disconnect() {
        // {productId}/evt/{devId}/disconnect
        String topicStr = "smarthome/evt/dev001/disconnect";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertEquals("disconnect", t.getBiz());
    }

    @Test
    public void testEnd2Cloud_NewFormat_Evt_Lwt() {
        // {productId}/evt/{devId}/lwt
        String topicStr = "smarthome/evt/dev001/lwt";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertEquals("lwt", t.getBiz());
    }

    @Test
    public void testEnd2Cloud_NewFormat_Res() {
        // {productId}/res/{biz}/{devId}/{requesterClientId}
        String topicStr = "smarthome/res/qlog/dev001/cloudClient01";
        NeulinkTopicParser.Topic t = parser.end2cloudParser(topicStr);
        assertNotNull(t);
        assertEquals("smarthome", t.getProduct());
        assertEquals("res", t.getGroup());
        assertEquals("res", t.getReq$res());
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
