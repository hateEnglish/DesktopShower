package com.xubao.gui.display;

import com.xubao.gui.struct.controlStateStruct.ControlState;
import com.xubao.gui.struct.controlStateStruct.StateChangeEvent;
import com.xubao.gui.struct.controlStruct.AppKeeper;
import com.xubao.gui.struct.controlStruct.StageKey;
import javafx.scene.control.Button;

/**
 * @Author xubao
 * @Date 2018/3/9
 */
public class DisplayStateKeeper {

    private static DisplayStateKeeper stateKeeper = new DisplayStateKeeper();

    public static DisplayStateKeeper getInstance() {
        return stateKeeper;
    }

    public static class BtuState extends ControlState<Button> {
        static BtuState NOEMAL = new BtuState("普通状态", "全屏");
        static BtuState FULL_SCREEN = new BtuState("全屏状态", "退出全屏");

        protected BtuState(String stateDesc, String showText) {
            super(stateDesc, showText);
        }
    }

    private BtuState btuState;

    public void initBtuState(Button but) {
        this.btuState = BtuState.FULL_SCREEN.getFirstState();
        this.btuState = btuState.nextState();
        ControlState.bindControl(BtuState.class, but);
    }


    public void changeBtuState() {
        btuState.beforeChangeToNextStateDoSomeThing();
        this.btuState = btuState.nextState();
    }


}
