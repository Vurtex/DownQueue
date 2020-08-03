package com.cmhi.downloadtools;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.cmhi.downloadtools.bean.DownloadEventMsg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.cmhi.downloadtools.DownloadThread.QUEUE_FINISHED;
import static com.cmhi.downloadtools.DownloadThread.THREAD_FINISHED;
import static com.cmhi.downloadtools.DownloadThread.THREAD_PROGRESS;

public class DemoActivity extends AppCompatActivity {
    DownQueue downQueue = new DownQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            downQueue.add(new DownloadThread2(
//                    new URL("http://120.27.237.139:8089/apk/upgrade/mobile/beijing/ruantanzhen/ruantanzhen.apk"), getFilesDir().getAbsolutePath() + File.separator +
//                    "ruantanzhen" + ".apk"));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        downQueue.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DownloadEventMsg msg) {
        if (msg.getType() == THREAD_PROGRESS) {
//            int progress = msg.getArg1();
//            if (progress == 100 && appName.equals(new_tv_apkName + SUFFIX)) {
//                binding.apkNameTv.setText(String.format("%s下载完成,正在安装...", appName));
//            } else
//                binding.apkNameTv.setText(String.format(appName + "下载中...%d%%", progress));
        } else if (msg.getType() == THREAD_FINISHED) {
//            binding.downloadedTv.setText(msg.getMsg());
//            //安装动作
//            if (typeEnum == TypeEnum.DEBUG) {
//                new Thread(() -> {
//                    installApk(MainActivity.this, new File(getFilesDir().getAbsolutePath() + File.separator + appName));
//                }).start();
//            } else {
//                new Thread(() -> {
//                    executeInstallCommand(getFilesDir().getAbsolutePath() + File.separator + appName);
//                }).start();
//            }
        } else if (msg.getType() == QUEUE_FINISHED) {
            //下载完所有的第三方apk 拉起牌照方
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downQueue.destroy();
    }
}
