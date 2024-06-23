package project;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSync {

    private static final int MAX_ITERATIONS = 1000;

    private static AtomicInteger A1 = new AtomicInteger(0);
    private static AtomicInteger A2 = new AtomicInteger(0);
    private static AtomicInteger A3 = new AtomicInteger(0);
    private static AtomicInteger B1 = new AtomicInteger(0);
    private static AtomicInteger B2 = new AtomicInteger(0);
    private static AtomicInteger B3 = new AtomicInteger(0);

    private static CountDownLatch latchA1 = new CountDownLatch(1);
    private static CountDownLatch latchB1 = new CountDownLatch(1);
    private static CountDownLatch latchB2 = new CountDownLatch(1);
    private static CountDownLatch latchA2 = new CountDownLatch(1);
    private static CountDownLatch latchB3 = new CountDownLatch(1);
    private static CountDownLatch latchA3 = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            latchA1 = new CountDownLatch(1);
            latchB1 = new CountDownLatch(1);
            latchB2 = new CountDownLatch(1);
            latchA2 = new CountDownLatch(1);
            latchB3 = new CountDownLatch(1);
            latchA3 = new CountDownLatch(1);

            Thread threadA = new Thread(new ThreadA());
            Thread threadB = new Thread(new ThreadB());

            threadA.start();
            threadB.start();

            threadA.join();
            threadB.join();
        }

        System.out.println("A1: " + A1.get());
        System.out.println("A2: " + A2.get());
        System.out.println("A3: " + A3.get());
        System.out.println("B1: " + B1.get());
        System.out.println("B2: " + B2.get());
        System.out.println("B3: " + B3.get());
    }

    static class ThreadA implements Runnable {
        @Override
        public void run() {
            try {
                // FuncA1
                A1.set(SumUtil.calculateSum(500));
                latchA1.countDown();

                latchB2.await();
                // FuncA2
                A2.set(B2.get() + SumUtil.calculateSum(300));
                latchA2.countDown();

                latchB3.await();
                // FuncA3
                A3.set(B3.get() + SumUtil.calculateSum(400));
                latchA3.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class ThreadB implements Runnable {
        @Override
        public void run() {
            try {
                // FuncB1
                B1.set(SumUtil.calculateSum(250));
                latchB1.countDown();

                latchA1.await();
                // FuncB2
                B2.set(A1.get() + SumUtil.calculateSum(200));
                latchB2.countDown();

                latchA2.await();
                // FuncB3
                B3.set(A2.get() + SumUtil.calculateSum(400));
                latchB3.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
