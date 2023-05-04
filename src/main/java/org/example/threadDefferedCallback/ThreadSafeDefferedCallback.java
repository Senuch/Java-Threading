package org.example.threadDefferedCallback;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeDefferedCallback {
    private final PriorityQueue<Callback> pq = new PriorityQueue<>((o1, o2) -> (int) (o1.executeAt - o2.executeAt));
    Lock lock = new ReentrantLock();
    Condition lockCondition = lock.newCondition();

    private void start() throws InterruptedException {
        long sleepFor;

        //noinspection InfiniteLoopStatement
        while (true) {
            lock.lock();

            while (pq.size() == 0) {
                lockCondition.await();
            }

            while (pq.size() != 0) {
                sleepFor = pq.peek().executeAt - System.currentTimeMillis();

                if (sleepFor <= 0) {
                    break;
                }

                //noinspection ResultOfMethodCallIgnored
                lockCondition.await(sleepFor, TimeUnit.MILLISECONDS);
            }

            Callback cb = pq.poll();
            assert cb != null;
            System.out.println(
                    "Executed at " + System.currentTimeMillis() / 1000 + " required at " + cb.executeAt / 1000
                            + ": message:" + cb.message);

            lock.unlock();
        }
    }

    public void registerCallback(Callback callback) {
        lock.lock();
        pq.add(callback);
        lockCondition.signal();
        lock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        runTestTenCallbacks();
    }

    public static void runTestTenCallbacks() throws InterruptedException {
        Set<Thread> allThreads = new HashSet<>();
        final ThreadSafeDefferedCallback deferredCallbackExecutor = new ThreadSafeDefferedCallback();

        Thread service = new Thread(() -> {
            try {
                deferredCallbackExecutor.start();
            } catch (InterruptedException ignored) {

            }
        });

        service.start();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                Callback cb = new Callback(1, "Hello this is " + Thread.currentThread().getName());
                deferredCallbackExecutor.registerCallback(cb);
            });
            thread.setName("Thread_" + (i + 1));
            thread.start();
            allThreads.add(thread);
            Thread.sleep(1000);
        }

        for (Thread t : allThreads) {
            t.join();
        }
    }

    private static class Callback {
        long executeAt;
        String message;

        public Callback(long executeAfter, String message) {
            this.executeAt = System.currentTimeMillis() + (executeAfter * 1000);
            this.message = message;
        }
    }
}