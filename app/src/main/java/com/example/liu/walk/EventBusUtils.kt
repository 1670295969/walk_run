package com.example.liu.walk

import android.util.EventLog
import org.greenrobot.eventbus.EventBus

/**
 * Created by liu on 18-1-15.
 */

data class WalkRunEvent(
        var eventType: String,
        var eventCount: Int
)

object EventBusUtils {

    fun register(any: Any) {
        if (!EventBus.getDefault().isRegistered(any)) {
            EventBus.getDefault().register(any)
        }
    }

    fun unregister(any: Any) {
        EventBus.getDefault().unregister(this)
    }

    fun post(any: Any) {
        EventBus.getDefault().post(any)
    }


}