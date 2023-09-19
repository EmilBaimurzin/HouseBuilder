package com.builder.game.ui.other

import android.app.Application
import com.builder.game.data.data_base.Database

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Database.init(this)
    }

}