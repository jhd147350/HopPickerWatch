package studio.jhd.hoppickerwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jiahaodong on 2016/9/19.
 */
public class TestView extends View {
    Paint paint;
    public TestView(Context context) {
        super(context);
        paint = new Paint(); //设置一个笔刷大小是3的黄色的画笔
        paint.setColor(Color.YELLOW);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(); //设置一个笔刷大小是3的黄色的画笔
        paint.setColor(Color.YELLOW);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
    }



    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.translate(canvas.getWidth()/2, canvas.getHeight()/2); //将位置移动画纸的坐标点:150,150
        canvas.drawCircle(0, 0, canvas.getWidth()/4, paint); //画圆圈 r 100
        //   canvas.draw;

        //使用path绘制路径文字
        canvas.save();
        canvas.translate(-(canvas.getWidth()/4), -(canvas.getWidth()/4));
        Path path = new Path();
        path.addArc(new RectF(0,0,canvas.getWidth()/2,canvas.getWidth()/2), 230, 80);
        Paint citePaint = new Paint(paint);
        citePaint.setTextSize(30);
        citePaint.setStrokeWidth(2);
        Paint black=new Paint();
        black.setAntiAlias(true);
        black.setStyle(Paint.Style.STROKE);
        black.setColor(Color.BLACK);
        canvas.drawPath(path,black);
        canvas.drawTextOnPath("http://www.android777.com", path, 0, 30, citePaint);
        canvas.restore();

        Paint tmpPaint = new Paint(paint); //小刻度画笔对象
        tmpPaint.setStrokeWidth(2);
        tmpPaint.setTextSize(40);

        float  y=canvas.getWidth()/4;
        int count = 60; //总刻度数

        for(int i=0 ; i <count ; i++){
            if(i%5 == 0){//大刻度
                canvas.drawLine(0f, y, 0, y+20f, paint);
                canvas.drawText(String.valueOf(i/5+1), 0f, y+65f, tmpPaint);

            }else{//小刻度
                canvas.drawLine(0f, y, 0f, y +15f, tmpPaint);
            }
            canvas.rotate(360/count,0f,0f); //旋转画纸
        }

        //绘制指针
        tmpPaint.setColor(Color.GRAY);
        tmpPaint.setStrokeWidth(4);
        canvas.drawCircle(0, 0, 7, tmpPaint);
        tmpPaint.setStyle(Paint.Style.FILL);
        tmpPaint.setColor(Color.YELLOW);
        canvas.drawCircle(0, 0, 5, tmpPaint);
        canvas.drawLine(0, 10, 0, -65, paint);
    }
}
