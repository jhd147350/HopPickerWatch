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
@RemoteView
public class HopPickerWatchView2 extends View {
    ViewFlipper v;

    //控件实际的宽高，在onmeasure种获取
    private int viewWidth = 0;
    private int viewHeight = 0;

    private Paint mPaint = new Paint();
    //外圆画笔
    private Paint outPaint = new Paint();
    //刻度数
    private int Scale = 72;
    //每一个刻度所占的角度
    private float degree = 360f / Scale;

    //旋转角度
    private float RotateDegree = 0;

    //每分钟度数 转一圈是12小时
    // 360 / (12*60) = 0.5
    private float perMinDegree = 0.5f;

    //以下4个属性的实际值还要在onmeasure种测量
    //表盘半径
    private float Radius = 300;
    //外表盘半径
    private float OutRadius = 350;
    //边界半径
    private int BoundsRadius = 500;
    //部分弧度圆半径  假定那个圆弧 过外表盘点和 表盘二分之一处点 推算得出
    private float arcR = OutRadius * OutRadius / Radius + Radius / 4;

    //watchface color
    private int DialColor = 0xFF666666;
    //
    private int Black = 0xFF000000;

    //大圆
    private Path arcPath;//= new Path();
    //要扣的表盘
    private Path watchface;//= new Path();

    //刻度上的数字
    // private String num="";
    private String nums[] = {"12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};

    // 抠图抗锯齿
    private PaintFlagsDrawFilter pfdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private void initMy() {//初始化一些东西
        mPaint.setAntiAlias(true);
        mPaint.setColor(DialColor);
        outPaint.setAntiAlias(true);
        outPaint.setStyle(Paint.Style.STROKE);
        //initOnMeasure();
    }

    private void initOnMeasure() {//跟屏幕尺寸相关的初始化操作放到这个函数里
        //初始化一些基本尺寸
        BoundsRadius = Math.min(viewWidth, viewHeight) / 2;
        Radius = Math.min(viewWidth, viewHeight) / 2;
        OutRadius = Math.min(viewWidth, viewHeight) / 2 + 50;
        arcR = OutRadius * OutRadius / Radius + Radius / 4;

        //初始化和尺寸相关的其他参数
        outPaint.setStrokeWidth(5);
        outPaint.setTextSize(50);
        //onMeasure方法会被调用好几次，所以path.add方法 会 一直放入好几个 path
        //每次 直接new一个新的
        arcPath = new Path();
        arcPath.addCircle(0f, arcR - Radius / 2, arcR, Path.Direction.CW);
        watchface = new Path();
        watchface.addCircle(0f, 0f, Radius, Path.Direction.CW);
    }

    public HopPickerWatchView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMy();
    }

    public HopPickerWatchView2(Context context) {
        super(context);
        initMy();
        // mPaint = new Paint();
        System.out.println("-----init paint-----");
    }


    public HopPickerWatchView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMy();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化坐标系,以边界半径为坐标原点
        canvas.translate(viewWidth / 2, viewHeight / 2);
        //先画一个圆
        canvas.drawCircle(0, 0, Radius, mPaint);

        //抠圆1，抠出的圆 有锯齿

        //设置抗锯齿
        canvas.setDrawFilter(pfdf);
        canvas.clipPath(watchface);

        //开始旋转，每次旋转的角度由时间决定，现在先模拟旋转
        canvas.rotate(RotateDegree);
        setRotateDegree();
        //画外圆的画笔
        outPaint.setColor(Black);


        //另一种思路 绘制不规则闭合曲线
        canvas.drawPath(arcPath, outPaint);

        //绘制刻度
        canvas.save();
        canvas.translate(0, arcR - Radius / 2);

        //刻度之间相隔的角度
        canvas.rotate(-RotateDegree);
        for (int i = 0; i < Scale; i++) {
            if (i % 6 == 0) {//大刻度
                canvas.drawLine(0, arcR, 0, arcR - 60, outPaint);
                //画数字
                canvas.save();
                canvas.translate(0, 120 - arcR);
              /*  num="*"+i/6;
                if(num.equals("0")){//将0换成12
                    num="12";
                }*/
                //纠正 数字旋转的角度 使表盘上的数字一直是正对我们的
                canvas.rotate(-i / 6 * 30);
                canvas.drawText(nums[i / 6], -outPaint.measureText(nums[i / 6]) / 2, 0, outPaint);
                canvas.restore();
            } else if (i % 6 == 3) {//中刻度
                canvas.drawLine(0, arcR, 0, arcR - 45, outPaint);
            } else {//小刻度
                canvas.drawLine(0, arcR, 0, arcR - 15, outPaint);
            }
            //旋转 加偏移量
            canvas.rotate(degree);
        }
        canvas.restore();

        //画指针 #F87219
        outPaint.setColor(0xFFF87219);
        canvas.drawLine(0, -Radius, 0, Radius, outPaint);
        outPaint.setColor(0xFF000000);

        //postInvalidateDelayed(16);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = viewWidth;
        } else {
            //  width = BoundsRadius * 2;
            width = viewWidth;

        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = viewHeight;
        } else {
            // height = BoundsRadius * 2;
            height = viewHeight;
        }
        //  MeasureSpec

        initOnMeasure();

        setMeasuredDimension(width, height);
    }


    //根据当前时间设置画布旋转角度
    private void setRotateDegree() {
        if (RotateDegree >= 360) {
            RotateDegree = 0;
        }

        RotateDegree += 0.2;


       /* Calendar calendar=Calendar.getInstance();
        int Hour=calendar.get(Calendar.HOUR);
        int Min=calendar.get(Calendar.MINUTE);
        Log.e("jhd",calendar.get(Calendar.HOUR)+"");
        Log.e("jhd",calendar.get(Calendar.MINUTE)+"");
        RotateDegree=(Hour*60+Min)*perMinDegree;*/
    }
}
