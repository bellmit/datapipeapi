package cn.hy.gxpipeapi.util;

public class KeyLockUtilTest implements Runnable{
    private final Integer key;
    private final String type;
    private static Integer a = 10;
    private static Integer b = 10;

    private final KeyLockUtil<String> keyLock = new KeyLockUtil<>();

    public KeyLockUtilTest(Integer key, String type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public void run() {
        keyLock.lock(type);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (type.equals("a")) {
            if (key <= a) {
                a -= key;
                System.out.println(Thread.currentThread().getName() + ",a扣减库存：" + key);
            } else {
                System.out.println(Thread.currentThread().getName() + ",a库存不足！" + ",剩余：" + a);
            }
        } else {
            if (key <= b) {
                b -= key;
                System.out.println(Thread.currentThread().getName() + ",b扣减库存：" + key);
            } else {
                System.out.println(Thread.currentThread().getName() + ",b库存不足！" + ",剩余：" + a);
            }
        }
        keyLock.unlock(type);
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i < 11; i++) {
            Thread.sleep(100);
            if (i % 2 == 0) {
                KeyLockUtilTest test = new KeyLockUtilTest(i, "a");
                Thread thread = new Thread(test);
                thread.start();
            } else {
                KeyLockUtilTest test = new KeyLockUtilTest(i, "b");
                Thread thread = new Thread(test);
                thread.start();
            }
            Thread.sleep(1);
        }

    }
}
