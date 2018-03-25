package com.xubao.comment.processorUtil;

import java.util.Map;

/**
 * @Author xubao
 * @Date 2018/3/25
 */
public class ProcessorProvider {
    //处理器类名 处理器对象
    protected Map<String,Processor> processorMap;

    protected ProcessorProvider(){}

    public Map<String, Processor> getProcessorMap() {
        return processorMap;
    }

    protected void setProcessorMap(Map<String, Processor> processorMap) {
        this.processorMap = processorMap;
    }

    public Processor getProcessorByClassName(String processorClassName){
        return processorMap.get(processorClassName);
    }


}
