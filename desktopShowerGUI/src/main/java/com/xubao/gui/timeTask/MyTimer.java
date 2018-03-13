package com.xubao.gui.timeTask;

import javafx.scene.control.Control;

import java.util.*;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/13
 */
public final class MyTimer
{
	private static Timer timer = new Timer(true);

	private static Map<Control,TimerTask> controlTimerTaskMap = new HashMap<>();

	public static void cancelControlTask(Control control){
		TimerTask timerTask = controlTimerTaskMap.get(control);
		if(timerTask!=null){
			timerTask.cancel();
		}
	}

	public static void addControlTask(Control control, TimerTask timerTask, Date firstTime, long period){
		controlTimerTaskMap.put(control,timerTask);
		timer.schedule(timerTask,firstTime,period);
	}

	public static void addControlTask(Control control, TimerTask timerTask, long delay, long period){
		controlTimerTaskMap.put(control,timerTask);
		timer.schedule(timerTask,delay,period);
	}

	public static void cancelTimer(){
		timer.cancel();
	}

}
