package com.xubao.gui.struct.controlStateStruct;

import javafx.scene.control.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.SystemColor.control;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/10
 */
public abstract class ControlState<T extends Control>
{
	private Logger log = LoggerFactory.getLogger(ControlState.class);

	protected static Map<Class<? extends ControlState>,List<ControlState>> statesMap = new HashMap();
	protected static Map<Class<? extends ControlState>,Control> controlMap = new HashMap<>();

	protected String stateDesc;
	protected String showText;
	protected List<ControlState> states;
	protected StateChangeEvent<? extends Control> stateChangeEvent;

	protected ControlState(String stateDesc, String showText){
		this.stateDesc = stateDesc;
		this.showText = showText;

		states = ControlState.statesMap.get(this.getClass());
		if(states==null){
			states = new ArrayList<>();
			ControlState.statesMap.put(this.getClass(),states);
		}
		states.add(this);
	}

	public void setChangeState(StateChangeEvent<T> stateChangeEvent)
	{
		this.stateChangeEvent = stateChangeEvent;
	}

	public static <C extends Control> void bindControl( Class<? extends ControlState> stateClass,C control)
	{
		controlMap.put(stateClass,control);
	}

	public String getStateDesc()
	{
		return stateDesc;
	}
	public String getShowText(){
		return showText;
	}

	public <S extends ControlState> S getFirstState(){
		List<ControlState> states = this.states;
		return states==null?null:(S)(states.get(0));
	}

	public  <S extends ControlState> S nextState(){

		List<? extends ControlState> states = this.states;
		if(states==null){
			return null;
		}

		int size = states.size();
		int i = 0;
		for(;i<size-1;i++){
			if(this== states.get(i)){
				break;
			}
		}
		return (S)(states.get((i+1)%size));
	}

	public boolean beforeChangeToNextStateDoSomeThing(){
		ControlState state = this.nextState();
		if(state.stateChangeEvent ==null){
			log.debug("没有改变事件可以触发");
			return true;
		}
		T control = (T)ControlState.controlMap.get(this.getClass());

		if(control==null){
			log.info("状态未绑定控件");
		}

		return state.stateChangeEvent.execute(control);
	}



	@Override
	public String toString()
	{
		return "ControlState{" +
				"stateDesc='" + stateDesc + '\'' +
				", showText='" + showText + '\'' +
				'}';
	}
}
