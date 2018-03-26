package com.xubao.comment.processorUtil;

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
    private static ProcessorCollectorInner processorCollectorInner = new ProcessorCollectorInner();
    private ProcessorCollector() {
    }

    public static ProcessorProvider collectProcessorsFromPackage(String pack) {
        return collectProcessorsFromPackage(ProcessorProvider.class.getClassLoader(), pack);
    }

    public static ProcessorProvider collectProcessorsFromPackage(ClassLoader classLoader, String pack) {
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
            System.out.println("pack="+pack);

            String packPath = pack.replace(".", File.separator);
            Enumeration<URL> resources = classLoader.getResources(packPath);
            Map<String, Processor> processorsMap = new HashMap<>();
            while (resources.hasMoreElements()) {
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
                    System.out.println("jar包");
                    JarFile jar = null;
                    try
                    {
                        JarURLConnection jarURLConnection = ((JarURLConnection)url.openConnection());
                        jarURLConnection.setUseCaches(false);
                        jar = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        System.out.println("进入循环");
                        while(entries.hasMoreElements())
                        {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            System.out.println("entryName="+name);
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
                                            System.out.println("classNotFound");
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
                    System.out.println("else");
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
