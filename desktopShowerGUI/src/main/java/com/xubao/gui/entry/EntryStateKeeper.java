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
        showScreenBtuState.beforeChangeToNextStateDoSomeThing();
        this.showScreenBtuState = showScreenBtuState.nextState();
    }
}
