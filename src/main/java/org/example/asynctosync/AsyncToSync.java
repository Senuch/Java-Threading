package org.example.asynctosync;

class Demonstration {
    public static void main(String[] args) throws Exception {
        SyncExecutor executor = new SyncExecutor();
        executor.asynchronousExecution(() -> System.out.println("I am done"));

        System.out.println("main thread exiting...");
    }
}


interface Callback {
    void done();
}

class AsyncExecutor {
    public void asynchronousExecution(Callback callback) throws InterruptedException {

        Thread t = new Thread(() -> {
            // Do some useful work
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            callback.done();
        });
        t.start();
    }
}

class SyncExecutor extends AsyncExecutor {
    @Override
    public void asynchronousExecution(Callback callback) throws InterruptedException {
        var signal = new Object();
        final boolean[] isDone = new boolean[1];
        var cb = new Callback() {
            @Override
            public void done() {
                callback.done();
                synchronized (signal){
                    signal.notify();
                    isDone[0] = true;
                }
            }
        };

        super.asynchronousExecution(cb);

        synchronized (signal){
            while (!isDone[0]){
                signal.wait();
            }
        }
    }
}