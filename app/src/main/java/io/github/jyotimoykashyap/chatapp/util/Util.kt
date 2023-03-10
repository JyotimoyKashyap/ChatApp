package io.github.jyotimoykashyap.chatapp.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.*

object Util {

    /**
     * Below is the pattern for the date format
     * ```
     * 2017-02-01T15:58:00.000Z"
     * ```
     * So we can derive the pattern as
     * ```
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ
     * ```
     */

    fun convertTimeStampToLong(s: String) : Long {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val time = simpleDateFormat.parse(s)
        return time.time
    }

    fun convertTimeStamp(s: String) : String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val dateObject = simpleDateFormat.parse(s)
        val calendar = Calendar.getInstance()
        if (dateObject != null) {
            calendar.time = dateObject
        }

        // dates
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        val year = calendar.get(Calendar.YEAR)
        val date = calendar.get(Calendar.DATE)

        // time
        val hour = calendar.get(Calendar.HOUR)
        val minute = String.format("%02d", calendar.get(Calendar.MINUTE))
        val amPm = calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault())
            .uppercase(Locale.ROOT)

        return "$month $date $year, $hour:$minute $amPm"
    }

    fun hideSoftKeyboard(view: View, context: Context) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}