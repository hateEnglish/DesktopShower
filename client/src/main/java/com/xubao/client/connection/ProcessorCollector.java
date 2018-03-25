package com.xubao.client.connection;

import com.google.protobuf.Message;
import com.xubao.client.connection.processor.Processor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
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
            getProcessorFromPackage("com.xubao.client.connection.processor");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getProcessorFromPackage(String pack) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        System.out.println("pack=" + pack);
        String packagePath = pack.replace(".", File.separator);
        System.out.println("packagePath=" + packagePath);
        ClassLoader classLoader = ProcessorCollector.class.getClassLoader();

        URL resource = classLoader.getResource(packagePath);
        System.out.println("urlxx="+resource);

        URL resource1 = classLoader.getResource("processor");
        System.out.println("url="+resource1);

        String absPackagePath = resource.getPath().substring(1).replace("%5c",File.separator);
                //resource.getPath().substring(1).replace('/', '\\') + packagePath;
        System.out.println(absPackagePath);

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





//    else if("jar".equals(protocol))
//    {
//        JarFile jar = null;
//        try
//        {
//            JarURLConnection jarURLConnection = ((JarURLConnection)url.openConnection());
//            jarURLConnection.setUseCaches(false);
//            jar = jarURLConnection.getJarFile();
//            Enumeration<JarEntry> entries = jar.entries();
//            while(entries.hasMoreElements())
//            {
//                JarEntry entry = entries.nextElement();
//                String name = entry.getName();
//                if(name.charAt(0) == '/')
//                {
//                    name = name.substring(1);
//                }
//                if(name.startsWith(packageDirName))
//                {
//                    int idx = name.lastIndexOf('/');
//                    if(idx != -1)
//                    {
//                        packageName = name.substring(0, idx).replace('/', '.');
//                    }
//                    if(idx != -1)
//                    {
//                        if(name.endsWith(".class") && !entry.isDirectory())
//                        {
//                            String className = name.substring(packageName.length() + 1, name.length() - 6);
//                            try
//                            {
//                                Class<?> t = Class.forName(packageName + '.' + className);
//                                classes.add((Class<T>)t);
//                            }
//                            catch(ClassNotFoundException ignored)
//                            {
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        catch(IOException ignored)
//        {
//        }
//        finally
//        {
//            if(jar != null)
//            {
//                jar.close();
//            }
//        }
//    }
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

    public static void main(String[] args){
        ProcessorCollector instance = ProcessorCollector.getInstance();
        System.out.println(instance);
    }
}
