package cn.hy.gxpipeapi.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class WeakHashLock<T> {

    /**
     * map 中锁数量阈值.
     */
    private static final int LOCK_SIZE_THRESHOLD = 1000;
    private final ReferenceQueue<ReentrantLock> queue = new ReferenceQueue<>();
    private final ConcurrentHashMap<T, WeakRefLock<T, ReentrantLock>> lockMap = new ConcurrentHashMap<>();

    public ReentrantLock get(T key) {
        // 可以设置一个阈值，当锁的数量超过这个阈值时移除一部分被回收的锁
        if (this.lockMap.size() > LOCK_SIZE_THRESHOLD) {
            clearEmptyRef();
        }

        WeakRefLock<T, ReentrantLock> weakRefLock = this.lockMap.get(key);
        ReentrantLock lock = weakRefLock == null ? null : weakRefLock.get();
        while (lock == null) {
            lockMap.putIfAbsent(key, new WeakRefLock<>(new ReentrantLock(true), this.queue, key));
            // 再次从 Map 中获取，保证同一用户获取的锁是一致的
            weakRefLock = lockMap.get(key);
            lock = weakRefLock == null ? null : weakRefLock.get();
            if (lock != null) {
                return lock;
            }
            // 这里注意如果堆资源过于紧张可能会返回空的情况，需要移除一部分被回收的锁
            clearEmptyRef();
        }
        return lock;
    }

    @SuppressWarnings("unchecked")
    public void clearEmptyRef() {
        Reference<? extends ReentrantLock> ref;
        while ((ref = this.queue.poll()) != null) {
            WeakRefLock<T, ? extends ReentrantLock> weakRefLock = (WeakRefLock<T, ? extends ReentrantLock>) ref;
            this.lockMap.remove(weakRefLock.key);
        }
    }


    static final class WeakRefLock<T, K> extends WeakReference<K> {

        private final T key;

        public WeakRefLock(K referent, ReferenceQueue<? super K> queue, T key) {
            super(referent, queue);
            this.key = key;
        }
    }
}
