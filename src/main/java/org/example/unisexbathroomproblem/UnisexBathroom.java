package org.example.unisexbathroomproblem;

import java.util.concurrent.Semaphore;

public class UnisexBathroom {
    public static void main(String[] args) throws InterruptedException {
        UnisexBathroom.runTest();
    }

    public enum User {
        NONE,
        MALE,
        FEMALE
    }

    private User inUseBy = User.NONE;
    int employeesInBathroom = 0;
    Semaphore maxEmployees = new Semaphore(3);

    void useBathroom(String name) throws InterruptedException {
        System.out.println(name + " using bathroom. Current employees in bathroom = " + employeesInBathroom);
        Thread.sleep(10000);
        System.out.println(name + " done using bathroom");
    }

    void maleUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while (inUseBy == User.FEMALE) {
                wait();
            }
            maxEmployees.acquire();
            inUseBy = User.MALE;
            employeesInBathroom++;
        }

        useBathroom(name);
        maxEmployees.release();

        synchronized (this) {
            employeesInBathroom--;
            if (employeesInBathroom == 0) {
                inUseBy = User.NONE;
            }

            this.notifyAll();
        }
    }

    @SuppressWarnings("SameParameterValue")
    void femaleUseBathroom(String name) throws InterruptedException {
        synchronized (this) {
            while (inUseBy == User.MALE) {
                wait();
            }
            maxEmployees.acquire();
            inUseBy = User.FEMALE;
            employeesInBathroom++;
        }

        useBathroom(name);
        maxEmployees.release();

        synchronized (this) {
            employeesInBathroom--;
            if (employeesInBathroom == 0) {
                inUseBy = User.NONE;
            }
            this.notifyAll();
        }
    }

    public static void runTest() throws InterruptedException {

        final UnisexBathroom unisexBathroom = new UnisexBathroom();

        Thread female1 = new Thread(() -> {
            try {
                unisexBathroom.femaleUseBathroom("Lisa");
            } catch (InterruptedException ignored) {

            }
        });

        Thread male1 = new Thread(() -> {
            try {
                unisexBathroom.maleUseBathroom("John");
            } catch (InterruptedException ignored) {

            }
        });

        Thread male2 = new Thread(() -> {
            try {
                unisexBathroom.maleUseBathroom("Bob");
            } catch (InterruptedException ignored) {

            }
        });

        Thread male3 = new Thread(() -> {
            try {
                unisexBathroom.maleUseBathroom("Anil");
            } catch (InterruptedException ignored) {

            }
        });

        Thread male4 = new Thread(() -> {
            try {
                unisexBathroom.maleUseBathroom("Wentao");
            } catch (InterruptedException ignored) {

            }
        });

        female1.start();
        male1.start();
        male2.start();
        male3.start();
        male4.start();

        female1.join();
        male1.join();
        male2.join();
        male3.join();
        male4.join();

    }

}
