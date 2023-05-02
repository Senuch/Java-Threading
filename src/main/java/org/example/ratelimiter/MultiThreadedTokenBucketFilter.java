package org.example.ratelimiter;

import java.util.HashSet;
import java.util.Set;

public class MultiThreadedTokenBucketFilter {
    private long possibleTokens = 0;
    private final int MAX_TOKENS;

    public MultiThreadedTokenBucketFilter(int maxTokens) {
        MAX_TOKENS = maxTokens;

        var thread = new Thread(() -> {
            try {
                daemonThread();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void daemonThread() throws InterruptedException {
        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (this) {
                if (possibleTokens < MAX_TOKENS) {
                    possibleTokens++;
                }
                notify();
            }

            int ONE_SECOND = 1000;
            //noinspection BusyWait
            Thread.sleep(ONE_SECOND);
        }
    }

    public void getToken() throws InterruptedException {
        synchronized (this) {
            while (possibleTokens == 0) {
                this.wait();
            }
            possibleTokens--;
        }

        System.out.println(
                "Granting " + Thread.currentThread().getName() + " token at " + System.currentTimeMillis() / 1000);
    }
}

class Demonstration {
    public static void main(String[] args) throws InterruptedException {
        Set<Thread> allThreads = new HashSet<>();
        final MultiThreadedTokenBucketFilter tokenBucketFilter = new MultiThreadedTokenBucketFilter(1);

        for (int i = 0; i < 10; i++) {

            Thread thread = new Thread(() -> {
                try {
                    tokenBucketFilter.getToken();
                } catch (InterruptedException ie) {
                    System.out.println("We have a problem");
                }
            });
            thread.setName("Thread_" + (i + 1));
            allThreads.add(thread);
        }

        for (Thread t : allThreads) {
            t.start();
        }

        for (Thread t : allThreads) {
            t.join();
        }

    }
}
