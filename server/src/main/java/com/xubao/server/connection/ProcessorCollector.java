package com.xubao.server.connection;

import com.google.protobuf.Message;
import com.xubao.comment.processorUtil.Processor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
@Deprecated
public class ProcessorCollector {
    private static Logger log = LoggerFactory.getLogger(ProcessorCollector.class);

    private static ProcessorCollector processorCollector = new ProcessorCollector();

    public static ProcessorCollector getInstance() {
        return processorCollector;
    }

    //String 消息类全限定名
    private static Map<String, Processor> processorMap = new HashMap<>();

    static {
        try {
            getProcessorFromPackage("com.xubao.server.connection.processor");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static void getProcessorFromPackage(String pack) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        System.out.println("pack=" + pack);
        String packagePath = pack.replace(".", File.separator);
        System.out.println("packagePath=" + packagePath);
        ClassLoader classLoader = ProcessorCollector.class.getClassLoader();

        URL resource = classLoader.getResource(packagePath);
        System.out.println("url="+resource);
        String absPackagePath = resource.getPath().substring(1).replace("%5c",File.separator);
        //  System.out.println(absPackagePath);

        File file = new File(absPackagePath);
        // System.out.println(file.getAbsolutePath());

        for (File child : file.listFiles()) {
            String fileName = child.getName();
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            Class<?> clazz = classLoader.loadClass(pack + '.' + fileName);
            Type[] genericInterfaces = clazz.getGenericInterfaces();

            for (Type type : genericInterfaces) {
                if (!"".equals(type.getTypeName())) {
                    ParameterizedType pType = (ParameterizedType) type;
                    Type[] actualTypeArguments = pType.getActualTypeArguments();

                    Processor processor = (Processor) clazz.newInstance();

                    processorMap.put(actualTypeArguments[0].getTypeName(), processor);
                }
            }
        }
    }

    public Processor getProcessorByClassName(String className) {
        Processor processor = processorMap.get(className);
        if (processor == null) {
            log.info("未进行处理的消息类型msg={}", className);
        }
        return processor;
    }

    public Processor getProcessorByMsg(Message msg) {
        System.out.println(msg.getClass().getName());
        return getProcessorByClassName(msg.getClass().getName());
    }

    public void processor(ChannelHandlerContext ctx, Message msg) {
        Processor processor = getProcessorByMsg(msg);
        if (processor != null) {
            processor.process(ctx, msg);
        }
    }

    public static void main(String[] args) {
        try {
            ProcessorCollector.getInstance().getProcessorFromPackage("com.xubao.server.connection.processor");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
