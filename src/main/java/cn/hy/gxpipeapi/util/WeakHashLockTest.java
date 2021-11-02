package cn.hy.gxpipeapi.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class WeakHashLockTest implements Runnable{

    private final Integer key;
    private final String type;
    private static Integer a = 10;
    private static Integer b = 10;
    private final static WeakHashLock<String> weakHashLock = new WeakHashLock<>();

    public WeakHashLockTest(Integer key, String type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public void run() {
        ReentrantLock lock = weakHashLock.get(type);
        lock.lock();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (type.equals("a")) {
            if (a > key) {
                a -= key;
                System.out.println(Thread.currentThread().getName() + "扣除库存：" + key + ",剩余库存：" + a);
            } else {
                System.out.println(Thread.currentThread().getName() +"扣除库存：" + key +  ",a库存不足,剩余库存：" + a);
            }
        } else if (type.equals("b")) {
            if (b > key) {
                b -= key;
                System.out.println(Thread.currentThread().getName() + "扣除库存：" + key + ",剩余库存：" + b);
            } else {
                System.out.println(Thread.currentThread().getName() +"扣除库存：" + key +  ",b库存不足,剩余库存：" + b);
            }
        }
        lock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i < 10; i++) {
            if (i % 2 != 0) {
                WeakHashLockTest test = new WeakHashLockTest(i, "a");
                Thread thread = new Thread(test, "a-" + i);
                thread.start();
            } else {
                WeakHashLockTest test = new WeakHashLockTest(i, "b");
                Thread thread = new Thread(test, "b-" + i);
                thread.start();
            }
            Thread.sleep(1);
        }

    }
}
