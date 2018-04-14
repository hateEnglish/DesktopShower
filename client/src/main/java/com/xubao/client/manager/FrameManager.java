package com.xubao.client.manager;

import com.xubao.client.pojo.ReceiveFrame;
import com.xubao.client.pojo.ReceiveFramePiece;
import com.xubao.comment.config.CommentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author xubao
 * @Date 2018/3/3
 */
public class FrameManager
{
	private static Logger log = LoggerFactory.getLogger(FrameManager.class);

	private static FrameManager frameManager = new FrameManager();

	public static FrameManager getInstance()
	{
		return frameManager;
	}

	private int frameCacheSize = CommentConfig.getInstance().getProperInt("client.frame_cache_size");
	//按帧顺序保存帧
	private List<ReceiveFrame> receiveFrameList = new LinkedList<>();

	private int frameBufSize = CommentConfig.getInstance().getProperInt("client.frame_buf_size");
	private List<ReceiveFrame> receiveFrameBufList = new LinkedList<>();

	//当缓存满时每次清除的旧帧数
	private int clearSize = CommentConfig.getInstance().getProperInt("client.clear_size");

	/**
	 * 按帧顺序存储
	 */
	public synchronized void addFrame(ReceiveFrame frame)
	{
		clearOldData();

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

	private synchronized void clearOldData()
	{
		if(receiveFrameList.size() >= frameCacheSize)
		{
			for(int i = 0; i < clearSize; i++)
			{
				receiveFrameList.remove(i);
			}
		}
	}

	private synchronized void moveFrameToBuf()
	{
		for(int i = 0; i < frameBufSize; i++)
		{
			try
			{
				ReceiveFrame frame = receiveFrameList.remove(0);
				receiveFrameBufList.add(receiveFrameBufList.size(), frame);
			}
			catch(IndexOutOfBoundsException e)
			{
				//e.printStackTrace();
				return;
			}

		}
	}

	private void isBufEmptyFullBuf()
	{
		synchronized(receiveFrameBufList)
		{
			if(receiveFrameBufList.isEmpty())
			{
				moveFrameToBuf();
			}
		}
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

	public synchronized ReceiveFrame getAndRemoveFirstFrame()
	{
		if(receiveFrameList.size() == 0)
		{
			return null;
		}
		ReceiveFrame receiveFrame = receiveFrameList.remove(0);
		return receiveFrame;
	}

	public synchronized ReceiveFrame getFrame(int frameNumber)
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
		isBufEmptyFullBuf();

		int waitInterval = 20;
		int useTime = 0;
		//List<ReceiveFrame> clone = this.frameListShadowCopy();
		Iterator<ReceiveFrame> iterator = receiveFrameBufList.iterator();//clone.iterator();
		for(; iterator.hasNext(); )
		{
			ReceiveFrame next = iterator.next();
			while(!next.isFull())
			{
				useTime += waitInterval;
				if(useTime >= timeUnit.toMillis(waitTime))
				{
					if(isNotFullReturnInTimeout)
					{
						iterator.remove();
						//this.receiveFrameList.remove(0);
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

			if(next.isFull())
			{
				iterator.remove();
				//this.receiveFrameList.remove(0);
				return next;
			}

			//移除前面不完整的,等待下一个完整帧
			if(removeUnfullAndRetryInTimeout)
			{
				useTime = 0;
				iterator.remove();
				//this.receiveFrameList.remove(0);
				continue;
			}

			log.warn("等待第一帧完整失败");
		}
		if(removeUnfullAndRetryInTimeout)
		{
			log.warn("所有帧都等待完整失败");
		}
//		log.warn("还没有任何帧数据");
		return null;
	}

	public List<ReceiveFrame> frameListShadowCopy()
	{
		List<ReceiveFrame> clone = new ArrayList<>();
		for(ReceiveFrame frame : receiveFrameList)
		{
			clone.add(clone.size(), frame);
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
