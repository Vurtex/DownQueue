package com.cmhi.downloadtools;

import android.util.Log;

import com.cmhi.downloadtools.bean.DownloadEventMsg;
import com.cmhi.downloadtools.bean.QueueEventMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.cmhi.downloadtools.DownloadThread.QUEUE_FINISHED;
import static com.cmhi.downloadtools.DownloadThread.THREAD_FINISHED;

public class DownQueue {
    private static final String TAG = "DownQueue";
    //线程锁，如果对这个不懂，百度一下
    private Lock lock = new ReentrantLock();
    //任务集合
    private List<DownloadThread> threads = new ArrayList<>();
    //任务状态的清单数据
    List<String> data = new ArrayList<>();
    public static int threadFinishedCount = 0;//已完成任务的数量
    private int count = 0;                //已添加多少个任务

    public DownQueue() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    public void add(DownloadThread thread) {
        lock.lock();
        count++;
        threads.add(thread);
        lock.unlock();
    }

    public void start() {
        lock.lock();
        if (threads.size() <= count && threads.size() > threadFinishedCount) {
            if (!threads.get(threadFinishedCount).isStarted()) {
                //开始一个新的下载任务
                threads.get(threadFinishedCount).start();
            }
        } else {
            Log.i(TAG, "队列已经没有任务了");
            EventBus.getDefault().post(new DownloadEventMsg(QUEUE_FINISHED));
        }
        lock.unlock();
    }

    public void destroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this); //注销事件
        threads.clear();
        data.clear();
    }

    public int count() {
        return threads.size();
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
                    text.append(s + "\n");
                }
                EventBus.getDefault().post(new DownloadEventMsg(THREAD_FINISHED, msg.getMsg(), text));
                //---------------------------
                threadFinishedCount++;
                //开始下一个任务
                start();
            }
            lock.unlock();
        }
    }
}
