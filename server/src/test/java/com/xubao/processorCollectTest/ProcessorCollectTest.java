package com.xubao.processorCollectTest;

import com.xubao.comment.processorUtil.ProcessorCollector;
import org.junit.Test;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/26
 */
public class ProcessorCollectTest {
    @Test
    public void test(){
        ProcessorCollector.collectProcessorsFromPackage("com.xubao.server.connection.processor");
    }
}
