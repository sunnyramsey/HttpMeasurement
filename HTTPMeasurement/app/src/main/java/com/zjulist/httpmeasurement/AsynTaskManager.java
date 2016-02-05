package com.zjulist.httpmeasurement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by dell on 2016/1/31.
 */
public class AsynTaskManager {
    private static AsynTaskManager instance = null;
    private ExecutorService executor;
    public static AsynTaskManager getInstance()
    {
        if(instance == null)
        {
            instance = new AsynTaskManager(5);
        }
        return instance;
    }

    private AsynTaskManager(int size)
    {
        executor = Executors.newFixedThreadPool(size);
    }

    public void postTask(Runnable task){
        executor.execute(task);
    }

    public void waitForAllTask(long timeout){
        executor.shutdown();
        try {
            executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
