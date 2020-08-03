# DownQueue
一个下载任务队列工具类
# [快速开始](https://github.com/Vurtex/DownQueue)

我将创建一个简单的API，使用自定义Thread和EventBus实现，采用观察者设计模式，git中含有MainActivity作为使用Demo参考。

#DownQueue类


- [DownQueue类](#downqueue类)    
  - [参数](#参数)    
  - [方法](#方法)    
  - [使用方法](#使用方法)

## 参数：

|               Parameter               |           Description            |
| :-----------------------------------: | :------------------------------: |
|           private Lock lock           | 线程锁，如果对这个不懂，百度一下 |
| private List<DownloadThread> threads  |             任务集合             |
|           List<String> data           |        任务状态的清单数据        |
| public static int threadFinishedCount |         已完成任务的数量         |
|           private int count           |         已添加多少个任务         |


##方法：

|                 Method                  |        Parameter         |               Description               |
| :-------------------------------------: | :----------------------: | :-------------------------------------: |
|               构造方法()                |            -             |          初始化EventBus等组件           |
| public void  add(DownloadThread thread) | 自定义的线程类，继Thread | 因为是单线程任务队列,设计上弃用线程池。 |
|           public void start()           |            -             |      开始顺序执行队列中的下载任务       |
|          public void destroy()          |            -             |   关闭队列，清楚缓存在onDestroy中使用   |
|           public int count()            |            -             |             获取当前任务数              |

## 使用方法：

一、项目build.gradle

```java
    dependencies {
     //EventBus
        implementation 'org.greenrobot:eventbus:3.1.1'
    }
```

二、食用方式

```java
    //类内
    private DownQueue downQueue = new DownQueue();

    //方法内
    try {
        downQueue.add(new DownloadThread(new URL(_url),getFilesDir().getAbsolutePath() + 									File.separator +ruantanzhen_apkName + ".apk"));
        }
    }catch(Exception ignored){
    }finally{
        downQueue.start();
    }

```

三、回调代码

```java
 		@Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DownloadEventMsg msg) {
        String appName = msg.getMsg();
        if (msg.getType() == THREAD_PROGRESS) {
          	//TODO 进度
            int progress = msg.getArg1();
           
        } else if (msg.getType() == THREAD_FINISHED) {
            binding.downloadedTv.setText(msg.getObj().toString());
            //TODO 安装动作
          	
        } else if (msg.getType() == QUEUE_FINISHED) {
            //TODO 下载完所有的第三方apk 拉起牌照方
            
        }
    }
```

四、清空（防内存泄漏）

```java
 		@Override
    protected void onDestroy() {
      super.onDestroy();
      downQueue.destroy();
    }
```

