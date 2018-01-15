package com.example.liu.walk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast

/**
 * Created by liu on 18-1-14.
 */
class WalkBroadcaster : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //接收注册的事件
        if (intent == null) return
        var typeEvent = intent.getStringExtra("msg")
        var walkTime = intent.getLongExtra("walk", 60 * 1000L)
        var runningTime = intent.getLongExtra("running", 2 * 60 * 1000L)
        var reCount = intent.getIntExtra("re_count", 7)
        var nowCount = intent.getIntExtra("now_count", 1)
        var walkEvent = WalkRunEvent(typeEvent,nowCount)
        EventBusUtils.post(walkEvent)
        if (nowCount > reCount) {
            Toast.makeText(context, typeEvent + "完成", Toast.LENGTH_SHORT).show()
            return
        }

        //running
        startMusic(context)

        //设置跑步闹铃
        var runningIntent = Intent(AmMangerClient.ACTION_RUN_WALK)

        runningIntent.putExtra("walk", walkTime)
                .putExtra("msg", typeEvent)
                .putExtra("running", runningTime)
                .putExtra("re_count", reCount)
                .putExtra("now_count", nowCount + 1)

//        AmMangerClient.create(context!!)
//                .setAndSendTask(AmMangerClient.REQ_RUNNING, runningIntent, walkTime + runningTime)

        when (typeEvent) {
            AmMangerClient.TYPE_RUNNING -> {
                //running
                AmMangerClient.create(context!!)
                        .setAndSendTask(AmMangerClient.REQ_RUNNING, runningIntent, walkTime + runningTime)

            }
            AmMangerClient.TYPE_WALK -> {
                //走路

                AmMangerClient.create(context!!)
                        .setAndSendTask(AmMangerClient.REQ_WALK, runningIntent, walkTime + runningTime)
            }
        }

    }

    private fun startMusic(context: Context?) {
        var mMediaPlayer = MediaPlayer.create(context, R.raw.ba_yin_he)
        mMediaPlayer.setOnCompletionListener {
            Toast.makeText(context, "音乐播放完了", Toast.LENGTH_SHORT).show()
            it.release()
        }
        mMediaPlayer.start()
        mMediaPlayer.setVolume(0.8f, 0.8f)

    }
}