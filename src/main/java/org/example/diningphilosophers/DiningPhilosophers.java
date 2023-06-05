package org.example.diningphilosophers;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class DiningPhilosophers {

    public static void main(String[] args) throws InterruptedException {
        DiningPhilosophers.runTest();
    }

    private final Random random = new Random(System.currentTimeMillis());

    private final Semaphore[] forks = new Semaphore[5];
    private final Semaphore maxDiners = new Semaphore(4);

    public DiningPhilosophers() {
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Semaphore(1);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void lifecycleOfPhilosopher(int id) throws InterruptedException {
        while (true) {
            contemplate();
            eat(id);
        }
    }

    private void contemplate() throws InterruptedException {
        Thread.sleep(random.nextInt());
    }

    public void eat(int id) throws InterruptedException {
        maxDiners.acquire();

        forks[id].acquire();
        forks[(id + 1) % 5].acquire();

        System.out.println("Philosopher " + id + " is eating");

        forks[id].release();
        forks[(id + 1) % 5].acquire();

        maxDiners.release();
    }

    static void startPhilosoper(DiningPhilosophers dp, int id) {
        try {
            dp.lifecycleOfPhilosopher(id);
        } catch (InterruptedException ignored) {

        }
    }

    public static void runTest() throws InterruptedException {
        final DiningPhilosophers dp = new DiningPhilosophers();

        Thread p1 = new Thread(() -> startPhilosoper(dp, 0));

        Thread p2 = new Thread(() -> startPhilosoper(dp, 1));

        Thread p3 = new Thread(() -> startPhilosoper(dp, 2));

        Thread p4 = new Thread(() -> startPhilosoper(dp, 3));

        Thread p5 = new Thread(() -> startPhilosoper(dp, 4));

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();

        p1.join();
        p2.join();
        p3.join();
        p4.join();
        p5.join();
    }
}
