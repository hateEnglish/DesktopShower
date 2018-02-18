package com.xubao.server.connection;

import com.google.protobuf.Message;
import com.xubao.server.connection.processor.Processor;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public class ProcessorCollector {
    private static ProcessorCollector processorCollector = new ProcessorCollector();

    public static ProcessorCollector getInstance(){
        return processorCollector;
    }

    //String 消息类全限定名
    private Map<String,Processor> processorMap = new HashMap<>();

    private void getProcessorFromPackage(String pack) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        System.out.println("pack="+pack);
        String packagePath = pack.replace(".", File.separator);
        System.out.println("packagePath="+packagePath);
        ClassLoader classLoader = this.getClass().getClassLoader();

        URL resource = classLoader.getResource(".");

        String absPackagePath = resource.getPath().substring(1).replace('/','\\')+packagePath;
      //  System.out.println(absPackagePath);

        File file = new File(absPackagePath);
       // System.out.println(file.getAbsolutePath());

        for(File child:file.listFiles()){
            String fileName = child.getName();
            fileName = fileName.substring(0,fileName.lastIndexOf('.'));
            Class<?> clazz = classLoader.loadClass(pack + '.' + fileName);
            Type[] genericInterfaces = clazz.getGenericInterfaces();

            for(Type type:genericInterfaces){
                if(!"".equals(type.getTypeName())) {
                    ParameterizedType pType = (ParameterizedType) type;
                    Type[] actualTypeArguments = pType.getActualTypeArguments();

                    Processor processor = (Processor) clazz.newInstance();

                    processorMap.put(actualTypeArguments[0].getTypeName(),processor);
                }
            }
        }
    }

    public Processor getProcessorByClassName(String className){
        return processorMap.get(className);
    }

    public Processor getProcessorByMsg(Message msg){
        return getProcessorByClassName(msg.getClass().getName());
    }

    public static void main(String[] args){
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
