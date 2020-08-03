package com.cmhi.downloadtools;

import android.util.Log;

import com.cmhi.downloadtools.bean.DownloadEventMsg;
import com.cmhi.downloadtools.bean.QueueEventMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.cmhi.downloadtools.DownloadThread.QUEUE_FINISHED;
import static com.cmhi.downloadtools.DownloadThread.QUEUE_RESPONSE;
import static com.cmhi.downloadtools.DownloadThread.THREAD_FAIL;
import static com.cmhi.downloadtools.DownloadThread.THREAD_FINISHED;

public class DownQueue {
    private static final String TAG = "DownQueue";
    //线程锁，如果对这个不懂，百度一下
    private Lock lock = new ReentrantLock();
    //任务集合
    private List<DownloadThread> threads = new ArrayList<>();
    //任务状态的清单数据
    private List<String> data = new ArrayList<>();
    private int threadFinishedCount = 0;    //已完成任务的数量
    private int count = 0;                  //已添加多少个任务
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public DownQueue() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    public void add(DownloadThread thread) {
        lock.lock();
        if (!isAdded(thread.getName())) {
            threads.add(thread);
            count++;
        }
        lock.unlock();
    }

    public void start() {
        lock.lock();
        if (threads.size() <= count && threads.size() > threadFinishedCount) {
            DownloadThread thread = threads.get(threadFinishedCount);
            if (!thread.isStarted()) {
                //开始一个新的下载任务
                if (!thread.Success)
                    fixedThreadPool.execute(thread);
                else {
                    //开始下一个任务
                    threadFinishedCount++;
                    start();
                    lock.unlock();
                }
            }
        } else {
            int failCount = 0;
            for (DownloadThread thread : threads) {
                if (!thread.Success) {
                    failCount++;
                }
            }
            if (failCount > 0) {
                count = threads.size();
                threadFinishedCount = 0;
                start();
                lock.unlock();
            } else {
                clear();
                EventBus.getDefault().post(new DownloadEventMsg(QUEUE_FINISHED));
            }
        }
        lock.unlock();
    }

    public void destroy() {
        if (threads.size() > 0) {
            for (int index = 0; index < threads.size(); index++) {
                try {
                    Thread.sleep(100);
                    threads.remove(index);
                } catch (Exception e) {
                    Log.i(TAG, "in to exception");
                    e.printStackTrace();
                    break;
                }
            }
        }
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this); //注销事件
        threads.clear();
        data.clear();
    }

    public boolean isAdded(String threadName) {
        int count = 0;
        for (DownloadThread thread : threads) {
            if (thread.getName().contains(threadName)) {
                count++;
            }
        }
        return count > 0;
    }

    public int count() {
        return count;
    }

    public int getThreadFinishedCount() {
        return threadFinishedCount;
    }

    public DownloadThread findTaskByName(String taskName) {
        for (DownloadThread thread : threads) {
            if (thread.getName().contains(taskName)) {
                return thread;
            }
        }
        return null;
    }

    public int findIndexByName(String taskName) {
        int index = 0;
        for (DownloadThread thread : threads) {
            if (thread.getName().contains(taskName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    public int getCurrentIndex() {
        return threadFinishedCount;
    }

    public DownloadThread getCurrentTask() {
        return threads.get(threadFinishedCount);
    }

    public DownloadThread removeCurrentTask() {
        return threads.remove(threadFinishedCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(QueueEventMsg msg) {
        if (msg.getType() == THREAD_FINISHED) {
            lock.lock();
            if (threads.size() >= threadFinishedCount) {
                //设置当前下载任务已完成
                data.add(msg.getMsg() + "下载完成");
                StringBuilder text = new StringBuilder();
                for (String s : data) {
                    text.append(s).append("\n");
                }
                //-----------------成功的任务，做标识--------------------
                DownloadThread currentThread = removeCurrentTask();
                currentThread.Success = true;
                threads.add(threadFinishedCount, currentThread);
                //-----------------------------------------------------
                EventBus.getDefault().post(new DownloadEventMsg(THREAD_FINISHED, msg.getMsg(), text));
                //开始下一个任务
                threadFinishedCount++;
                start();
            }
            lock.unlock();
        } else if (msg.getType() == THREAD_FAIL) {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            Log.e(TAG, msg.getMsg() + "下载失败");
            data.add(msg.getMsg() + "下载失败");
            //------------失败的任务，做标识--------------------
            DownloadThread currentThread = removeCurrentTask();
            currentThread.Success = false;
            threads.add(threadFinishedCount, currentThread);
            //-----------------------------------------------
            EventBus.getDefault().post(new DownloadEventMsg(QUEUE_RESPONSE, msg.getMsg()));
            //开始下一个任务
            threadFinishedCount++;
            start();
        }
    }

    public void clear() {
        if (threads.size() > 0) {
            data.clear();
            threads.clear();
            count = 0;
            threadFinishedCount = 0;
        }
    }
}
