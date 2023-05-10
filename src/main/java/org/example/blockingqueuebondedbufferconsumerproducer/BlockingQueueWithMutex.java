package org.example.blockingqueuebondedbufferconsumerproducer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueWithMutex<T> {
    private final T[] array;
    private final int capacity;
    private int size = 0;
    private int head = 0;
    private int tail = 0;
    private final Lock lock = new ReentrantLock();

    public BlockingQueueWithMutex(int capacity) {
        //noinspection unchecked
        array = (T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enqueue(T item) throws InterruptedException {
        lock.lock();

        while (size == capacity) {
            lock.unlock();

            lock.lock();
        }

        array[tail] = item;
        tail = (tail + 1) % capacity;
        size++;

        lock.unlock();
    }

    public T dequeue() throws InterruptedException {
        T result;
        lock.lock();

        while (size == 0) {
            lock.unlock();

            lock.lock();
        }

        result = array[head];
        array[head] = null;
        head = (head + 1) % capacity;
        size--;

        lock.unlock();

        return result;

    }
}

class Demonstration {
    public static void main(String[] args) throws InterruptedException {
        final BlockingQueueWithMutex<Integer> q = new BlockingQueueWithMutex<>(5);

        Thread producer1 = new Thread(() -> {
            try {
                int i = 1;
                while (true) {
                    q.enqueue(i);
                    System.out.println("Producer thread 1 enqueued " + i);
                    i++;
                }
            } catch (InterruptedException ignored) {
            }
        });

        Thread producer2 = new Thread(() -> {
            try {
                int i = 5000;
                while (true) {
                    q.enqueue(i);
                    System.out.println("Producer thread 2 enqueued " + i);
                    i++;
                }
            } catch (InterruptedException ignored) {

            }
        });

        Thread producer3 = new Thread(() -> {
            try {
                int i = 100000;
                while (true) {
                    q.enqueue(i);
                    System.out.println("Producer thread 3 enqueued " + i);
                    i++;
                }
            } catch (InterruptedException ignored) {

            }
        });

        Thread consumer1 = new Thread(() -> {
            try {
                while (true) {
                    System.out.println("Consumer thread 1 dequeued " + q.dequeue());
                }
            } catch (InterruptedException ignored) {

            }
        });

        Thread consumer2 = new Thread(() -> {
            try {
                while (true) {
                    System.out.println("Consumer thread 2 dequeued " + q.dequeue());
                }
            } catch (InterruptedException ignored) {

            }
        });

        Thread consumer3 = new Thread(() -> {
            try {
                while (true) {
                    System.out.println("Consumer thread 3 dequeued " + q.dequeue());
                }
            } catch (InterruptedException ignored) {

            }
        });

        producer1.setDaemon(true);
        producer2.setDaemon(true);
        producer3.setDaemon(true);
        consumer1.setDaemon(true);
        consumer2.setDaemon(true);
        consumer3.setDaemon(true);

        producer1.start();
        producer2.start();
        producer3.start();

        consumer1.start();
        consumer2.start();
        consumer3.start();

        Thread.sleep(1000);
    }
}