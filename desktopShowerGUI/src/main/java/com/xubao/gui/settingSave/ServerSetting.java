package com.xubao.gui.settingSave;

/**
 * @Author xubao
 * @Date 2018/4/18
 */
public class ServerSetting extends Setting {
    private String shareTheme;
    private int screenSize;
    private int sendDelay;
    private int sendQuality;
    private boolean isNeedPwd;
    private String pwd;

    public ServerSetting() {
    }

    public ServerSetting(String shareTheme, int screenSize, int sendDelay, int sendQuality, boolean isNeedPwd, String pwd) {
        this.shareTheme = shareTheme;
        this.screenSize = screenSize;
        this.sendDelay = sendDelay;
        this.sendQuality = sendQuality;
        this.isNeedPwd = isNeedPwd;
        this.pwd = pwd;
    }

    public String getShareTheme() {
        return shareTheme;
    }

    public void setShareTheme(String shareTheme) {
        this.shareTheme = shareTheme;
    }

    public int getSendDelay() {
        return sendDelay;
    }

    public void setSendDelay(int sendDelay) {
        this.sendDelay = sendDelay;
    }

    public int getSendQuality() {
        return sendQuality;
    }

    public void setSendQuality(int sendQuality) {
        this.sendQuality = sendQuality;
    }

    public boolean isNeedPwd() {
        return isNeedPwd;
    }

    public void setNeedPwd(boolean needPwd) {
        isNeedPwd = needPwd;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(int screenSize) {
        this.screenSize = screenSize;
    }

    @Override
    public String toString() {
        return "ServerSetting{" +
                "shareTheme='" + shareTheme + '\'' +
                ", screenSize=" + screenSize +
                ", sendDelay=" + sendDelay +
                ", sendQuality=" + sendQuality +
                ", isNeedPwd=" + isNeedPwd +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
