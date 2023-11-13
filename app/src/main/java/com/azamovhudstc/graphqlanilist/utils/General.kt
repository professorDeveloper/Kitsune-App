
package com.azamovhudstc.graphqlanilist.utils

import android.R.attr.translationY
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.application.App
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

val Int.dp: Float get() = (this / Resources.getSystem().displayMetrics.density)
val Float.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
 fun DetailFullDataQuery.StartDate.toSortString(): String {
    if (day == null && year == null && month == null)
        return "??"
    val a = if (month != null) DateFormatSymbols().months[month - 1] else ""
    return (if (day != null) "$day " else "") + a + (if (year != null) ", $year" else "")
}

fun randomColor(): Int {
    val color = listOf(
        Color.parseColor("#24687B"),
        Color.parseColor("#014037"),
        Color.parseColor("#7E1416"),
        Color.parseColor("#989D60"),
        Color.parseColor("#BF5264"),
        Color.parseColor("#542437"),
        Color.parseColor("#329669"),
        Color.parseColor("#3D3251"),
        Color.parseColor("#D85C43"),
        Color.parseColor("#C02944"),
        Color.parseColor("#D3B042"),
    )
    return color.shuffled().first()
}

fun DetailFullDataQuery.EndDate.toSortString(): String {
    if (day == null && year == null && month == null)
        return "??"
    val a = if (month != null) DateFormatSymbols().months[month - 1] else ""
    return (if (day != null) "$day " else "") + a + (if (year != null) ", $year" else "")
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}
fun localLoadTabTxt(): ArrayList<String> {
    val list = ArrayList<String>()
    list.add("More Details")
    list.add("Anime")
    return list
}
class ZoomOutPageTransformer() : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        if (position == 0.0f ) {
            setAnimation(view.context, view, 300, floatArrayOf(1.3f, 1f, 1.3f, 1f), 0.5f to 0f)
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1.0f).setDuration((200 * 1f).toLong()).start()
        }
    }


}
fun setAnimation(
    context: Context?,
    viewToAnimate: View,
    duration: Long = 150,
    list: FloatArray = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f),
    pivot: Pair<Float, Float> = 0.5f to 0.5f
) {

    val anim = ScaleAnimation(
        list[0],
        list[1],
        list[2],
        list[3],
        Animation.RELATIVE_TO_SELF,
        pivot.first,
        Animation.RELATIVE_TO_SELF,
        pivot.second
    )
    anim.duration = (duration * 1f).toLong()
    anim.setInterpolator(context, R.anim.over_shoot)
    viewToAnimate.startAnimation(anim)
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
fun View.slideTop(animTime: Long, startOffset: Long) {
    val slideUp = AnimationUtils.loadAnimation(App.instance, R.anim.slide_top).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}


fun View.slideUp(animTime: Long, startOffset: Long) {
    val slideUp = AnimationUtils.loadAnimation(App.instance, R.anim.slide_up).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}
fun View.alpha() {
    animate().
    translationY(height.toFloat())
        .alpha(0.0f)
        .setDuration(555)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                visibility = View.GONE
            }
        })
}
fun DetailFullDataQuery.Trailer.getYoutubeFormat(): String {
    return if (this.site != null && this.site.toString() == "youtube")
        "https://www.youtube.com/embed/${this.id.toString().trim('"')}"
    else ""
}
fun View.slideStart(animTime: Long, startOffset: Long,hide:View?=null) {
    val slideUp = AnimationUtils.loadAnimation(App.instance, R.anim.slide_start).apply {
        setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
                hide?.hide()

            }
            override fun onAnimationEnd(animation: Animation?) {
                hide?.hide()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                TODO("Not yet implemented")
            }

        })
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}
fun View.slideEnd(animTime: Long, startOffset: Long) {
    val slideUp = AnimationUtils.loadAnimation(App.instance, R.anim.slide_end).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}


