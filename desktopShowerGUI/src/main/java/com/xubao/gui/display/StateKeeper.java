package com.xubao.gui.display;

import com.xubao.gui.bootstarp.Bootstrap;
import com.xubao.gui.struct.controlStateStruct.ControlState;
import com.xubao.gui.struct.controlStateStruct.StateChangeEvent;
import com.xubao.gui.struct.controlStruct.AppKeeper;
import com.xubao.gui.struct.controlStruct.StageKey;
import javafx.scene.control.Button;
import javafx.scene.control.Control;

/**
 * @Author xubao
 * @Date 2018/3/9
 */
public class StateKeeper {

    private static StateKeeper stateKeeper = new StateKeeper();

    public static StateKeeper getInstance(){
        return stateKeeper;
    }

    public static class BtuState extends ControlState<Button>
    {
        static BtuState NOEMAL = new BtuState("普通状态","全屏");
        static BtuState FULL_SCREEN = new BtuState("全屏状态","退出全屏");

        protected BtuState(String stateDesc, String showText)
        {
            super(stateDesc, showText);
        }
    }

    private BtuState btuState;

    public void initBtuState(Button but){
        this.btuState = BtuState.FULL_SCREEN.getFirstState();
        this.btuState = btuState.nextState();
        ControlState.bindControl(BtuState.class,but);

	    BtuState.NOEMAL.setChangeState(new StateChangeEvent<Button>()
	    {
		    @Override
		    public void execute(Button button)
		    {
			    button.setText(BtuState.NOEMAL.getShowText());
			    AppKeeper.getStage(StageKey.STAGE).setFullScreen(false);
		    }
	    });

	    BtuState.FULL_SCREEN.setChangeState(new StateChangeEvent<Button>()
	    {
		    @Override
		    public void execute(Button button)
		    {
			    button.setText(BtuState.FULL_SCREEN.getShowText());
                AppKeeper.getStage(StageKey.STAGE).setFullScreen(true);
		    }
	    });
    }


    public void changeBtuState(Button but){
        this.btuState = btuState.nextState();
        btuState.beforeChangeToNextStateDoSomeThing();
        //but.setText(btuState.getShowText());
    }


}
