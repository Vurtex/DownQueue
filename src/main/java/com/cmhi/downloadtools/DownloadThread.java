package com.cmhi.downloadtools;

import android.os.FileUtils;

import com.cmhi.downloadtools.bean.DownloadEventMsg;
import com.cmhi.downloadtools.bean.QueueEventMsg;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Vurtex
 */
public class DownloadThread extends Thread {

    //开始下载
    public final static int THREAD_BEGIN = 1001;
    //下载结束
    public final static int THREAD_FINISHED = 1002;
    //更新进度
    public final static int THREAD_PROGRESS = 1003;
    //当前队列任务结束
    public final static int QUEUE_FINISHED = 1004;
    //文件名
    private String fileName;
    //下载进度
    private int percent = 0;
    //下载路径
    private URL url;
    //下载的文件大小
    private long fileLength;
    //文件的保存路径
    private String filePath;
    //是否线程已启动
    private boolean isStarted = false;


    public DownloadThread(URL url, String filePath) {
        this.url = url;
        this.filePath = filePath;
        String[] strs = filePath.split("/");
        this.fileName = strs[strs.length - 1];
    }

    /**
     * 开始下载任务
     */
    @Override
    public void run() {
        isStarted = true;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();    //建立一个远程连接句柄，此时尚未真正连接
            conn.setConnectTimeout(5 * 1000);    //设置连接超时时间为5秒
            conn.setRequestMethod("GET");    //设置请求方式为GET
            conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            conn.setRequestProperty("Charset", "UTF-8");    //设置客户端编码
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");    //设置用户代理
            conn.setRequestProperty("Connection", "Keep-Alive");    //设置Connection的方式
            conn.connect();    //和远程资源建立真正的连接，但尚无返回的数据流

            fileLength = conn.getContentLength();
            byte[] buffer = new byte[8096];        //下载的缓冲池为8KB

            bis = new BufferedInputStream(conn.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(new File(filePath+".debris")));

            long downloadLength = 0;//当前已下载的文件大小
            int bufferLength = 0;

            while ((bufferLength = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bufferLength);
                bos.flush();
                //计算当前下载进度
                downloadLength += bufferLength;
                percent = Integer.valueOf(100 * downloadLength / fileLength + "");
                EventBus.getDefault().post(new DownloadEventMsg(THREAD_PROGRESS, percent, fileName));
            }
            //发送下载完毕的消息
            new File(filePath+".debris").renameTo(new File(filePath));
            EventBus.getDefault().post(new QueueEventMsg(THREAD_FINISHED, fileName));
        } catch (Exception e) {
            e.printStackTrace();
            //TODO:建议这里发送下载失败的消息
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public URL getUrl() {
        return url;
    }

    public float getPercent() {
        return percent;
    }

    public boolean isStarted() {
        return isStarted;
    }
}