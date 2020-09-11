package com.neucore.neusdk_demo.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "KeywordHistory")
public class KeywordHistoryEntity {

    @Id(autoincrement = true)
    private Long _id;
    public Long Id;

    @Property(nameInDb = "Keyword")
    public String Keyword;

    @Property(nameInDb = "QueryTime")
    public long QueryTime;

    @Generated(hash = 715681405)
    public KeywordHistoryEntity(Long _id, Long Id, String Keyword, long QueryTime) {
        this._id = _id;
        this.Id = Id;
        this.Keyword = Keyword;
        this.QueryTime = QueryTime;
    }

    @Generated(hash = 462930205)
    public KeywordHistoryEntity() {
    }

    public Long getId() {
        return this.Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getKeyword() {
        return this.Keyword;
    }

    public void setKeyword(String Keyword) {
        this.Keyword = Keyword;
    }

    public long getQueryTime() {
        return this.QueryTime;
    }

    public void setQueryTime(long QueryTime) {
        this.QueryTime = QueryTime;
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }


}