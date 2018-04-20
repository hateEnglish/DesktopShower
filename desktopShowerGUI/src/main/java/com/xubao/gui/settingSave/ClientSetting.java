package com.xubao.gui.settingSave;

/**
 * @Author xubao
 * @Date 2018/4/18
 */
public class ClientSetting extends Setting {
    private String nickName;
    private boolean isFullScreen;

    public ClientSetting(String nickName, boolean isFullScreen) {
        this.nickName = nickName;
        this.isFullScreen = isFullScreen;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    @Override
    public String toString() {
        return "ClientSetting{" +
                "nickName='" + nickName + '\'' +
                ", isFullScreen=" + isFullScreen +
                ", saveTime=" + saveTime +
                ", id=" + id +
                '}';
    }
}
