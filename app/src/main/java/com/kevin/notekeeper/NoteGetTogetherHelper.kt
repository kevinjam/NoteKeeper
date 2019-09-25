package com.kevin.notekeeper

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.util.Log

class NoteGetTogetherHelper(val context: Context, val lifecycler:Lifecycle) :LifecycleObserver {

    init {
        lifecycler.addObserver(this)
    }
    val tag = this::class.java.simpleName

    var currentLat =0.0
    var cuttentLon =0.0

    val locManager = PseudoLocationManager(context) {lat,lon->

        currentLat = lat
        cuttentLon = lon

    }
    val msgManager = PseudoMessagingManager(context)
    var msgConnection:PseudoMessagingConnection? = null


    fun sendMessage(note:NoteInfo){
        val getTogetherMessage = "$currentLat | $cuttentLon | {${note.title} |{${note.course?.title}}"
        msgConnection!!.send(getTogetherMessage)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startHangler()
    {
    locManager.start()
        msgManager.connect { connection->
            Log.e(tag, "Connection callback -----LifeCycle state : ${lifecycler.currentState}")
            if (lifecycler.currentState.isAtLeast(Lifecycle.State.STARTED))
            msgConnection = connection
            else
                connection.disconnect()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopHandler(){
        Log.e(tag, "stopHandler")
        locManager.stop()
        msgConnection?.disconnect()
    }
}