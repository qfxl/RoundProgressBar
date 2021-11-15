# RoundProgressBar
Android circular countdown control, using Canvas plus ValueAnimator to achieve the timing effect, you can customize the text, text size, text color, outer arc width, color, the direction of the outer arc sweep, etc.

[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu) 

## LICENSE

[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)

## Example

![此处输入图片的描述][1]

## Gradle

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```
dependencies {
    implementation 'com.github.qfxl:RoundProgressBar:1.0.1'
}
```
## code
```java
        mRoundProgressBar.setCenterTextSize(40);
        mRoundProgressBar.setCenterText("清风徐来");
        mRoundProgressBar.setCenterTextColor(Color.BLACK);
        mRoundProgressBar.setCountDownTimeMillis(5000);
        mRoundProgressBar.setPadding(20, 20, 20, 20);
        mRoundProgressBar.setCenterBackground(Color.WHITE);
        mRoundProgressBar.setStartAngle(-90);
        mRoundProgressBar.setStrokeWidth(10);
        mRoundProgressBar.setStrokeColor(Color.BLACK);
        mRoundProgressBar.setAutoStart(true);
        mRoundProgressBar.setShouldDrawOutsideWrapper(true);
        mRoundProgressBar.setOutsideWrapperColor(Color.GRAY);
        mRoundProgressBar.setSupportEts(true);       
        mRoundProgressBar.setDirection(RoundProgressBar.Direction.REVERSE);    
```

## layout
```xml
<com.github.view.RoundProgressBar
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="10dp"
        app:rpb_centerBackgroundColor="#808080"
        app:rpb_centerText="跳过"
        app:rpb_centerTextColor="#fff"
        app:rpb_centerTextSize="14sp"
        app:rpb_countDownTimeInMillis="5000"
        app:rpb_progressDirection="forward"
        app:rpb_sweepStartAngle="-90"
        app:rpb_autoStart="true"
        app:rpb_sweepStrokeColor="@color/colorPrimaryDark"
        app:rpb_sweepStrokeWidth="2dp"
        app:rpb_drawOutsideWrapper="true"
        app:rpb_outsideWrapperColor="@color/colorGray"
        app:rpb_supportEndToStart="true"                        
        />
```

## Attrs

|attr|attr description|type|default|
|:--:|:--:|:--:|:--:|
|rpb_sweepStrokeWidth|外弧线的宽度(The width of the outer arc)|dimension|2dp|
|rpb_sweepStrokeColor|外弧线的颜色(Color of outer arc)|color|Color.BLACK|
|rpb_sweepStartAngle|外弧线的起始扫描角度(The starting sweep angle of the outer arc)|integer|-90|
|rpb_centerText|中间文本(Center text)|string|-|
|rpb_centerTextSize|中间文本的文字大小(Center textSize)|dimension|12sp|
|rpb_centerTextColor|中间文本的文字颜色(Center textCOlor)|color|Color.WHITE|
|rpb_centerBackgroundColor|中间区域的背景色(The background color of the center area)|color|#808080|
|rpb_countDownTimeInMillis|倒计时的时间(Countdown time)|integer|3000(ms)|
|rpb_progressDirection|外弧扫过的方向(The direction the outer arc sweeps)|enum[forward(0),reverse(1)]|forward(0)|
|rpb_autoStart|是否自动开启倒计时(Auto start)|boolean|true|
|rpb_drawOutsideWrapper|是否绘制外弧wrapper(Whether to draw the outer arc wrapper)|boolean|false|
|rpb_outsideWrapperColor|外弧wrapper的颜色(The color of the outer arc wrapper)|color|#E8E8E8|
|rpb_supportEndToStart|是否支持反转(true 绘制的progress=progress-360)|boolean|false|

## version

* v1.0.0 move repository to Jitpack, Jcenter no longer supported.

## others
### progress listener

```java
        RoundProgressBar mRoundProgressBar = (RoundProgressBar) findViewById(R.id.rpb_1);
        mRoundProgressBar.setProgressChangeListener(new RoundProgressBar.ProgressChangeListener() {
            @Override
            public void onFinish() {
               
            }

            @Override
            public void onProgressChanged(int progress) {
             
            }
        });
```

### how to stop
`Stop automatically at the end of the countdown, of course, it can also be forced to stop.`
```java
mRoundProgressBar.stop();
```

### how to pause
```java
mRoundProgressBar.pause();
```

### how to resume 
```java
mRoundProgressBar.resume();
```

### percentage timer

if 'rpb_centerText' is null or empty, then percentage timer will show

  [1]: https://github.com/qfxl/RoundProgressBar/blob/master/gif/demo.gif
