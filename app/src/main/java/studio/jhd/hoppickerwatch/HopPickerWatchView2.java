package studio.jhd.hoppickerwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

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
public class HopPickerWatchView2 extends BasicWatchView {

    private Paint mPaint = new Paint();
    //外圆画笔
    private Paint outPaint = new Paint();
    //刻度数
    private int Scale = 72;

    //旋转角度
    private float RotateDegree = 0;

    //每分钟度数 转一圈是12小时
    // 360 / (12*60) = 0.5
    private float perMinDegree = 0.5f;

    //表盘半径
    private float Radius = 300;
    //外表盘半径
    private float OutRadius = 350;
    //边界半径
    private int BoundsRadius = 300;
    //部分弧度圆半径  假定那个圆弧 过外表盘点和 表盘二分之一处点 推算得出
    private float arcR = OutRadius * OutRadius / Radius + Radius / 4;

    //watchface color
    private int DialColor = 0xFF666666;
    //
    private int Black = 0xFF000000;

    public HopPickerWatchView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HopPickerWatchView2(Context context) {
        super(context);
        // mPaint = new Paint();
        System.out.println("-----init paint-----");
    }


    public HopPickerWatchView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //mPaint=new Paint();
        //初始化paint
        System.out.println("arcR:" + arcR);
        mPaint.setColor(DialColor);
        mPaint.setAntiAlias(true);
        //初始化坐标系,以边界半径为坐标原点
        canvas.translate(BoundsRadius, BoundsRadius);
        //抠圆1，抠出的圆 有锯齿
      /*  Path watchface = new Path();
        watchface.addCircle(0f, 0f, Radius+19, Path.Direction.CW);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvas.clipPath(watchface);*/

        //先画一个圆
        canvas.drawCircle(0, 0, Radius, mPaint);

        //抠圆2
        Path watchface2 = new Path();
        watchface2.addCircle(0f, 0f, Radius+15, Path.Direction.CW);
        Paint clipPaint=new Paint();
        clipPaint.setColor(0xFFFFFFFF);
        clipPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawPath(watchface2,mPaint);
        mPaint.setXfermode(null);


        //开始旋转，每次旋转的角度由时间决定，现在先模拟旋转
        canvas.rotate(RotateDegree);
        Log.e("jhd","*-----------------"+RotateDegree);
        setRotateDegree();

        //画外圆的画笔
        outPaint.setColor(Black);
        outPaint.setAntiAlias(true);
        outPaint.setStyle(Paint.Style.STROKE);
        //画指针
        canvas.drawLine(0, -Radius, 0, Radius, outPaint);
        Path outC = new Path();

        RectF temp = new RectF(-arcR, (-Radius / 2), arcR, arcR * 2 - Radius / 2);


        //另一种思路 绘制不规则闭合曲线
        Path arcPath = new Path();
        arcPath.addCircle(0f, arcR - Radius / 2, arcR, Path.Direction.CW);
        canvas.drawPath(arcPath, outPaint);


        //绘制刻度
        canvas.save();
        canvas.translate(0, arcR - Radius / 2);
        outPaint.setStrokeWidth(5);
        outPaint.setTextSize(50);
        //刻度之间相隔的角度
        float degree = 360f / Scale;

        canvas.rotate(-RotateDegree);
        int position =3;
        for (int i = 0; i < Scale; i++) {
            if (i % 6 == 0) {//大刻度
                canvas.drawLine(0, arcR, 0, arcR - 60, outPaint);
                //画数字
                canvas.save();
                canvas.translate(0,120-arcR);
                String num=""+i/6;
                if(num.equals("0")){//将0换成12
                    num="12";
                }
                //纠正 数字旋转的角度 使表盘上的数字一直是正对我们的
                canvas.rotate(-i/6*30);
                canvas.drawText(num,-outPaint.measureText(num)/2,0,outPaint);
                canvas.restore();
            } else if(i%6==3){//中刻度
                canvas.drawLine(0, arcR, 0, arcR - 45, outPaint);

            }else{//小刻度
                canvas.drawLine(0, arcR, 0, arcR - 15, outPaint);
            }


            //旋转 加偏移量
            canvas.rotate(degree);
        }
        canvas.restore();
       // canvas.drawTextRun();



        postInvalidateDelayed(16);




    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;

        } else {
            width = BoundsRadius * 2;

        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = BoundsRadius * 2;
        }
        //  MeasureSpec
        setMeasuredDimension(width, height);
    }

    //启动钟表
  /*  private void startClock(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    int hour = calendar.get(Calendar.HOUR)+1;
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    setHour(hour);
                    setMinute(minute);
                    setSecond(second);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }*/
    //根据当前时间设置画布旋转角度
    private void setRotateDegree(){
      /*  if(RotateDegree>=360){
            RotateDegree=0;
        }*/
       Calendar calendar=Calendar.getInstance();
        int Hour=calendar.get(Calendar.HOUR);
        int Min=calendar.get(Calendar.MINUTE);
        Log.e("jhd",calendar.get(Calendar.HOUR)+"");
        Log.e("jhd",calendar.get(Calendar.MINUTE)+"");
        RotateDegree=(Hour*60+Min)*perMinDegree;
      // RotateDegree+=0.2;
    }
}
