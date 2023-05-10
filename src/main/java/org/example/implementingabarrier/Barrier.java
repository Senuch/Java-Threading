package org.example.implementingabarrier;

public class Barrier {
    public static void main(String[] args) throws Exception {
        Barrier.runTest();
    }

    private int count = 0;
    private int released = 0;
    private final int totalThreads;

    public Barrier(int totalThreads) {
        this.totalThreads = totalThreads;
    }

    public synchronized void await() throws InterruptedException {
        while (count == totalThreads) {
            wait();
        }

        count++;

        if (count == totalThreads) {
            notifyAll();
            released = totalThreads;
        } else {
            while (count < totalThreads) {
                wait();
            }
        }

        released--;
        if (released == 0) {
            count = 0;
            notifyAll();
        }

    }

    public static void runTest() throws InterruptedException {
        final Barrier barrier = new Barrier(3);

        Thread p1 = new Thread(() -> {
            try {
                System.out.println("Thread 1");
                barrier.await();
                System.out.println("Thread 1");
                barrier.await();
                System.out.println("Thread 1");
                barrier.await();
            } catch (InterruptedException ignored) {
            }
        });

        Thread p2 = new Thread(() -> {
            try {
                Thread.sleep(500);
                System.out.println("Thread 2");
                barrier.await();
                Thread.sleep(500);
                System.out.println("Thread 2");
                barrier.await();
                Thread.sleep(500);
                System.out.println("Thread 2");
                barrier.await();
            } catch (InterruptedException ignored) {
            }
        });

        Thread p3 = new Thread(() -> {
            try {
                Thread.sleep(1500);
                System.out.println("Thread 3");
                barrier.await();
                Thread.sleep(1500);
                System.out.println("Thread 3");
                barrier.await();
                Thread.sleep(1500);
                System.out.println("Thread 3");
                barrier.await();
            } catch (InterruptedException ignored) {
            }
        });

        p1.start();
        p2.start();
        p3.start();

        p1.join();
        p2.join();
        p3.join();
    }
}
