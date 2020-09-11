package com.neucore.neulink.extend;

import java.util.HashMap;
import java.util.Map;

public class UpdateResult extends Result {
    private Map<String,Object> datas = new HashMap<String,Object>();
    public Map<String, Object> getDatas() {
        return datas;
    }
    public void setDatas(Map<String, Object> datas) {
        this.datas = datas;
    }
}
