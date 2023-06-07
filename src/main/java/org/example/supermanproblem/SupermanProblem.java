package org.example.supermanproblem;

public class SupermanProblem {
    private static volatile SupermanProblem instance;

    private SupermanProblem() {
    }

    public static SupermanProblem getInstance() {
        if (instance == null) {
            synchronized (SupermanProblem.class) {
                if (instance == null){
                    //noinspection InstantiationOfUtilityClass
                    instance = new SupermanProblem();
                }
            }
        }

        return instance;
    }
}
