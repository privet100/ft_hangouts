package com.music42.ft_hangouts

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat

class App : Application() {

    companion object {
        private const val PREFS_NAME = "ft_hangouts_prefs"
        private const val KEY_HEADER_COLOR = "header_color"
        private const val KEY_BACKGROUND_TIME = "background_time"

        private lateinit var instance: App

        fun getPrefs(): SharedPreferences {
            return instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        fun getHeaderColor(): Int {
            val colorId = getPrefs().getInt(KEY_HEADER_COLOR, R.color.header_blue)
            return ContextCompat.getColor(instance, colorId)
        }

        fun setHeaderColor(colorResId: Int) {
            getPrefs().edit().putInt(KEY_HEADER_COLOR, colorResId).apply()
        }

        fun getHeaderColorResId(): Int {
            return getPrefs().getInt(KEY_HEADER_COLOR, R.color.header_blue)
        }

        fun setBackgroundTime(time: Long) {
            getPrefs().edit().putLong(KEY_BACKGROUND_TIME, time).apply()
        }

        fun getBackgroundTime(): Long {
            return getPrefs().getLong(KEY_BACKGROUND_TIME, 0)
        }

        fun clearBackgroundTime() {
            getPrefs().edit().remove(KEY_BACKGROUND_TIME).apply()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
