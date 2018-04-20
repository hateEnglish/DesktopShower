package com.xubao.gui.settingSave;

import java.sql.Timestamp;

/**
 * @Author xubao
 * @Date 2018/4/18
 */
public class Setting {
    protected Timestamp saveTime;
    protected int id;

    public Timestamp getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(Timestamp saveTime) {
        this.saveTime = saveTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
