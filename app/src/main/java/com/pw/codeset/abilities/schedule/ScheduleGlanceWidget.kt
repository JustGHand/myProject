package com.pw.codeset.abilities.schedule

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.pw.codeset.R
import com.pw.codeset.databean.ScheduleBean
import com.pw.codeset.manager.ScheduleManager


class ScheduleAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent)
        val mgr = AppWidgetManager.getInstance(context)
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)

        if (ACTION_REFRESH == intent.getAction()) {
            // 刷新列表数据
            mgr.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_schedules)
            Log.d("PW_CODE", "XML刷新点击")
        } else if (ACTION_COMPLETE == intent.getAction()) {
            val scheduleId = intent.getStringExtra(EXTRA_ITEM_ID)
            val manager = ScheduleManager.getInstance()
            val bean = manager.getSchedule(scheduleId)
            if (bean != null) {
                manager.completeSchedule(bean)
                Log.d("PW_CODE", "XML完成点击: " + scheduleId)
                // 完成后刷新
                mgr.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_schedules)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH: String = "com.pw.action.REFRESH"
        const val ACTION_COMPLETE: String = "com.pw.action.COMPLETE"
        const val EXTRA_ITEM_ID: String = "extra_item_id"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.getPackageName(), R.layout.widget_schedule)

            // 绑定 ListView 的 Service
            val serviceIntent: Intent = Intent(context, ScheduleWidgetService::class.java)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // 这一步很重要，强制刷新的关键
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)))
            views.setRemoteAdapter(R.id.lv_schedules, serviceIntent)

            // 设置刷新按钮点击
            val refreshIntent = Intent(context, ScheduleAppWidgetProvider::class.java)
            refreshIntent.setAction(ACTION_REFRESH)
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val refreshPi = PendingIntent.getBroadcast(
                context, appWidgetId, refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_refresh, refreshPi)

            // 设置列表项点击的模板 (PendingIntent Template)
            val clickIntent = Intent(context, ScheduleAppWidgetProvider::class.java)
            clickIntent.setAction(ACTION_COMPLETE)
            val clickPi = PendingIntent.getBroadcast(
                context, 0, clickIntent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setPendingIntentTemplate(R.id.lv_schedules, clickPi)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

class ScheduleWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ScheduleRemoteViewsFactory(this.getApplicationContext())
    }
}

internal class ScheduleRemoteViewsFactory(private val mContext: Context) : RemoteViewsFactory {
    private val mList: MutableList<ScheduleBean> = ArrayList<ScheduleBean>()

    override fun onDataSetChanged() {
        // 当调用 notifyAppWidgetViewDataChanged 时，此方法在子线程运行
        // 可以在这里安全地读磁盘文件
        ScheduleManager.getInstance().init()
        val today = ScheduleManager.getInstance().getTodayScheduleList()
        mList.clear()
        if (today != null) {
            for (bean in today) {
                if (bean.getStatus() == 0) { // 假设 0 是 UNDONE
                    mList.add(bean)
                }
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position >= mList.size) return null

        val bean = mList.get(position)
        val views = RemoteViews(mContext.getPackageName(), R.layout.widget_item_schedule)
        views.setTextViewText(R.id.tv_desc, bean.getDesc())

        // 设置点击填充 Intent (Fill-in Intent)
        val fillInIntent = Intent()
        fillInIntent.putExtra(ScheduleAppWidgetProvider.EXTRA_ITEM_ID, bean.getId())
        views.setOnClickFillInIntent(R.id.btn_complete, fillInIntent)

        return views
    }

    override fun onCreate() {}
    override fun onDestroy() {
        mList.clear()
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}