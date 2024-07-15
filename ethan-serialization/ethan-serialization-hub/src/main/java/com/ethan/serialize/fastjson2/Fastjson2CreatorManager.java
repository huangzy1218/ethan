package com.ethan.serialize.fastjson2;

import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.reader.ObjectReaderCreatorASM;
import com.alibaba.fastjson2.writer.ObjectWriterCreatorASM;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Fastjson2CreatorManager {

    /**
     * An empty classLoader used when classLoader is system classLoader. Prevent the NPE.
     */
    private static final ClassLoader SYSTEM_CLASSLOADER_KEY = new ClassLoader() {
    };

    private final Map<ClassLoader, ObjectReaderCreatorASM> readerMap = new ConcurrentHashMap<>();
    private final Map<ClassLoader, ObjectWriterCreatorASM> writerMap = new ConcurrentHashMap<>();

    public void setCreator(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = SYSTEM_CLASSLOADER_KEY;
        }
        JSONFactory.setContextReaderCreator(readerMap.computeIfAbsent(classLoader, ObjectReaderCreatorASM::new));
        JSONFactory.setContextWriterCreator(writerMap.computeIfAbsent(classLoader, ObjectWriterCreatorASM::new));
    }
}
