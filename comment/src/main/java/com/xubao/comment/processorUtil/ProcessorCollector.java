package com.xubao.comment.processorUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
            String packPath = pack.replace(".", File.separator);
            Enumeration<URL> resources = classLoader.getResources(packPath);
            Map<String, Processor> processorsMap = new HashMap<>();
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();
                if (protocol.equals("file")) {
                    
                }
                else if (protocol.equals("jar")) {

                }
            }

            return processorsMap.size()==0?null:processorsMap;
        }
    }
}
