package org.example.blockingqueue_bondedbuffer_consumerproducer;

class Demonstration2 {
    public static void main(String[] args) throws InterruptedException {
        final BlockingQueueWithSemaphore<Integer> q = new BlockingQueueWithSemaphore<>(5);

        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    q.enqueue(i);
                    System.out.println("enqueued " + i);
                }
            } catch (InterruptedException ignored) {

            }
        });

        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.println("Thread 2 dequeued: " + q.dequeue());
                }
            } catch (InterruptedException ignored) {

            }
        });

        Thread t3 = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.println("Thread 3 dequeued: " + q.dequeue());
                }
            } catch (InterruptedException ignored) {

            }
        });

        t1.start();
        Thread.sleep(4000);
        t2.start();
        t2.join();

        t3.start();
        t1.join();
        t3.join();

    }
}

public class BlockingQueueWithSemaphore<T> {
    private final T[] array;
    private final int capacity;
    private int head = 0;
    private int tail = 0;
    private final CountingSemaphore lock = new CountingSemaphore(1, 1);
    private final CountingSemaphore consumer;
    private final CountingSemaphore producer;

    @SuppressWarnings("unchecked")
    public BlockingQueueWithSemaphore(int capacity) {
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
        this.consumer = new CountingSemaphore(capacity, 0);
        this.producer = new CountingSemaphore(capacity, capacity);
    }

    public void enqueue(T item) throws InterruptedException {
        producer.acquire();
        lock.acquire();

        array[tail] = item;
        tail = (tail + 1) % capacity;

        lock.release();
        consumer.release();

    }

    public T dequeue() throws InterruptedException {
        T result;

        consumer.acquire();
        lock.acquire();

        result = array[head];
        array[head] = null;
        head = (head + 1) % capacity;

        lock.release();
        producer.release();

        return result;

    }

}

class CountingSemaphore {
    private int usedPermits;
    private final int maxCount;

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