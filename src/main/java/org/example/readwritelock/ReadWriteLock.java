package org.example.readwritelock;

public class ReadWriteLock {
    public static void main(String[] args) throws Exception {

        final ReadWriteLock rwl = new ReadWriteLock();

        Thread t1 = new Thread(() -> {
            try {

                System.out.println("Attempting to acquire write lock in t1: " + System.currentTimeMillis());
                rwl.acquireWriteLock();
                System.out.println("write lock acquired t1: " + System.currentTimeMillis());

                // Simulates write lock being held indefinitely
                for (; ; ) {
                    //noinspection BusyWait
                    Thread.sleep(500);
                }

            } catch (InterruptedException ignored) {

            }
        });

        Thread t2 = new Thread(() -> {
            try {

                System.out.println("Attempting to acquire write lock in t2: " + System.currentTimeMillis());
                rwl.acquireWriteLock();
                System.out.println("write lock acquired t2: " + System.currentTimeMillis());

            } catch (InterruptedException ignored) {

            }
        });

        Thread tReader1 = new Thread(() -> {
            try {

                rwl.acquireReadLock();
                System.out.println("Read lock acquired: " + System.currentTimeMillis());

            } catch (InterruptedException ignored) {

            }
        });

        Thread tReader2 = new Thread(() -> {
            System.out.println("Read lock about to release: " + System.currentTimeMillis());
            rwl.releaseReadLock();
            System.out.println("Read lock released: " + System.currentTimeMillis());
        });

        tReader1.start();
        t1.start();
        Thread.sleep(3000);
        tReader2.start();
        Thread.sleep(1000);
        t2.start();
        tReader1.join();
        tReader2.join();
        t2.join();
    }

    boolean isWriteLocked = false;
    int readers = 0;

    public synchronized void acquireReadLock() throws InterruptedException {
        while (isWriteLocked) {
            wait();
        }

        readers++;
    }

    public synchronized void releaseReadLock() {
        readers--;
        notify();
    }

    public synchronized void acquireWriteLock() throws InterruptedException {
        while (isWriteLocked || readers != 0) {
            wait();
        }

        isWriteLocked = true;
    }

    @SuppressWarnings("unused")
    public synchronized void releaseWriteLock() {
        isWriteLocked = false;
        notify();
    }
}
