package com.example.liu.walk

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.view.View
import android.content.Context.VIBRATOR_SERVICE
import android.media.MediaPlayer
import android.os.Build
import android.os.Vibrator
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {


    private var walkMin = 1
    private var runningMin = 2
    private var reCount = 7
    private lateinit var tvWalkStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBusUtils.register(this)
        setContentView(R.layout.activity_main)
        tvWalkStatus = findViewById(R.id.tv_status)
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtils.unregister(this)
    }

    private fun initListener() {


        RxTextView.afterTextChangeEvents(findViewById(R.id.et_walk))
                .subscribe {
                    walkMin = try {
                        var time = it.editable().toString()
                        time.toInt()
                    } catch (e: Exception) {
                        0
                    }
                }

        RxTextView.afterTextChangeEvents(findViewById(R.id.et_running))
                .subscribe {
                    runningMin = try {
                        var time = it.editable().toString()
                        time.toInt()
                    } catch (e: Exception) {
                        0
                    }
                }

        RxTextView.afterTextChangeEvents(findViewById(R.id.et_Count))
                .subscribe {
                    reCount = try {
                        var time = it.editable().toString()
                        time.toInt()
                    } catch (e: Exception) {
                        0
                    }
                }

        findViewById<View>(R.id.btn_start).setOnClickListener {
            Toast.makeText(this, "开始了", Toast.LENGTH_SHORT).show()
            //检测
            if (walkMin <= 0) {
                walkMin = 1
//                Toast.makeText(this, "走路分钟数必须大于零", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
            }

            if (runningMin <= 0) {
                runningMin = 2
//                Toast.makeText(this, "跑步分钟数必须大于零", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
            }

            if (reCount <= 0) {
                reCount = 7
//                Toast.makeText(this, "重复次数必须大于零", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
            }

            var amManager = AmMangerClient.create(this)
            amManager.cancelAlarm()

            var walkIntent = Intent(AmMangerClient.ACTION_RUN_WALK)
            walkIntent.putExtra("walk", walkMin * 60 * 1000L)
                    .putExtra("msg", AmMangerClient.TYPE_WALK)
                    .putExtra("running", runningMin * 60 * 1000L)
                    .putExtra("re_count", reCount)
                    .putExtra("now_count", 1)
            amManager.setAndSendTask(AmMangerClient.REQ_WALK, walkIntent, walkMin * 60 * 1000L)

            var runningIntent = Intent(AmMangerClient.ACTION_RUN_WALK)
            runningIntent.putExtra("walk", walkMin * 60 * 1000L)
                    .putExtra("msg", AmMangerClient.TYPE_RUNNING)
                    .putExtra("running", runningMin * 60 * 1000L)
                    .putExtra("re_count", reCount)
                    .putExtra("now_count", 1)
            amManager.setAndSendTask(AmMangerClient.REQ_RUNNING, runningIntent, (walkMin + runningMin) * 60 * 1000L)
            tvWalkStatus.text = "第1次走路"
        }

        findViewById<View>(R.id.btn_end).setOnClickListener {
            var amManager = AmMangerClient.create(this)
            amManager.cancelAlarm()
        }

    }

    private fun walk() {
        tvWalkStatus.text = "该走路了"
    }

    private fun run() {
        tvWalkStatus.text = "该跑步了"
    }


    /**
     * 震动
     */
    private fun vibrator() {
        var mVibrator = application.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator

        //设置震动周期，数组表示时间：等待+执行，单位是毫秒，下面操作代表:等待100，执行100，等待100，执行1000，
        //后面的数字如果为-1代表不重复，之执行一次，其他代表会重复，0代表从数组的第0个位置开始
        mVibrator.vibrate(longArrayOf(100, 100, 100, 1000), -1)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(walkRun: WalkRunEvent) {
        if (walkRun.eventCount > reCount) {
            tvWalkStatus.text = "恭喜您完成了运动"
            return
        }
        when (walkRun.eventType) {
            AmMangerClient.TYPE_WALK -> {
                tvWalkStatus.text = "第${walkRun.eventCount}次跑步"
            }
            AmMangerClient.TYPE_RUNNING -> {
                tvWalkStatus.text = "第${walkRun.eventCount + 1}次走路"
            }
        }
    }


}
