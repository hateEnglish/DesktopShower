package com.xubao.client.manager;

import com.xubao.client.pojo.ReceiveFrame;
import com.xubao.client.pojo.ReceiveFramePiece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author xubao
 * @Date 2018/3/3
 */
public class FrameManager
{
	private static FrameManager frameManager = new FrameManager();

	public static FrameManager getInstance()
	{
		return frameManager;
	}

	private int frameCacheSize;
	//按帧顺序保存帧
	private List<ReceiveFrame> receiveFrameList = new ArrayList<>();

	/**
	 * 按帧顺序存储
	 */
	public void addFrame(ReceiveFrame frame)
	{
		int i = receiveFrameList.size();
		for(; i >= 1; i--)
		{
			if(receiveFrameList.get(i - 1).compareTo(frame) < 0)
			{
				break;
			}
		}

		receiveFrameList.add(i, frame);
	}

	public void addFramePiece(ReceiveFramePiece framePiecec)
	{
		ReceiveFrame frame = getFrame(framePiecec.getFrameNumber());
		if(frame == null)
		{
			frame = new ReceiveFrame(framePiecec.getFrameNumber(), framePiecec.getDataSize());
			addFrame(frame);
		}
		frame.addFramePiece(framePiecec);
	}

	public ReceiveFrame getAndRemoveFirstFrame()
	{
		if(receiveFrameList.size() == 0)
		{
			return null;
		}
		ReceiveFrame receiveFrame = receiveFrameList.remove(0);
		return receiveFrame;
	}

	public ReceiveFrame getFrame(int frameNumber)
	{
		for(ReceiveFrame frame : receiveFrameList)
		{
			if(frame.getFrameNumber() == frameNumber)
			{
				return frame;
			}
		}

		return null;
	}

	public ReceiveFrame getFirstFullFrameAndRemoveUnFull()
	{
		Iterator<ReceiveFrame> iterator = receiveFrameList.iterator();
		while(iterator.hasNext())
		{
			ReceiveFrame next = iterator.next();
			if(next.isFull())
			{
				return next;
			}
			else
			{
				iterator.remove();
			}
		}

		return null;
	}

	public ReceiveFrame findFirstFullFrame()
	{
		for(ReceiveFrame frame : receiveFrameList)
		{
			if(frame.isFull())
			{
				return frame;
			}
		}

		return null;
	}

	/**
	 * 在规定时间内等待第一个帧完整并获取
	 */
	public ReceiveFrame getAndWaitFirstFrameFull(int waitTime, TimeUnit timeUnit, boolean isNotFullReturnInTimeout, boolean removeUnfullAndRetryInTimeout)
	{
		int waitInterval = 20;
		int useTime = 0;
		List<ReceiveFrame> clone = this.frameListShadowCopy();
		Iterator<ReceiveFrame> iterator = clone.iterator();
		for(;iterator.hasNext();)
		{
			ReceiveFrame next = iterator.next();
			while(!next.isFull()){
				useTime+=waitInterval;
            	if(useTime>=timeUnit.toMillis(waitTime)){
            		if(isNotFullReturnInTimeout){
            			iterator.remove();
            			this.receiveFrameList.remove(0);
            			return next;
		            }
            		break;
	            }

	            try
	            {
		            Thread.sleep(waitInterval);
	            }
	            catch(InterruptedException e)
	            {
		            e.printStackTrace();
	            }
            }

            if(next.isFull()){
				iterator.remove();
	            this.receiveFrameList.remove(0);
				return next;
            }

            //移除前面不完整的,等待下一个完整帧
            if(removeUnfullAndRetryInTimeout){
				useTime = 0;
				iterator.remove();
	            this.receiveFrameList.remove(0);
				continue;
            }

            throw new RuntimeException("等待第一帧完整失败");
		}
		if(removeUnfullAndRetryInTimeout){
			throw new RuntimeException("所有帧都等待完整失败");
		}
		throw new RuntimeException("还没有任何帧数据");
	}

	public List<ReceiveFrame> frameListShadowCopy(){
		List<ReceiveFrame> clone = new ArrayList<>();
		for(ReceiveFrame frame:receiveFrameList){
			clone.add(clone.size(),frame);
		}

		return clone;
	}

	public static void main(String[] args)
	{
		FrameManager.getInstance().addFrame(new ReceiveFrame(3));
		FrameManager.getInstance().addFrame(new ReceiveFrame(2));
		FrameManager.getInstance().addFrame(new ReceiveFrame(1));
		FrameManager.getInstance().addFrame(new ReceiveFrame(4));
		FrameManager.getInstance().addFrame(new ReceiveFrame(2));
		FrameManager.getInstance().addFrame(new ReceiveFrame(3));

		System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
		System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
		System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
		System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
		System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
		System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
		System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());

		System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());
		System.out.println(FrameManager.getInstance().getAndRemoveFirstFrame());

	}
}
