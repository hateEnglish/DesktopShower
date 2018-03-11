package com.xubao.gui.struct.controlStruct;

/**
 * @Author xubao
 * @Date 2018/3/11
 */
public enum ControlKey {

    STAGE(null,"舞台"),

    ;


    ControlKey(ControlKey parentKey,String name){
        this.parentKey = parentKey;
        this.name = name;
    }
    private ControlKey parentKey;
    private String name;
}
