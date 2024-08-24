package thread;

import java.util.concurrent.ArrayBlockingQueue;

public class Worker implements Runnable{
    // 用于保存任务的队列
    private ArrayBlockingQueue<Runnable> tasks;
    // 线程的状态 是否终止
    private volatile boolean isStopped;
    // 保存执行 run 方法的线程
    private volatile Thread thisThread;
    //构造函数
    public Worker(ArrayBlockingQueue<Runnable> tasks){
        this.tasks=tasks;
    }

    @Override
    public void run() {
        thisThread=Thread.currentThread();
        //不断的去任务队列里面取出任务然后执行
        while(!isStopped){
            try{
                Runnable task=tasks.take();
                task.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // 注意是其他线程调用这个方法 同时需要注意是 thisThread 这个线程在执行上面的 run 方法
    // 其他线程调用 thisThread 的 interrupt 方法之后 thisThread 会出现异常 然后就不会一直阻塞了
    // 会判断 isStopped 是否为 true 如果为 true 的话就可以退出 while 循环了
    public void stop(){
        isStopped=true;
        thisThread.interrupt();
    }
    public boolean isStopped(){
        return isStopped;
    }
}
