package studio.jhd.hoppickerwatch;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.TextView;

/**
 * Created by jiahaodong on 2016/9/25-21:23.
 * 935410469@qq.com
 * https://github.com/jhd147350
 */

public class MyWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        int id=appWidgetIds[0];
        TextView tv;

    }
}
