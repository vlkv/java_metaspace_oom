package com.tradingview;

import com.sun.org.apache.bcel.internal.util.ClassLoader;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;

public class Main {

    final private ClassPool parentPool = new ClassPool(true);

    public static void main(String[] args) {
        Main m = new Main();
        List<Class> classes = new LinkedList<>();
        Map<String, Long> metrics = new HashMap<>();
        try {
            for (int i = 0; i < Integer.MAX_VALUE; ++i) {
                metrics = m.getMemoryPoolsMetrics();
                Class cls = m.generateRandomClass();
                classes.add(cls);
            }
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder();
            sb.append("Exception catched: " + ex + "\n");
            for (Map.Entry<String, Long> e : metrics.entrySet()) {
                sb.append(e.getKey() + " = " + e.getValue() / 1024 + " Kb\n");
            }
            System.out.println(sb.toString());
        } finally {
            StringBuilder sb = new StringBuilder();

            System.out.println(sb.toString());
        }
    }

    private Class generateRandomClass() throws CannotCompileException {
        ClassPool pool = new ClassPool(parentPool);
        pool.childFirstLookup = true;

        ClassLoader loader = new ClassLoader(this.getClass().getClassLoader());

        CtClass ctCls = pool.makeClass("java.util.Map");
        Random rnd = new Random(System.currentTimeMillis());
        long salt = Math.abs(rnd.nextInt());
        CtMethod ctMethod = CtMethod.make("public java.lang.String helloWorld" + salt + "(){return \"Hello, World!\";}", ctCls);
        ctCls.addMethod(ctMethod);
        ctCls.replaceClassName("java.util.Map", "com.tradingview.RandomClass" + salt);
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

