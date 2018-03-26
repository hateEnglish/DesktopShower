package com.xubao.comment.processorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author xubao
 * @Date 2018/3/25
 */
public final class ProcessorCollector {
    private static Logger log = LoggerFactory.getLogger(ProcessorCollector.class);
    private static ProcessorCollectorInner processorCollectorInner = new ProcessorCollectorInner();
    private ProcessorCollector() {
    }

    public static ProcessorProvider collectProcessorsFromPackage(String pack) {
        log.debug("-----------");
        return collectProcessorsFromPackage(ProcessorCollector.class.getClassLoader(), pack);
    }

    public static ProcessorProvider collectProcessorsFromPackage(ClassLoader classLoader, String pack) {
        log.debug("collectProcessorsFromPackage");
        ProcessorProvider processorProvider = null;

        try {
            Map<String, Processor> processorsMap = processorCollectorInner.collectProcessorFromPackage(classLoader, pack);
            processorProvider = new ProcessorProvider();
            processorProvider.setProcessorMap(processorsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return processorProvider;
    }

    private static class ProcessorCollectorInner {
        public Map<String, Processor> collectProcessorFromPackage(ClassLoader classLoader, String pack) throws IOException {
            log.debug("collectProcessorFromPackage");
            log.debug("pack={}",pack);

            String packPath = pack.replace(".","/");
            log.debug(packPath);
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packPath);
            log.debug("resource={}",resources);
            log.debug("moreEle={}",resources.hasMoreElements());
            Map<String, Processor> processorsMap = new HashMap<>();
            while (resources.hasMoreElements()) {
                log.debug("文件");
                URL url = resources.nextElement();
                String protocol = url.getProtocol();
                if (protocol.equals("file")) {
                    String filePath = URLDecoder.decode(url.getFile(),"utf-8");
                    File dir = new File(filePath);
                    File[] files = dir.listFiles();
                    for(File file:files){
                        String fileName = file.getName();
                        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                        try {
                            Class<Processor> clazz = (Class<Processor>) classLoader.loadClass(pack + '.' + fileName);
                            Class msgClass = getMsgClass(clazz);
                            Processor processor = clazz.newInstance();
                            processorsMap.put(msgClass.getName(),processor);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (protocol.equals("jar")) {
                    log.debug("jar包");
                    JarFile jar = null;
                    try
                    {
                        JarURLConnection jarURLConnection = ((JarURLConnection)url.openConnection());
                        jarURLConnection.setUseCaches(false);
                        jar = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        log.debug("进入循环");
                        while(entries.hasMoreElements())
                        {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            log.debug("entryName="+name);
                            if(name.charAt(0) == '/')
                            {
                                name = name.substring(1);
                            }
                            if(name.startsWith(packPath))
                            {
                                int idx = name.lastIndexOf('/');
                                if(idx != -1)
                                {
                                    packPath = name.substring(0, idx).replace('/', '.');
                                }
                                if(idx != -1)
                                {
                                    if(name.endsWith(".class") && !entry.isDirectory())
                                    {
                                        String className = name.substring(pack.length() + 1, name.length() - 6);
                                        try
                                        {
                                            Class<Processor> clazz = (Class<Processor>) Class.forName(pack + '.' + className);
                                            Class msgClass = null;
                                            try {
                                                msgClass = getMsgClass(clazz);
                                                Processor processor = clazz.newInstance();
                                                processorsMap.put(msgClass.getName(),processor);
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            } catch (InstantiationException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        catch(ClassNotFoundException ignored)
                                        {
                                            log.debug("classNotFound");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch(IOException ignored)
                    {
                        ignored.printStackTrace();
                    }
                    finally
                    {
                        if(jar != null)
                        {
                            jar.close();
                        }
                    }
                }
                else{
                    log.debug("else");
                }
            }

            return processorsMap.size()==0?null:processorsMap;
        }

        private Class getMsgClass(Class<? extends Processor> p) throws IllegalAccessException, InstantiationException {
            Type[] genericInterfaces = p.getGenericInterfaces();

            for (Type type : genericInterfaces) {
                if (!"".equals(type.getTypeName())) {
                    ParameterizedType pType = (ParameterizedType) type;
                    Type[] actualTypeArguments = pType.getActualTypeArguments();
                    return (Class) actualTypeArguments[0];
                }
            }
            return null;
        }
    }

}
