
package com.azamovhudstc.graphqlanilist.utils

import android.app.Activity
import android.content.ClipData.newIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}
inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)

inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}


 fun String.removeSource(): String {
    val regex = Regex("\\(Source:.*\\)")
    var text = this
    text = regex.replace(text, "").trim()
    text = text.replace("\n$", "")
    return text
}

/**
 * It logs the error message, the localized message, the throwable object, and the stack trace
 *
 * @param throwable The exception that was thrown.
 */
fun logError(throwable: Throwable?) {
    Log.e("ApiError", "-------------------------------------------------------------------")
    Log.e("ApiError", "safeApiCall: " + throwable?.localizedMessage)
    Log.e("ApiError", "safeApiCall: " + throwable?.message)
    Log.e("ApiError", "safeApiCall: $throwable")
    throwable?.printStackTrace()
    Log.e("ApiError", "-------------------------------------------------------------------")
}

fun logMessage(string: String?) {
    Log.e("Error Happened", "-------------------------------------------------------------------")
    Log.e("Error Happened", "--->: ${string.orEmpty()}")
    Log.e("Error Happened", "-------------------------------------------------------------------")
}

/* A function that is used to set the text of a TextView to a HTML string. */
fun TextView.setHtmlText(htmlString: String?) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlString)
    }
}

/**
Dismiss Keyboard function added
**/

fun Int?.or1() = this ?: 1

fun dismissKeyboard(view: View?) {
    view?.let {
        ViewCompat.getWindowInsetsController(view)?.hide(WindowInsetsCompat.Type.ime())
    }
}

/**
 * It takes a number of seconds since the epoch and returns a string in the format "Day, dd Month
 * yyyy, hh:mm a"
 *
 * @param seconds The number of seconds since January 1, 1970 00:00:00 UTC.
 * @return The date in the format of Day, Date Month Year, Hour:Minute AM/PM
 */
fun displayInDayDateTimeFormat(seconds: Int): String {
    val dateFormat = SimpleDateFormat("E, dd MMM yyyy, hh:mm a", Locale.getDefault())
    val date = Date(seconds * 1000L)
    return dateFormat.format(date)
}
