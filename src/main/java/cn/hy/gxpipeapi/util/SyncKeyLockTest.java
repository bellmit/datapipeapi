package cn.hy.gxpipeapi.util;

import org.apache.tools.ant.util.DateUtils;

import java.sql.SQLOutput;

public class SyncKeyLockTest implements Runnable{
    private final Integer key;
    private final String type;
    private static Integer a = 10;
    private static Integer b = 10;
    private static Integer c = 10;

    public SyncKeyLockTest(Integer key, String type) {
        this.key = key;
        this.type = type;
    }
    @Override
    public void run() {
        synchronized (type) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (type.equals("a")) {
                a --;
                System.out.println(Thread.currentThread().getName() + ",a:" + a);
            } else if (type.equals("b")) {
                b--;
                System.out.println(Thread.currentThread().getName() + ",b:" + b);
            } else {
                c--;
                System.out.println(Thread.currentThread().getName() + ",c:" + c);
            }

        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i < 11; i++) {
            if (i % 2 == 0) {
                SyncKeyLockTest test = new SyncKeyLockTest(i, "a");
                Thread thread = new Thread(test, "a-" + i);
                thread.start();
            } else {
                SyncKeyLockTest test = new SyncKeyLockTest(i, "b");
                Thread thread = new Thread(test, "b-" + i);
                thread.start();
            }
//            SyncKeyLockTest test = new SyncKeyLockTest(i, "c");
//            Thread thread = new Thread(test, "c-" + i);
//            thread.start();
        }

    }
}
