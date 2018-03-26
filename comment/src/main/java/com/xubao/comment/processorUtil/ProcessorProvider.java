package com.xubao.comment.processorUtil;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * @Author xubao
 * @Date 2018/3/25
 */
public class ProcessorProvider {
    //消息类全路径 处理器对象
    protected Map<String,Processor> processorMap;

    protected ProcessorProvider(){}

    public Map<String, Processor> getProcessorMap() {
        return processorMap;
    }

    protected void setProcessorMap(Map<String, Processor> processorMap) {
        this.processorMap = processorMap;
    }

    public Processor getProcessorByMsgClassName(String processorClassName){
        return processorMap.get(processorClassName);
    }

    public Processor getProcessorByMsg(Message msg){
        return processorMap.get(msg.getClass().getName());
    }

    public void processor(ChannelHandlerContext ctx, Message msg) {
        Processor processor = getProcessorByMsg(msg);
        if (processor != null) {
            processor.process(ctx, msg);
        }
    }

}
