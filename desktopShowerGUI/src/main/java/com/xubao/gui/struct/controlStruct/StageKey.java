package com.xubao.gui.struct.controlStruct;

/**
 * @Author xubao
 * @Date 2018/3/11
 */
public enum  StageKey {
    STAGE("主舞台"),
    DISPLAY_STAGE("显示桌面舞台"),
            ;

    StageKey(String name){
        this.name = name;
    }
    private String name;
}
