package com.xubao.comment.util;

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

	private static Map<Object,TimerTask> timerTaskMap = new HashMap<>();

	public static void cancelControlTask(Object key){
		TimerTask timerTask = timerTaskMap.get(key);
		if(timerTask!=null){
			timerTask.cancel();
		}
	}

	public static void addControlTask(Object key, TimerTask timerTask, Date firstTime, long period){
		timerTaskMap.put(key,timerTask);
		timer.schedule(timerTask,firstTime,period);
	}

	public static void addControlTask(Object key, TimerTask timerTask, long delay, long period){
		timerTaskMap.put(key,timerTask);
		timer.schedule(timerTask,delay,period);
	}

	public static void cancelTimer(){
		timer.cancel();
	}

}
