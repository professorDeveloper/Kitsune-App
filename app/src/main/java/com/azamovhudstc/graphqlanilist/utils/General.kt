/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */


package com.azamovhudstc.graphqlanilist.utils

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.*
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavOptions
import androidx.viewpager2.widget.ViewPager2
import com.azamovhudstc.graphqlanilist.DetailFullDataQuery
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.application.App
import com.google.android.material.snackbar.Snackbar
import java.lang.reflect.Field
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CancellationException
import kotlin.math.log2
import kotlin.math.pow

val Int.dp: Float get() = (this / Resources.getSystem().displayMetrics.density)
val Float.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
fun DetailFullDataQuery.StartDate.toSortString(): String {
    if (day == null && year == null && month == null)
        return "??"
    val a = if (month != null) DateFormatSymbols().months[month - 1] else ""
    return (if (day != null) "$day " else "") + a + (if (year != null) ", $year" else "")
}

suspend fun <T> tryWithSuspend(
    post: Boolean = false,
    snackbar: Boolean = true,
    call: suspend () -> T
): T? {
    return try {
        call.invoke()
    } catch (e: Throwable) {
        logError(e)
        null
    } catch (e: CancellationException) {
        null
    }
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
        if (position == 0.0f) {
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



fun <T> randomSelectFromList(list: List<T>): T? {
    val filteredList = list.filter { it != "HENTAI" }
    if (filteredList.isEmpty()) {
        return null
    }
    return filteredList[Random().nextInt(filteredList.size)]

}



fun snackString(s: String?, activity: Activity? = null, clipboard: String? = null) {
    if (s != null) {
        (activity)?.apply {
            runOnUiThread {
                val snackBar = Snackbar.make(
                    window.decorView.findViewById(android.R.id.content),
                    s,
                    Snackbar.LENGTH_LONG
                )
                snackBar.view.apply {
                    updateLayoutParams<FrameLayout.LayoutParams> {
                        gravity = (Gravity.CENTER_HORIZONTAL or Gravity.TOP)
                        width = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                    translationY = (24f + 32f)
                    translationZ = 32f
                    val shapeDrawable = ShapeDrawable()
                    shapeDrawable.paint.color =
                        Color.parseColor("#9E120F0F") // Set the background color if needed
                    shapeDrawable.paint.style = Paint.Style.FILL
                    shapeDrawable.shape = RoundRectShape(
                        floatArrayOf(120f, 120f, 120f, 120f, 120f, 120f, 120f, 120f),
                        null,
                        null
                    )

                    this.background = shapeDrawable
                    setOnClickListener {
                        snackBar.dismiss()
                    }
                    setOnLongClickListener {
                        true
                    }
                }
                snackBar.show()
            }
        }
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

abstract class GesturesListener : GestureDetector.SimpleOnGestureListener() {
    private var timer: Timer? = null //at class level;
    private val delay: Long = 200

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        processSingleClickEvent(e)
        return super.onSingleTapUp(e)
    }

    override fun onLongPress(e: MotionEvent) {
        processLongClickEvent(e)
        super.onLongPress(e)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        processDoubleClickEvent(e)
        return super.onDoubleTap(e)
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        onScrollYClick(distanceY)
        onScrollXClick(distanceX)
        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    private fun processSingleClickEvent(e: MotionEvent) {
        val handler = Handler(Looper.getMainLooper())
        val mRunnable = Runnable {
            onSingleClick(e)
        }
        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    handler.post(mRunnable)
                }
            }, delay)
        }
    }

    private fun processDoubleClickEvent(e: MotionEvent) {
        timer?.apply {
            cancel()
            purge()
        }
        onDoubleClick(e)
    }

    private fun processLongClickEvent(e: MotionEvent) {
        timer?.apply {
            cancel()
            purge()
        }
        onLongClick(e)
    }

    open fun onSingleClick(event: MotionEvent) {}
    open fun onDoubleClick(event: MotionEvent) {}
    open fun onScrollYClick(y: Float) {}
    open fun onScrollXClick(y: Float) {}
    open fun onLongClick(event: MotionEvent) {}
}

fun getCurrentBrightnessValue(context: Context): Float {
    fun getMax(): Int {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        val fields: Array<Field> = powerManager.javaClass.declaredFields
        for (field in fields) {
            if (field.name.equals("BRIGHTNESS_ON")) {
                field.isAccessible = true
                return try {
                    field.get(powerManager)?.toString()?.toInt() ?: 255
                } catch (e: IllegalAccessException) {
                    255
                }
            }
        }
        return 255
    }

    fun getCur(): Float {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            127
        ).toFloat()
    }

    return brightnessConverter(getCur() / getMax(), true)
}

fun animationTransactionClearStack(clearFragmentID: Int): NavOptions.Builder {
    val navBuilder = NavOptions.Builder()
    navBuilder.setEnterAnim(R.anim.from_right).setExitAnim(R.anim.to_left)
        .setPopEnterAnim(R.anim.from_left).setPopExitAnim(R.anim.to_right)
        .setPopUpTo(clearFragmentID, true)
    return navBuilder
}

fun animationTransaction(): NavOptions.Builder {
    val navBuilder = NavOptions.Builder()
    navBuilder.setEnterAnim(R.anim.from_right).setExitAnim(R.anim.to_left)
        .setPopEnterAnim(R.anim.from_left).setPopExitAnim(R.anim.to_right)
    return navBuilder
}

fun brightnessConverter(it: Float, fromLog: Boolean) =
    MathUtils.clamp(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            if (fromLog) log2((it * 256f)) * 12.5f / 100f else 2f.pow(it * 100f / 12.5f) / 256f
        else it, 0.001f, 1f
    )


fun View.slideUp(animTime: Long, startOffset: Long) {
    val slideUp = AnimationUtils.loadAnimation(App.instance, R.anim.slide_up).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}

fun DetailFullDataQuery.Trailer.getYoutubeFormat(): String {
    return if (this.site != null && this.site.toString() == "youtube")
        "https://www.youtube.com/embed/${this.id.toString().trim('"')}"
    else ""
}

fun View.slideStart(animTime: Long, startOffset: Long, hide: View? = null) {
    val slideUp = AnimationUtils.loadAnimation(App.instance, R.anim.slide_start).apply {
        setAnimationListener(object : Animation.AnimationListener {
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


