package org.example.blockingqueue_bondedbuffer_consumerproducer;

public class BlockingQueueWithSemaphore<T> {
}

class CountingSemaphore {
    private int usedPermits = 0;
    private final int maxCount;

    public CountingSemaphore(int count) {
        this.maxCount = count;
    }

    public CountingSemaphore(int count, int initialPermits) {
        this.maxCount = count;
        this.usedPermits = count - initialPermits;
    }

    public synchronized void acquire() throws InterruptedException {
        while (usedPermits == maxCount) {
            wait();
        }

        notify();
        usedPermits++;
    }

    public synchronized void release() throws InterruptedException {
        while (usedPermits == 0) {
            wait();
        }

        notify();
        usedPermits--;
    }
}