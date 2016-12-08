package com.tradingview;

import javassist.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Field;
import java.util.*;

public class Main {

    final private ClassPool parentPool = new ClassPool(true);
    static Random rnd = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws Exception {
        Main m = new Main();
        ClassesCache classesCache = new ClassesCache(150);
        List<Class> classes = new LinkedList<>();
        List<Object> objects = new LinkedList<>();
        Map<String, Long> metrics = new HashMap<>();
        try {
            ClassPool pool = new ClassPool(m.parentPool);
            ClassLoader loader = new ClassLoader(m.getClass().getClassLoader()) {};
            for (int i = 0; i < Integer.MAX_VALUE; ++i) {
                metrics = m.getMemoryPoolsMetrics();
                Class cls = m.generateRandomClass(loader, pool);
                //classes.add(cls);
                classesCache.putClass(String.valueOf(i), cls);
                //Object obj = cls.newInstance();
                //objects.add(obj);

                removeClass(cls, loader);
                classesCache.removeEmptyWeakRefs();
            }
        } finally {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Long> e : metrics.entrySet()) {
                sb.append(e.getKey() + " = " + e.getValue() / 1024 + " Kb\n");
            }
            System.out.println(sb.toString());
        }
    }

    private static void removeClass(Class cls, ClassLoader loader) throws Exception {
        Class lc = loader.getClass();
        Field f = lc.getSuperclass().getDeclaredField("classes");
        f.setAccessible(true);
        Vector<Class<?>> classes = (Vector<Class<?>>)f.get(loader);
        classes.remove(cls);
    }

    private Class generateRandomClass(ClassLoader loader, ClassPool pool) throws CannotCompileException {

        //pool.childFirstLookup = true;

        long salt = Math.abs(rnd.nextInt());
        CtClass ctCls = pool.makeClass("com.tradingview.RandomClass" + salt + System.nanoTime());
        CtMethod ctMethod = CtMethod.make("public java.lang.String helloWorld" + salt + "(){return \"Hello, World!\";}", ctCls);
        ctCls.addMethod(ctMethod);
        //Class res = ctCls.toClass(new ClassLoader(loader){}, null);
        Class res = ctCls.toClass(loader, null);
        return res;
    }

    private Map<String, Long> getMemoryPoolsMetrics() {
        Map<String, Long> res = new TreeMap<>();
        for (MemoryPoolMXBean memoryMXBean : ManagementFactory.getMemoryPoolMXBeans())
        {
            if (!memoryMXBean.isValid())
            {
                continue;
            }

            String name = memoryMXBean.getName().toLowerCase().replace(' ', '_');

            MemoryUsage usage = memoryMXBean.getUsage();
            res.put(name + "_used", usage.getUsed());
            res.put(name + "_commited", usage.getCommitted());

            MemoryUsage peakUsage = memoryMXBean.getPeakUsage();
            res.put(name + "_used_peak", peakUsage.getUsed());
            res.put(name + "_commited_peak", peakUsage.getCommitted());

            if (memoryMXBean.isUsageThresholdSupported())
            {
                res.put(name + "_usage_threshold", memoryMXBean.getUsageThreshold());
                res.put(name + "_usage_threshold_count", memoryMXBean.getUsageThresholdCount());
            }

            if (memoryMXBean.isCollectionUsageThresholdSupported())
            {
                res.put(name + "_collection_usage_threshold", memoryMXBean.getCollectionUsageThreshold());
                res.put(name + "_collection_usage_threshold_count", memoryMXBean.getCollectionUsageThresholdCount());
            }
        }
        return res;
    }
}

