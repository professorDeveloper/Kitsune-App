package com.azamovhudstc.graphqlanilist.bindings

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Genre
import com.azamovhudstc.graphqlanilist.di.GlideApp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import com.ms.square.android.expandabletextview.ExpandableTextView

/**
 * A binding adapter that loads an image from a url into an ImageView.
 *
 * @param image ImageView - The ImageView that we want to bind the image to.
 * @param url The URL of the image to load.
 */
@BindingAdapter("image")
fun ImageView.provideImageBinding(url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(context)
            .load(url)
            .into(this)
    }
}

@BindingAdapter("image")
fun ShapeableImageView.provideImageBinding(url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(context)
            .load(url)
            .into(this)
    }
}


//@BindingAdapter("avatarImage")
//fun ImageView.setAvatarImage(url: String?) {
//    if (!url.isNullOrEmpty()) {
//        this.load(url) {
//            crossfade(true)
//            transformations(CircleCropTransformation())
//        }
//    }
//}

/**
 * If the value is true, show the view, otherwise hide it.
 *
 * @param view View - The view you want to change the visibility of
 * @param value The value that will be passed to the binding adapter.
 */
@BindingAdapter("android:visibility")
fun View.setVisibility(value: Boolean) {
    isVisible = value
}

/**
 * "If the isFiller boolean is true, set the background color of the CardView to #2B2C30, otherwise
 * set it to #17293F."
 *
 * The @BindingAdapter annotation is what tells the compiler that this function is a binding
 * adapter. The first parameter is the name of the attribute that we'll be using in our XML layout.
 * The second parameter is the type of the attribute. In this case, it's a boolean
 *
 * @param view CardView - The view that we're binding to.
 * @param isFiller Boolean - This is the parameter that we will pass to the binding adapter.
 */
@BindingAdapter("isFiller")
fun CardView.changeFillerBg(isFiller: Boolean) {
    setCardBackgroundColor(Color.parseColor(if (isFiller) "#2B2C30" else "#17293F"))
}
fun ExpandableTextView.setHtmlText(description: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(description)
    }
}
fun Genre.getColors(): Triple<ColorStateList, ColorStateList, ColorStateList> {
    val color = when (name) {
        "Action" -> "#24687B"
        "Adventure" -> "#014037"
        "Comedy" -> "#E6977E"
        "Drama" -> "#7E1416"
        "Ecchi" -> "#7E174A"
        "Fantasy" -> "#989D60"
        "Hentai" -> "#37286B"
        "Horror" -> "#5B1765"
        "Mahou Shoujo" -> "#BF5264"
        "Mecha" -> "#542437"
        "Music" -> "#329669"
        "Mystery" -> "#3D3251"
        "Psychological" -> "#D85C43"
        "Romance" -> "#C02944"
        "Sci-Fi" -> "#85B14B"
        "Slice of Life" -> "#D3B042"
        "Sports" -> "#6B9145"
        "Supernatural" -> "#338074"
        "Thriller" -> "#224C80"
        else -> "#000000"
    }.toStateListColor()

    val outlineColor = when (name) {
        "Action" -> "#1c4f62"
        "Adventure" -> "#7fbf8c"
        "Comedy" -> "#d46c5f"
        "Drama" -> "#5f0c0e"
        "Ecchi" -> "#5f0c33"
        "Fantasy" -> "#6d7143"
        "Hentai" -> "#2b1c4d"
        "Horror" -> "#4d1c4f"
        "Mahou Shoujo" -> "#a8414d"
        "Mecha" -> "#3a1c1e"
        "Music" -> "#1f5a3c"
        "Mystery" -> "#2d1f3a"
        "Psychological" -> "#9c422d"
        "Romance" -> "#82162a"
        "Sci-Fi" -> "#4d7738"
        "Slice of Life" -> "#b09c39"
        "Sports" -> "#4d6230"
        "Supernatural" -> "#24695f"
        "Thriller" -> "#1c3d6e"
        else -> "#000000"
    }.toStateListColor()


    val r = Color.red(color.defaultColor)
    val g = Color.green(color.defaultColor)
    val b = Color.blue(color.defaultColor)
    // Calculate the luminance of the chip color
    val luminance = 0.299 * r + 0.587 * g + 0.114 * b

    // Set the text color based on the luminance
    val textColor = if (luminance > 160) {
        ColorStateList.valueOf(Color.BLACK)
    } else {
        ColorStateList.valueOf(Color.WHITE)
    }

    return Triple(color, outlineColor, textColor)
}
fun String.toStateListColor(): ColorStateList {
    return ColorStateList.valueOf(Color.parseColor(this))
}
@BindingAdapter("htmlText")
fun TextView.setHtml(htmlString: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlString)
    }
}

@BindingAdapter("animatedProgress")
fun ContentLoadingProgressBar.setAnimatedProgress(progress: Int) {
    ObjectAnimator.ofInt(this, "progress", progress)
        .setDuration(400)
        .start()
}

//@BindingAdapter("markdown")
//fun TextView.setMarkdownText(string: String) {
//    text = Markwon.create(this.context)
//        .toMarkdown(string)
//}
