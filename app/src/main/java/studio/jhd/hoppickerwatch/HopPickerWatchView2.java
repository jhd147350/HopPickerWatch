package studio.jhd.hoppickerwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;
import android.widget.ViewFlipper;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by jhd147350 on 16/9/21-0:54
 * 935410469@qq.com
 * https://github.com/jhd147350
 * 原出处 https://knewone.com/things/hop-picker-de-chuang-yi-wan-biao/
 * <p>
 * 第二版 换个简单思路
 */
@RemoteView//这个标记没用，要想实现appwidget还需修改框架层
public class HopPickerWatchView2 extends View {
    //控件实际的宽高，在onmeasure种获取
    private int viewWidth = 0;
    private int viewHeight = 0;
    private Paint mPaint = new Paint();
    //外圆画笔
    private Paint outPaint = new Paint();
    //刻度数,每一个刻度表示10min
    private int Scale = 72;
    //每一个刻度所占的角度
    private float degree = 360f / Scale;
    //旋转角度
    private float RotateDegree = 0;
    //每分钟度数 转一圈是12小时
    // 360 / (12*60) = 0.5
    private float perMinDegree = 0.5f;
    //以下7个属性的实际值还要在onmeasure重新测量
    //表盘半径
    private float r = 300;
    //外圆半径
    private float R = 350;
    //刻度圆半径  假定那个圆弧 过外圆半圆处点和表盘直径3/4处点 推算得出
    private float arcR = R * R / r + r / 4;
    //一小时间隔刻度线的长度
    private float hourLen = 60;
    //半小时间隔刻度线的长度
    private float halfHourLen = 45;
    //10min间隔刻度线的长度
    private float tenMinLen = 15;
    //刻度数字位置，在一小时刻度的下方
    private float numPosition = hourLen + 40;

    //watchface color
    private int DialColor = 0xFFE5E6EB;
    //black
    private int Black = 0xFF000000;
    //刻度圆路径
    private Path arcPath;//= new Path();
    //要扣的表盘
    private Path watchface;//= new Path();
    //刻度数字
    private String nums[] = {"12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    // 抠图抗锯齿
    private PaintFlagsDrawFilter pfdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private void initMy() {//初始化画笔
        mPaint.setAntiAlias(true);
        mPaint.setColor(DialColor);
        outPaint.setAntiAlias(true);
        outPaint.setStyle(Paint.Style.STROKE);
    }

    private void initOnMeasure() {//跟屏幕尺寸相关的初始化操作放到这个函数里
        //初始化一些基本尺寸
        //  BoundsRadius = Math.min(viewWidth, viewHeight) / 2;
        r = Math.min(viewWidth, viewHeight) / 2;
        R = Math.min(viewWidth, viewHeight) / 2 + 50;
        arcR = R * R / r + r / 4;
        //初始化和尺寸相关的其他参数
        // TODO: 2016/10/1  画笔宽度和刻度长度最好由dp换算，这样不同dpi的手机看到的指针和表盘线条粗细是一样的
        outPaint.setStrokeWidth(5);
        outPaint.setTextSize(50);
        hourLen = 60;
        halfHourLen = 45;
        tenMinLen = 15;
        numPosition = hourLen +40;

        //onMeasure方法会被调用好几次，所以path.add方法 会 一直放入好几个 path
        //每次 直接new一个新的在add，就不会出现绘图的时候出现很多半径不同的圆了
        arcPath = new Path();
        arcPath.addCircle(0f, arcR - r / 2, arcR, Path.Direction.CW);
        watchface = new Path();
        watchface.addCircle(0f, 0f, r, Path.Direction.CW);
    }

    public HopPickerWatchView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMy();
    }

    public HopPickerWatchView2(Context context) {
        super(context);
        initMy();
    }

    public HopPickerWatchView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMy();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化坐标系,以view中心点为坐标原点，方便后续计算
        canvas.translate(viewWidth / 2, viewHeight / 2);
        //先画表盘
        canvas.drawCircle(0, 0, r, mPaint);
        //设置抗锯齿，不设置，扣出来的圆有明显锯齿
        canvas.setDrawFilter(pfdf);
        //抠圆
        canvas.clipPath(watchface);
        //开始旋转，每次旋转的角度由时间决定
        setRotateDegree();
        canvas.rotate(RotateDegree);
        //画刻度圆的画笔设置成黑色
        outPaint.setColor(Black);
        //绘制刻度圆
        canvas.drawPath(arcPath, outPaint);
        //绘制刻度
        canvas.save();
        //将坐标原点移动到刻度圆的圆心处
        canvas.translate(0, arcR - r / 2);
        //反向旋转刻度圆，使其一直保持相对水平
        canvas.rotate(-RotateDegree);
        for (int i = 0; i < Scale; i++) {
            if (i % 6 == 0) {//带数字的大刻度
                canvas.drawLine(0, arcR, 0, arcR - hourLen, outPaint);
                //开始绘制数字
                canvas.save();
                //位移画布，是数字绘制在刻度圆内，与刻度的距离可自行定义
                canvas.translate(0, numPosition - arcR);
                //纠正 数字旋转的角度 使表盘上的数字一直是正对我们的
                canvas.rotate(-i / 6 * 30);
                //绘制数字，并保证数字正对刻度线
                canvas.drawText(nums[i / 6], -outPaint.measureText(nums[i / 6]) / 2, 0, outPaint);
                canvas.restore();
            } else if (i % 6 == 3) {//间隔半小时的中刻度
                canvas.drawLine(0, arcR, 0, arcR - halfHourLen, outPaint);
            } else {//间隔10min的小刻度
                canvas.drawLine(0, arcR, 0, arcR - tenMinLen, outPaint);
            }
            //旋转 加偏移量，绘制下一条刻度线
            canvas.rotate(degree);
        }
        canvas.restore();

        //最后绘制指针 #F87219橘黄色
        outPaint.setColor(0xFFF87219);
        canvas.drawLine(0, -r, 0, r, outPaint);
        outPaint.setColor(Black);//恢复黑色
        postInvalidateDelayed(1000);//每秒刷新一次
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      //    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        initOnMeasure();

        setMeasuredDimension(viewWidth, viewHeight);
    }

    //根据当前时间设置画布旋转角度
    private void setRotateDegree() {
        Calendar calendar = Calendar.getInstance();
        int Hour = calendar.get(Calendar.HOUR);
        int Min = calendar.get(Calendar.MINUTE);
        RotateDegree = (Hour * 60 + Min) * perMinDegree;
    }
}
