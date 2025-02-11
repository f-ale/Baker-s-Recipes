package com.francescoalessi.recipes.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors
{

    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final ExecutorService diskIO;

    private AppExecutors(ExecutorService diskIO)
    {
        this.diskIO = diskIO;
    }

    public static AppExecutors getInstance()
    {
        if (sInstance == null)
        {
            synchronized (LOCK)
            {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public ExecutorService diskIO()
    {
        return diskIO;
    }
}