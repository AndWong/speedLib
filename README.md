# speedLib
网络测速 包含 网络延时,上下行速度,基于OkHttp3.0
```
使用方式 :
(1)设置网络延时的监听 : setNetDelayListener
(2)设置下载速度的监听 : setSpeedListener (由于某种原因,上传速度取下载速度的1/4)
(3)设置网络延时ping的ip : setPindCmd
(4)设置测速地址 : setSpeedUrl
(5)设置测速的回调次数 : setSpeedCount
(6)设置测速超时时间 : setSpeedTimeOut
```

```
        SpeedManager speedManager = new SpeedManager.Builder()
                .setPindCmd("your ip")
                .setSpeedUrl("your url")
                .setSpeedCount(6)
                .setSpeedTimeOut(10 * 1000)
                .setNetDelayListener(new NetDelayListener() {
                    @Override
                    public void result(String delay) {

                    }
                })
                .setSpeedListener(new SpeedListener() {
                    @Override
                    public void speeding(long downSpeed, long upSpeed) {

                    }

                    @Override
                    public void finishSpeed(long finalDownSpeed, long finalUpSpeed) {

                    }
                })
                .builder();
        speedManager.startSpeed();

```
