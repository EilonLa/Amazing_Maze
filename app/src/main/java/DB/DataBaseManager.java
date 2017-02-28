package DB;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by אילון on 06/02/2017.
 */

public class DataBaseManager extends Thread  {
    private LinkedBlockingQueue<Thread> mQueue;
    private boolean mIsAlive;
    public DataBaseManager(){
        mQueue = new LinkedBlockingQueue<>();
        mIsAlive = true;
    }

    @Override
    public void run() {
        while (mIsAlive) {
            Thread thread = null;
            try {
                thread = mQueue.take();
                thread.start();
                while (thread.isAlive()) {
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void addThread(Thread thread){
        mQueue.offer(thread);
    }
}
