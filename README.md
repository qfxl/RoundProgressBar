# RoundProgressBar
Android圆形倒计时控件，采用Canvas绘制外加ValueAnimator实现计时效果,可以自定义文字，文字大小，文字颜色，外弧的宽度，颜色，外弧扫过的方向等

## 效果图

![此处输入图片的描述][1]

## Gradle
## code
```java
  RoundProgressBar roundProgressBar = new RoundProgressBar(this);
        roundProgressBar.setCenterTextSize(40);
        roundProgressBar.setCenterText("清风徐来");
        roundProgressBar.setCenterTextColor(Color.BLUE);
        roundProgressBar.setCountDownTimeMillis(5000);
        roundProgressBar.setPadding(10,10,10,10);
        roundProgressBar.setDirection(RoundProgressBar.Direction.REVERSE);
        roundProgressBar.setCenterBackground(Color.WHITE);
        roundProgressBar.setStartAngle(-90);
        roundProgressBar.setStrokeWidth(10);
        roundProgressBar.setAutoStart(true);
```

## layout
```xml
<com.qfxl.view.RoundProgressBar
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="10dp"
        app:center_background="#808080"
        app:center_text="跳过"
        app:center_text_color="#fff"
        app:center_text_size="14sp"
        app:count_down_time_millis="5000"
        app:progress_direction="forward"
        app:sweep_start_angle="-90"
        app:auto_start="true"
        app:sweep_stroke_color="@color/colorPrimaryDark"
        app:sweep_stroke_width="2dp" />
```

## 自定义属性

|属性|属性说明|类型|默认值|
|:--:|:--:|:--:|:--:|
|sweep_stroke_width|外弧线的宽度|dimension|5|
|sweep_stroke_color|外弧线的颜色|color|Color.BLACK|
|sweep_start_angle|外弧线的起始扫描角度|integer|-90|
|center_text|中间文本|string|-|
|center_text_size|中间文本的文字大小|dimension|12sp|
|center_text_color|中间文本的文字颜色|color|Color.BLACK|
|center_background|中间区域的背景色|color|Color.GRAY|
|count_down_time_millis|倒计时的时间|integer|3000(ms)|
|progress_direction|外弧扫过的方向|enum[forward(0),reverse(1)]|forward(0)|
|auto_start|是否自动开启倒计时|boolean|true|

## 其他
### 如何监听

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
### 如何手动开启计时
```java
mRoundProgressBar.start();
```
### 如何停止
`倒计时结束自动停止，当然也可以强制停止`
```java
mRoundProgressBar.stop();
```

### 如何设置为百分比计时

只需要center_text为空，则默认开启百分比计时效果


  [1]: https://github.com/qfxl/RoundProgressBar/blob/master/gif/demo.gif
