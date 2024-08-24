package thread;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class MyFixedThreadPool {
    // 用于存储任务的阻塞队列
    private ArrayBlockingQueue<Runnable> taskQueue;

    // 保存线程池当中所有的线程
    private ArrayList<Worker> threadLists;

    // 线程池是否关闭
    private boolean isShutDown;

    // 线程池当中的线程数目
    private int numThread;

    public MyFixedThreadPool(int i) {
        this(Runtime.getRuntime().availableProcessors() + 1, 1024);
    }

    public MyFixedThreadPool(int numThread, int maxTaskNumber) {
        this.numThread = numThread;
        taskQueue = new ArrayBlockingQueue<>(maxTaskNumber); // 创建阻塞队列
        threadLists = new ArrayList<>();
        // 将所有的 worker 都保存下来
        for (int i = 0; i < numThread; i++) {
            //worker类用来从任务队列中取任务
            Worker worker = new Worker(taskQueue);
            threadLists.add(worker);
        }
        for (int i = 0; i < threadLists.size(); i++) {
            new Thread(threadLists.get(i),
                    "ThreadPool-Thread-" + i).start(); // 让worker开始工作
        }
    }
    private void stopAllThread() {
        for (Worker worker : threadLists) {
            worker.stop(); // 调用 worker 的 stop 方法 让正在执行 worker 当中 run 方法的线程停止执行
        }
    }

    public void shutDown() {
        // 等待任务队列当中的任务执行完成
        while (taskQueue.size() != 0) {
            // 如果队列当中还有任务 则让出 CPU 的使用权
            Thread.yield();
        }
        // 在所有的任务都被执行完成之后 停止所有线程的执行
        stopAllThread();
    }
    public void submit(Runnable runnable) {
        try {
            taskQueue.put(runnable); // 如果任务队列满了， 调用这个方法的线程会被阻塞
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
