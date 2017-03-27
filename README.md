# speedLib
网络测速 包含 网络延时,上下行速度,基于OkHttp3.0

使用方式 :
```
SpeedManager speedManager = new SpeedManager.Builder()
                .setSpeedUrl("url")
                .setSpeedCount(6)
                .setNetDelayListener(new SpeedListener() {
                    @Override
                    public void speeding(double speed) {

                    }

                    @Override
                    public void finishSpeed(double finalSpeed) {

                    }
                })
                .setDownloadListener(new SpeedListener() {
                    @Override
                    public void speeding(double speed) {
                        Toast.makeText(MainActivity.this,"test : " + speed ,Toast.LENGTH_LONG);
                    }

                    @Override
                    public void finishSpeed(double finalSpeed) {

                    }
                })
                .setUpLoadListener(new SpeedListener() {
                    @Override
                    public void speeding(double speed) {

                    }

                    @Override
                    public void finishSpeed(double finalSpeed) {

                    }
                })
                .builder();
        speedManager.startSpeed();
```
