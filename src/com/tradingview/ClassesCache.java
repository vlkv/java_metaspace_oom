package com.tradingview;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


public class ClassesCache
{
    private class WeakClassRef extends WeakReference<Class> {
        public String IL;
        public WeakClassRef(String IL, Class referent, ReferenceQueue<? super Class> q)
        {
            super(referent, q);
            this.IL = IL;
        }

    }

    final private Map<String, WeakClassRef> classes = new HashMap<>();
    final private ReferenceQueue<Class> referenceQueue = new ReferenceQueue<>();

    final private long cleanupPeriodMillis;
    private long lastCleanupTime = 0;

    public ClassesCache(int cleanupPeriodMs)
    {
        System.out.println("Creating a ClassesCache instance with params" + " cleanupPeriodMs:" + cleanupPeriodMs);
        cleanupPeriodMillis = cleanupPeriodMs;
    }


    public synchronized Class getClass(String IL)
    {
        final WeakReference<Class> wr = classes.get(IL);
        if (wr == null)
        {
            return null;
        }

        final Class result = wr.get();
        if (result == null)
        {
            classes.remove(IL);
        }

        final long curTime = System.currentTimeMillis();
        if (curTime - lastCleanupTime > cleanupPeriodMillis)
        {
            removeEmptyWeakRefs();
        }
        return result;
    }


    public synchronized void putClass(String IL, Class cls)
    {
        WeakClassRef weakCls = new WeakClassRef(IL, cls, referenceQueue);
        classes.put(IL, weakCls);
    }

    public void removeEmptyWeakRefs()
    {
        long removedCount = 0;

        Reference<? extends Class> wr;
        while ((wr = referenceQueue.poll()) != null) {
            removedCount++;
            classes.remove(((WeakClassRef)wr).IL);

        }

        long aliveCount = classes.size();

        lastCleanupTime = System.currentTimeMillis();

        System.out.println("ClassesCache cleanup finished, classes_removed:" + removedCount + " classes_alive:" + aliveCount);
    }


}
