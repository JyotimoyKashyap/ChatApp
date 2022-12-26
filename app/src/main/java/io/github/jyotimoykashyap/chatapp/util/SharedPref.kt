package io.github.jyotimoykashyap.chatapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SharedPref {

    // keys
    const val BRANCH_AUTH_TOKEN = "X-Branch-Auth-Token"

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    lateinit var sharedPreferences: SharedPreferences

    // call this in activity
    fun createSharedPreferences(applicationContext: Context) {
        sharedPreferences = EncryptedSharedPreferences.create(
            "preferendesfile",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun clearSharedPrefs() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    fun saveEntry(key: String, value: Any) {
        when(value) {
            is Boolean -> {
                sharedPreferences.edit()
                    .putBoolean(key, value)
                    .apply()
            }
            is Int -> {
                sharedPreferences.edit()
                    .putInt(key, value)
                    .apply()
            }
            is Long -> {
                sharedPreferences.edit()
                    .putLong(key, value)
                    .apply()
            }
            is String -> {
                sharedPreferences.edit()
                    .putString(key, value)
                    .apply()
            }
            is Float -> {
                sharedPreferences.edit()
                    .putFloat(key, value)
                    .apply()
            }
        }
    }

    fun readEntry(key: String) : String {
        val value = sharedPreferences
            .getString(key, "null")
        return value ?: "null"
    }

    fun readEntry(key: String, type: Any) =
        when(type) {
            is String -> {
                sharedPreferences
                    .getString(key, "null")
            }
            is Boolean -> {
                sharedPreferences
                    .getBoolean(key, false)
            }
            is Int -> {
                sharedPreferences
                    .getInt(key, -1)
            }
            is Float -> {
                sharedPreferences
                    .getFloat(key, 0f)
            }
            is Long -> {
                sharedPreferences
                    .getLong(key, 0L)
            }
            else -> {
                null
            }
        }




}