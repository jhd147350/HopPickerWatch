package studio.jhd.hoppickerwatch;

/**
 * Created by jiahaodong on 2016/9/19.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jhd147350 on 16/9/6-16:06
 * 935410469@qq.com
 * https://github.com/jhd147350
 * 原出处 https://knewone.com/things/hop-picker-de-chuang-yi-wan-biao/
 *
 * 第一版 思路想复杂了，弃掉了，可以看HopPickerWatchView2
 */
public class HopPickerWatchView extends View {

    private Paint mPaint = new Paint();
    //外圆画笔
    private Paint outPaint=new Paint();

    //表盘半径
    private float Radius = 300;
    //外表盘半径
    private float OutRadius = 350;
    //边界半径
    private int BoundsRadius=400;
    //部分弧度圆半径  假定那个圆弧 过外表盘点和 表盘二分之一处点 推算得出
    private float arcR= OutRadius*OutRadius/Radius+Radius/4;

    //watchface color
    private int DialColor = 0xFF666666;
    //
    private int Black=0xFF000000;

    public HopPickerWatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HopPickerWatchView(Context context) {
        super(context);
        // mPaint = new Paint();
        System.out.println("-----init paint-----");
    }


    public HopPickerWatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //mPaint=new Paint();
        //初始化paint
        System.out.println("arcR:"+arcR);
        mPaint.setColor(DialColor);
        mPaint.setAntiAlias(true);
        //初始化坐标系,以边界半径为坐标原点
        canvas.translate(BoundsRadius, BoundsRadius);
        //先画一个圆
        canvas.drawCircle(0, 0, Radius, mPaint);
        //画外圆 半圆 路径
        outPaint.setColor(Black);
        outPaint.setAntiAlias(true);
        outPaint.setStyle(Paint.Style.STROKE);
       Path outC = new Path();

       // outC.addArc(new RectF(-OutRadius,-OutRadius,OutRadius,OutRadius),0f,180f);
      //  RectF halfC= new RectF(-OutRadius,-OutRadius,OutRadius,OutRadius);
      //  outC.arcTo(halfC,0f,180f);
        RectF temp=new RectF(-arcR,(-Radius/2),arcR,arcR*2-Radius/2);
      //  Path a=new Path();
      //  a.addArc(temp,180f,360f);
       // outC.arcTo(temp,180f,180f);
       // outC.close();
      //  canvas.drawPath(outC,outPaint);
      //  canvas.drawRect(temp,outPaint);
      //  canvas.drawPath(a,outPaint);
        //画不规则半弧

        //region 合并半圆和圆弧
        Region region=new Region();
        region.setPath(outC,new Region(new Rect((int)-OutRadius,(int)-OutRadius,(int)OutRadius,(int)OutRadius)));
       // region.set(new Rect((int)-OutRadius,(int)-OutRadius,(int)OutRadius,(int)OutRadius));
        region.op(new Rect((int)-arcR,(int)(-Radius/2),(int)arcR,(int)(arcR*2-Radius/2)), Region.Op.INTERSECT);

        RegionIterator iterator=new RegionIterator(region);
     //  Rect r=new Rect();
     //   while (iterator.next(r)){
    //       canvas.drawRect(r,outPaint);
      //  }

        // canvas.drawArc();

        Path path1 = new Path();
        path1.addCircle(150, 150, 100, Path.Direction.CW);
        Path path2 = new Path();
        path2.addCircle(200, 200, 90, Path.Direction.CW);
        path1.op(path2, Path.Op.INTERSECT);
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);
        paint1.setColor(0xFFFF0000);
        canvas.drawPath(path1, paint1);


        //另一种思路 绘制不规则闭合曲线
       // Path test0=new Path();
        Path halfCPath=new Path();
        //test11.addCircle(0f,0f,OutRadius, Path.Direction.CW);
        halfCPath.addCircle(0f,0f,OutRadius, Path.Direction.CW);
        Path arcPath=new Path();
        //test11.addArc(halfC,0f,180f);
        //-1有瑕疵，不-1绘制不出来
        arcPath.addCircle(0f,arcR-Radius/2,arcR-1, Path.Direction.CW);
        halfCPath.op(arcPath, Path.Op.INTERSECT);
        canvas.drawPath(halfCPath,outPaint);
       /*
        //***************************************

        //test22的半径在438和463之间 是绘制不出来交集的效果，其他值都可以成功绘制交集效果
        Path test22=new Path();
      //  test22.addCircle(0f,arcR-Radius/2,arcR, Path.Direction.CW);
       // test22.addCircle(0f,arcR-Radius/2,437, Path.Direction.CW);
        //test22.close();
        test22.addArc(temp,180f,180f);
       // test.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        //android 4.4 才能使用 op方法
       test11.op(test22 ,Path.Op.INTERSECT);
        //test11.close();
          canvas.drawPath(test11,paint1);
        canvas.drawLine(-500f,-Radius/2,500f,-Radius/2,paint1);
        canvas.drawPath(test22,paint1);*/


        ///----绘制刻度





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
            width = BoundsRadius * 3;

        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = BoundsRadius * 3;
        }
        //  MeasureSpec
        setMeasuredDimension(width, height);
    }
}

