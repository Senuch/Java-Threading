package org.example.nonblockingstack;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) throws Exception {

        NonblockingStack<Integer> stack = new NonblockingStack<>();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        int numThreads = 2;
        CyclicBarrier barrier = new CyclicBarrier(numThreads);

        //long start = System.currentTimeMillis();
        Integer testValue = 51;

        try {
            for (int i = 0; i < numThreads; i++) {
                executorService.submit(() -> {
                    for (int i1 = 0; i1 < 10000; i1++) {
                        stack.push(testValue);
                    }

                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException ex) {
                        System.out.println("ignoring exception");
                        //ignore both exceptions
                    }

                    for (int i1 = 0; i1 < 10000; i1++) {
                        stack.pop();
                    }
                });
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }

        System.out.println("Number of elements in the stack = " + stack.size());
    }
    public static class NonblockingStack<T> {
        private final AtomicInteger count = new AtomicInteger(0);
        private final AtomicReference<StackNode<T>> top = new AtomicReference<>();

        public int size() {
            return count.get();
        }

        public void push(T newItem) {
            StackNode<T> oldTop;
            StackNode<T> newTop;
            do {
                oldTop = top.get();
                newTop = new StackNode<>(newItem);
                newTop.setNext(oldTop);
            } while (!top.compareAndSet(oldTop, newTop));

            count.incrementAndGet();
        }

        public T pop() {
            StackNode<T> oldTop;
            StackNode<T> newTop;

            do {
                oldTop = top.get();
                if (oldTop == null) return null;
                newTop = oldTop.getNext();
            } while (!top.compareAndSet(oldTop, newTop));

            count.decrementAndGet();
            return oldTop.getItem();
        }
    }

    public static class StackNode<T> {
        private final T item;
        private StackNode<T> next;

        public StackNode(T item) {
            this.item = item;
        }

        public StackNode<T> getNext() {
            return next;
        }

        public void setNext(StackNode<T> stackNode) {
            next = stackNode;
        }

        public T getItem() {
            return this.item;
        }
    }
}