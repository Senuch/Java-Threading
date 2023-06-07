package org.example.barbershop;

import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

class Demonstration {
    public static void main(String[] args) throws InterruptedException {
        BarberShop.runTest();
    }
}

public class BarberShop {
    final int CHAIRS = 3;
    Semaphore waitForCustomerToEnter = new Semaphore(0);
    Semaphore waitForBarberToGetReady = new Semaphore(0);
    Semaphore waitForCustomerToLeave = new Semaphore(0);
    Semaphore waitForBarberToCutHair = new Semaphore(0);
    int waitingCustomers = 0;
    int hairCutsGiven = 0;
    ReentrantLock lock = new ReentrantLock();

    void customerWalksIn() throws InterruptedException {
        lock.lock();
        if (waitingCustomers == CHAIRS) {
            lock.unlock();
            return;
        }
        waitingCustomers++;
        lock.unlock();

        waitForCustomerToEnter.release();
        waitForBarberToGetReady.acquire();

        lock.lock();
        waitingCustomers--;
        lock.unlock();

        waitForBarberToCutHair.acquire();
        waitForCustomerToLeave.release();
    }

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    void barber() throws InterruptedException {
        while (true) {
            waitForCustomerToEnter.acquire();
            waitForBarberToGetReady.release();
            hairCutsGiven++;
            System.out.println("Barber cutting hair..." + hairCutsGiven);
            Thread.sleep(50);
            waitForBarberToCutHair.release();
            waitForCustomerToLeave.acquire();
        }
    }

    public static void runTest() throws InterruptedException {

        HashSet<Thread> set = new HashSet<>();
        final BarberShop barberShopProblem = new BarberShop();

        Thread barberThread = new Thread(() -> {
            try {
                barberShopProblem.barber();
            } catch (InterruptedException ignored) {

            }
        });
        barberThread.start();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                try {
                    barberShopProblem.customerWalksIn();
                } catch (InterruptedException ignored) {

                }
            });
            set.add(t);
        }

        for (Thread t : set) {
            t.start();
        }

        for (Thread t : set) {
            t.join();
        }

        set.clear();
        Thread.sleep(800);

        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(() -> {
                try {
                    barberShopProblem.customerWalksIn();
                } catch (InterruptedException ignored) {

                }
            });
            set.add(t);
        }
        for (Thread t : set) {
            t.start();
        }

        barberThread.join();
    }
}
