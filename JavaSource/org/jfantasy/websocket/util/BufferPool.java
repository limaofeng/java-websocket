
package org.jfantasy.websocket.util;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BufferPool{
	
	private static int maxBufferPoolSize = 1000;// 默认的直接缓冲区池上限大小1000
	private static int minBufferPoolSize = 1000;// 默认的直接缓冲区池下限大小1000
	private static int writeBufferSize = 64;// 响应缓冲区大小默认为4k
	
	private static BufferPool bufferPool = new BufferPool();// BufferPool的单实例
	
	private AtomicInteger usableCount = new AtomicInteger();// 可用缓冲区的数量
	private AtomicInteger createCount = new AtomicInteger();// 已创建了缓冲区的数量
	private ConcurrentLinkedQueue<ByteBuffer> queue = new ConcurrentLinkedQueue<ByteBuffer>();// 保存直接缓存的队列
	
	private BufferPool(){
		// 预先创建直接缓冲区
		for(int i = 0; i < minBufferPoolSize; ++i){
			ByteBuffer bb = ByteBuffer.allocateDirect(writeBufferSize * 1024);
			this.queue.add(bb);
		}
		
		// 设置可用的缓冲区和已创建的缓冲区数量
		this.usableCount.set(minBufferPoolSize);
		this.createCount.set(minBufferPoolSize);
	}
	
	/**
	 * 
	 * <li>方法名：getBuffer
	 * <li>@return
	 * <li>返回类型：ByteBuffer
	 * <li>说明：
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-2-2
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	public ByteBuffer getBuffer(){
		ByteBuffer bb = this.queue.poll();
		
		if(bb == null){// 如果缓冲区不够则创建新的缓冲区
			bb = ByteBuffer.allocateDirect(writeBufferSize * 1024);
			this.createCount.incrementAndGet();
		}else{
			this.usableCount.decrementAndGet();
		}
		
		return bb;
	}
	
	/**
	 * 
	 * <li>方法名：releaseBuffer
	 * <li>@param bb
	 * <li>返回类型：void
	 * <li>说明：释放缓冲区
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-2-2
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	public void releaseBuffer(ByteBuffer bb){
		if(this.createCount.intValue() > maxBufferPoolSize && (this.usableCount.intValue() > (this.createCount.intValue() / 2) ) ){
			bb = null;
			this.createCount.decrementAndGet();
		}else{
			this.queue.add(bb);
			this.usableCount.incrementAndGet();
		}
	}
	
	public static BufferPool getInstance(){
		return bufferPool;
	}
}
