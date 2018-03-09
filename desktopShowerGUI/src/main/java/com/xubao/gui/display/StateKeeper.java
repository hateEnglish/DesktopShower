package com.xubao.gui.display;

import javafx.scene.control.Button;

/**
 * @Author xubao
 * @Date 2018/3/9
 */
public class StateKeeper {

    private static StateKeeper stateKeeper = new StateKeeper();

    public static StateKeeper getInstance(){
        return stateKeeper;
    }

    enum BtuState{

        NOEMAL("普通状态","全屏"),
        FULL_SCREEN("全屏状态","退出全屏"),;

        private String state;
        private String showText;

        BtuState(String state,String showText){
            this.state = state;
            this.showText = showText;
        }

        public String getShowText(){
            return showText;
        }

        public static BtuState getFirstState(){
            return NOEMAL;
        }

        public BtuState nextState(){
            int size = BtuState.values().length;
            int i = 0;
            for(;i<size-1;i++){
                if(this==BtuState.values()[i]){
                    break;
                }
            }
            return BtuState.values()[(i+1)%size];
        }
    }

    private BtuState btuState;

    public void initBtuState(Button but){
        this.btuState = BtuState.getFirstState();
        this.btuState = btuState.nextState();
    }


    public void changeBtuState(Button but){
        this.btuState = btuState.nextState();
        but.setText(btuState.getShowText());
    }
}
