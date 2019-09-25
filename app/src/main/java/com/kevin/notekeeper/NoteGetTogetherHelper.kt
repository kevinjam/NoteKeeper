package com.kevin.notekeeper

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context

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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startHangler()
    {
    locManager.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopHandler(){
        locManager.stop()
    }
}