package org.example.blockingqueue_bondedbuffer_consumerproducer;

public class Main {
    public static void main( String args[] ) throws Exception{
        final BlockingQueue<Integer> q = new BlockingQueue<Integer>(5);

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 8; i++) {
                        q.enqueue(i);
                        System.out.println("enqueued " + i);
                    }
                } catch (InterruptedException ie) {

                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 4; i++) {
                        System.out.println("Thread 2 dequeued: " + q.dequeue());
                    }
                } catch (InterruptedException ie) {

                }
            }
        });

        Thread t3 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 4; i++) {
                        System.out.println("Thread 3 dequeued: " + q.dequeue());
                    }
                } catch (InterruptedException ie) {

                }
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

    public static class BlockingQueue<T> {
        private T[] array;
        private int capacity;
        private int size = 0;
        private int head = 0;
        private int tail = 0;

        final Object lock = new Object();

        public BlockingQueue(int capacity) {
            array = (T[]) new Object[capacity];
            this.capacity = capacity;
        }

        public void enqueue(T item) throws InterruptedException {
            synchronized (lock) {
                while (size == capacity) {
                    lock.wait();
                }

                array[tail] = item;

                tail = (tail + 1) % capacity;
                size++;

                lock.notifyAll();
            }
        }

        public T dequeue() throws InterruptedException {

            T result = null;
            synchronized (lock) {
                while (size == 0) {
                    lock.wait();
                }

                result = array[head];
                head = (head + 1) % capacity;
                size--;

                lock.notifyAll();
            }

            return result;
        }
    }
}
