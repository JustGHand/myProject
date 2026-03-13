package com.pw.codeset.utils
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


object GlanceUtils {

    // 定义一个全局协程作用域，用于处理小组件更新任务
    private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /**
     * 通用更新方法：刷新指定类型的小组件
     * @param context 上下文
     * @param widgetClass 小组件的类名，例如 ScheduleGlanceWidget::class.java
     */
    @JvmStatic
    fun <T : GlanceAppWidget> updateWidget(context: Context, widgetClass: Class<T>) {
        LogToastUtils.printLog("GlanceUtils.updateWidget")
        widgetScope.launch {
            delay(300)
            try {
                val manager = GlanceAppWidgetManager(context)
                // 1. 获取该类小组件在桌面的所有 ID
                val glanceIds = manager.getGlanceIds(widgetClass)
                if (glanceIds.isNotEmpty()) {

                    // 2. 实例化一个小组件对象来调用它的 update 方法
                    // 注意：Glance 组件类通常必须有一个无参构造函数
                    val widgetInstance = widgetClass.getDeclaredConstructor().newInstance()

                    // 3. 循环刷新每一个 ID
                    glanceIds.forEach { id ->
                        // 关键：在更新前，手动改一下 Glance 的内部 DataStore
                        // 这样系统会百分之百认为这个小组件“变了”，从而强制重绘
                        updateAppWidgetState(context, id) { prefs ->
                            prefs[longPreferencesKey("force_update_tick")] = System.currentTimeMillis()
                        }
                        LogToastUtils.printLog("widgetInstance.update")
                        widgetInstance.update(context, id)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 通用刷新小组件方法
     *
     * @param context       上下文
     * @param providerClass 小组件 Provider 的类名 (例如 ScheduleAppWidgetProvider.class)
     * @param listViewId    小组件中 ListView/StackView 的 ID (如果不含列表，传 0 或 -1)
     */
    fun updateAllWidgets(context: Context?, providerClass: Class<*>, listViewId: Int) {
        if (context == null) return

        try {
            val mgr = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, providerClass)
            val ids = mgr.getAppWidgetIds(cn)

            if (ids == null || ids.size == 0) {
                Log.d("WidgetUtils", "没有在桌面上找到该组件实例")
                return
            }

            // 1. 如果有列表控件，通知列表数据失效，强制触发 RemoteViewsFactory 的 onDataSetChanged
            if (listViewId > 0) {
                mgr.notifyAppWidgetViewDataChanged(ids, listViewId)
            }

            // 2. 发送标准的 ACTION_APPWIDGET_UPDATE 广播
            // 这会触发 Provider 的 onUpdate 方法，用于刷新按钮点击监听和普通 TextView
            val intent = Intent(context, providerClass)
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)

            Log.d("WidgetUtils", "已发送刷新指令给: " + providerClass.getSimpleName())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}