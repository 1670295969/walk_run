package com.example.liu.walk

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Created by liu on 18-1-14.
 */
class AmMangerClient(var context: Context) {

    companion object {
        val ACTION_RUN_WALK = "liu.walk"
        val TYPE_RUNNING = "running"
        val TYPE_WALK = "walk"
        val REQ_RUNNING = 101
        var REQ_WALK = 102

        fun create(context: Context): AmMangerClient {
            return AmMangerClient(context)
        }
    }

    private lateinit var am: AlarmManager

    init {
        am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun getAmPendingIntent(typeId: Int, intent: Intent): PendingIntent {
        intent.action = ACTION_RUN_WALK
        return PendingIntent.getBroadcast(context, typeId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun setAndSendTask(reqCode: Int, intent: Intent, timeMills: Long) {
        var pi = getAmPendingIntent(reqCode, intent)
        if (Build.VERSION.SDK_INT < 19) {
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeMills, pi)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeMills, pi)
        }
    }

    fun cancelAlarm() {
        // 取消AlarmManager的定时服务
        val intent = Intent(ACTION_RUN_WALK)// 和设定闹钟时的action要一样

        // 这里PendingIntent的requestCode、intent和flag要和设定闹钟时一样
        val piRun = PendingIntent.getBroadcast(context, REQ_RUNNING, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        am.cancel(piRun)
        val piWalk = PendingIntent.getBroadcast(context, REQ_WALK, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        am.cancel(piWalk)
    }

}