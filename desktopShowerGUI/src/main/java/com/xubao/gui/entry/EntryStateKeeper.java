package com.xubao.gui.entry;

import com.xubao.gui.struct.controlStateStruct.ControlState;
import javafx.scene.control.Button;

/**
 * @Author xubao
 * @Date 2018/3/13
 */
public final class EntryStateKeeper {

    private EntryStateKeeper(){}
    private static EntryStateKeeper entryStateKeeper = new EntryStateKeeper();
    public static EntryStateKeeper getInstance(){
        return entryStateKeeper;
    }

    ShowScreenBtuState showScreenBtuState;
    public static class ShowScreenBtuState extends ControlState<Button> {

        protected ShowScreenBtuState(String stateDesc, String showText) {
            super(stateDesc, showText);
        }

        public static ShowScreenBtuState NORMAL = new ShowScreenBtuState("准备状态","启动桌面共享");
        public static ShowScreenBtuState START_SHOW_SCREEN = new ShowScreenBtuState("共享状态","取消桌面共享");
    }

    public void initShowScreenBtuState(Button showScreenState){
        showScreenBtuState = ShowScreenBtuState.NORMAL;
        ControlState.bindControl(ShowScreenBtuState.class,showScreenState);
    }

    public void changeShowScreenBtuState(){
        boolean success = showScreenBtuState.beforeChangeToNextStateDoSomeThing();
        if(success) {
            this.showScreenBtuState = showScreenBtuState.nextState();
        }
    }

    ConnectButState connectButState;
    public static class ConnectButState extends ControlState<Button>{

        protected ConnectButState(String stateDesc, String showText) {
            super(stateDesc, showText);
        }

        public static ConnectButState NORMAL = new ConnectButState("准备状态","连接");
        public static ConnectButState CONNECTED = new ConnectButState("连接状态","断开连接");
    }

    public void initConnectBut(Button connectBut){
        connectButState = ConnectButState.NORMAL;
        ControlState.bindControl(ConnectButState.class,connectBut);
    }

    public void changeConnectBut(){
        boolean success = connectButState.beforeChangeToNextStateDoSomeThing();
        if(success) {
            connectButState = connectButState.nextState();
        }
    }
}
