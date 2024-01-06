/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AnimePlayingDetails
import com.azamovhudstc.graphqlanilist.databinding.ActivityPlayerBinding
import com.azamovhudstc.graphqlanilist.ui.adapter.CustomAdapter
import com.azamovhudstc.graphqlanilist.utils.*
import com.azamovhudstc.graphqlanilist.utils.widgets.DoubleTapPlayerView
import com.azamovhudstc.graphqlanilist.viewmodel.PlayerViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.github.rubensousa.previewseekbar.PreviewBar
import com.github.rubensousa.previewseekbar.media3.PreviewTimeBar
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.ui.CaptionStyleCompat.*
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import kotlin.math.min


@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {
    private var notchHeight: Int = 0

    private val model by viewModels<PlayerViewModel>()
    private var quality: String = "Auto"
    private lateinit var animePlayingDetails: AnimePlayingDetails
    private lateinit var exoTopControllers: LinearLayout
    private lateinit var exoMiddleControllers: LinearLayout
    private lateinit var exoBottomControllers: LinearLayout
    private val handler = Handler(Looper.getMainLooper())
    private var isFullscreen: Int = 0
    private var orientationListener: OrientationEventListener? = null

    private var isNormal = true


    // Top buttons
    private lateinit var loadingLayout: LinearLayout
    private lateinit var playerView: DoubleTapPlayerView
    private lateinit var subsToggleButton: ToggleButton
    private lateinit var exoPlay: ImageView
    private lateinit var scaleBtn: ImageButton
    private lateinit var exoRotate: ImageButton
    private lateinit var qualityBtn: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var prevEpBtn: ImageButton
    private var doubleBackToExitPressedOnce: Boolean = false
    private lateinit var backPressSnackBar: Snackbar
    private lateinit var nextEpBtn: ImageButton
    private lateinit var videoEpTextView: TextView
    private lateinit var exoPip: ImageButton
    private lateinit var exoSpeed: ImageButton
    private lateinit var exoProgress: ExtendedTimeBar
    private lateinit var exoLock: ImageButton
    private var isInit: Boolean = false
    private var isTV: Boolean = false
    private val mCookieManager = CookieManager()
    private lateinit var exoBrightness: Slider
    private lateinit var exoVolume: Slider
    private lateinit var exoBrightnessCont: View
    private lateinit var exoVolumeCont: View
    var rotation = 0
    private lateinit var previewTimeBar: PreviewTimeBar


    override fun onAttachedToWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val displayCutout = window.decorView.rootWindowInsets.displayCutout
            if (displayCutout != null) {
                if (displayCutout.boundingRects.size > 0) {
                    notchHeight = min(
                        displayCutout.boundingRects[0].width(),
                        displayCutout.boundingRects[0].height()
                    )
                    checkNotch()
                }
            }
        }
        super.onAttachedToWindow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        // show video inside notch if API >= 28 and orientation is landscape
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        ) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        parseExtra()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // hiding 3 bottom buttons by default and showing when user swipes
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        //Initialize
        hideSystemBars()
        onBackPressedDispatcher.addCallback(this) {
            finishAndRemoveTask()
        }


        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        CookieHandler.setDefault(mCookieManager)
        playerView = binding.exoPlayerView
        playerView.doubleTapOverlay = binding.doubleTapOverlay
        loadingLayout = binding.loadingLayout

        exoPip = playerView.findViewById(R.id.exo_pip)
        exoSpeed = playerView.findViewById(R.id.exo_speed)
        prevEpBtn = playerView.findViewById(R.id.exo_prev_ep)
        exoRotate = playerView.findViewById(R.id.exo_rotate)
        nextEpBtn = playerView.findViewById(R.id.exo_next_ep)
        videoEpTextView = playerView.findViewById(R.id.exo_title)
        exoLock = playerView.findViewById(R.id.exo_lock)
        exoPlay = playerView.findViewById(R.id.exo_play_pause)
        exoMiddleControllers = findViewById(R.id.exo_middle_controllers)
        exoTopControllers = findViewById(R.id.exo_top_controllers)
        exoBottomControllers = findViewById(R.id.exo_bottom_controllers)
        exoBrightness = findViewById(R.id.exo_brightness)
        exoVolume = findViewById(R.id.exo_volume)
        exoBrightnessCont = findViewById(R.id.exo_brightness_cont)
        exoVolumeCont = findViewById(R.id.exo_volume_cont)
        imageView = findViewById(R.id.imageView)
        exoProgress = findViewById(R.id.exo_progress)
        updateEpisodeName()
        playerView.keepScreenOn = true
        playerView.player = model.player
        playerView.subtitleView?.visibility = View.VISIBLE
        playerView.findViewById<ExtendedTimeBar>(R.id.exo_progress).setKeyTimeIncrement(10000)
        prepareButtons()

        initSubStyle()


        model.isLoading.observe(this) { isLoading ->
            loadingLayout.isVisible = isLoading
            playerView.isVisible = !isLoading
        }
        model.keepScreenOn.observe(this) { keepScreenOn ->
            playerView.keepScreenOn = keepScreenOn
        }
        model.playNextEp.observe(this) { playNextEp ->
            if (playNextEp) setNewEpisode()
        }
        model.isError.observe(this) { isError ->
            if (isError) {
                finishAndRemoveTask()
            }
        }
        if (!isInit) {
            model.setAnimeLink(
                sourceType,
                animePlayingDetails.animeUrl,
                animePlayingDetails.animeEpisodeMap[animePlayingDetails.animeEpisodeIndex] as String,
                listOf(animePlayingDetails.epType)
            )
            prevEpBtn.setImageViewEnabled(animePlayingDetails.animeEpisodeIndex.toInt() >= 2)
            nextEpBtn.setImageViewEnabled(animePlayingDetails.animeEpisodeIndex.toInt() != animePlayingDetails.animeTotalEpisode.toInt())
        }
        isInit = true
    }

    private fun initSubStyle() {

        ///Init Subtityle
        val primaryColor = when (5) {
            0 -> Color.BLACK
            1 -> Color.DKGRAY
            2 -> Color.GRAY
            3 -> Color.LTGRAY
            4 -> Color.WHITE
            5 -> Color.RED
            6 -> Color.YELLOW
            7 -> Color.GREEN
            8 -> Color.CYAN
            9 -> Color.BLUE
            10 -> Color.MAGENTA
            11 -> Color.TRANSPARENT
            else -> Color.WHITE
        }
        val secondaryColor = when (0) {
            0 -> Color.BLACK
            1 -> Color.DKGRAY
            2 -> Color.GRAY
            3 -> Color.LTGRAY
            4 -> Color.WHITE
            5 -> Color.RED
            6 -> Color.YELLOW
            7 -> Color.GREEN
            8 -> Color.CYAN
            9 -> Color.BLUE
            10 -> Color.MAGENTA
            11 -> Color.TRANSPARENT
            else -> Color.BLACK
        }
        val outline = when (1) {
            0 -> EDGE_TYPE_OUTLINE // Normal
            1 -> EDGE_TYPE_DEPRESSED // Shine
            2 -> EDGE_TYPE_DROP_SHADOW // Drop shadow
            3 -> EDGE_TYPE_NONE // No outline
            else -> EDGE_TYPE_OUTLINE // Normal
        }
        val subBackground = when (9) {
            0 -> Color.TRANSPARENT
            1 -> Color.BLACK
            2 -> Color.DKGRAY
            3 -> Color.GRAY
            4 -> Color.LTGRAY
            5 -> Color.WHITE
            6 -> Color.RED
            7 -> Color.YELLOW
            8 -> Color.GREEN
            9 -> Color.CYAN
            10 -> Color.BLUE
            11 -> Color.MAGENTA
            else -> Color.TRANSPARENT
        }
        val subWindow = when (4) {
            0 -> Color.TRANSPARENT
            1 -> Color.BLACK
            2 -> Color.DKGRAY
            3 -> Color.GRAY
            4 -> Color.LTGRAY
            5 -> Color.WHITE
            6 -> Color.RED
            7 -> Color.YELLOW
            8 -> Color.GREEN
            9 -> Color.CYAN
            10 -> Color.BLUE
            11 -> Color.MAGENTA
            else -> Color.TRANSPARENT
        }
        val font = when (0) {
            0 -> ResourcesCompat.getFont(this, R.font.animity)
            1 -> ResourcesCompat.getFont(this, R.font.poppins_bold)
            2 -> ResourcesCompat.getFont(this, R.font.poppins)
            3 -> ResourcesCompat.getFont(this, R.font.poppins_thin)
            else -> ResourcesCompat.getFont(this, R.font.inter_bold)
        }


        val subStyle = CaptionStyleCompat(
            Color.RED,
            Color.GREEN,
            Color.RED,
            outline,
            Color.RED,
            font
        )
        playerView.subtitleView?.setStyle(subStyle)
    }

    private fun updateEpisodeName() {
        videoEpTextView.text =
            resources.getString(R.string.episode, animePlayingDetails.animeEpisodeIndex)

    }


    private fun initPopupQuality(): Dialog {

        val trackSelectionDialogBuilder =
            TrackSelectionDialogBuilder(
                this,
                "Available Qualities",
                model.player,
                C.TRACK_TYPE_VIDEO
            )
        trackSelectionDialogBuilder.setTheme(R.style.SpeedDialog)
        trackSelectionDialogBuilder.setTrackNameProvider {
            if (it.frameRate > 0f) it.height.toString() + "p" else it.height.toString() + "p (fps : N/A)"
        }
        val trackDialog = trackSelectionDialogBuilder.build()
        trackDialog.setOnDismissListener { hideSystemBars() }
        return trackDialog
    }

    @SuppressLint("ClickableViewAccessibility")
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun prepareButtons() {
        // For Screen Rotation
        var flag = true
        exoRotate.setOnClickListener {
            if (flag) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                flag = false
            } else {
                this.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                flag = true

            }
        }


        val gestureSpeed = (300 * 1f).toLong()
        //Player UI Visibility Handler
        val brightnessRunnable = Runnable {
            if (exoBrightnessCont.alpha == 1f)
                lifecycleScope.launch {
                    ObjectAnimator.ofFloat(exoBrightnessCont, "alpha", 1f, 0f)
                        .setDuration(gestureSpeed).start()
                    delay(gestureSpeed)
                    exoBrightnessCont.visibility = View.GONE
                    checkNotch()
                }
        }

        playerView.setLongPressListenerEvent {
            val currentSpeed = model.player.playbackParameters.speed
            if (currentSpeed == 1f && model.player.playWhenReady && isNormal) {
                val params = PlaybackParameters(2f)
                model.player.setPlaybackParameters(params)
                snackString("Speed 2x", this@PlayerActivity)
            }
        }



        playerView.setActionUpListener {
            val currentSpeed = model.player.playbackParameters.speed
            if (currentSpeed == 2f && model.player.playWhenReady && isNormal) {
                val params = PlaybackParameters(1f)
                model.player.setPlaybackParameters(params)
                snackString("Speed 1x", this@PlayerActivity)
            }
        }

        brightnessRunnable.run()
        // Custom player views
        exoLock.setOnClickListener {
            if (!isLocked) {
                exoLock.setImageResource(R.drawable.ic_lock_24)
            } else {
                exoLock.setImageResource(R.drawable.ic_lock_open_24)
            }
            isLocked = !isLocked
            lockScreen(isLocked)
        }

        scaleBtn = playerView.findViewById(R.id.exo_screen)
        qualityBtn = playerView.findViewById(R.id.exo_quality)
        prevEpBtn = playerView.findViewById(R.id.exo_prev_ep)
        nextEpBtn = playerView.findViewById(R.id.exo_next_ep)
        subsToggleButton = playerView.findViewById(R.id.subs_toggle_btn)


//        model.player.playbackParameters =

        qualityBtn.setOnClickListener {
            initPopupQuality().show()
        }

        exoSpeed.setOnClickListener {
            val builder =
                AlertDialog.Builder(this, R.style.SpeedDialog)
            builder.setTitle("Speed")


            val speed = arrayOf("0.25", "0.5", "Normal", "1.5", "2")
            val adapter = CustomAdapter(
                this,
                speed
            )
            builder.setAdapter(adapter) { dad, which ->
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                hideSystemBars()

                when (which) {
                    0 -> {
                        isNormal = false
                        adapter.setSelected(0)
                        changeVideoSpeed(0.25f)
                    }
                    1 -> {
                        isNormal = false
                        adapter.setSelected(1)
                        changeVideoSpeed(0.5f)
                    }
                    2 -> {
                        isNormal = true

                        adapter.setSelected(2)
                        changeVideoSpeed(1f)
                    }
                    3 -> {
                        isNormal = false
                        adapter.setSelected(3)
                        changeVideoSpeed(1.5f)
                    }
                    else -> {
                        isNormal = false
                        adapter.setSelected(4)
                        changeVideoSpeed(2f)

                    }
                }
            }
            hideSystemBars()

            val dialog = builder.create()
            dialog.show()
        }


        subsToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                playerView.subtitleView?.visibility = View.VISIBLE
            } else {
                playerView.subtitleView?.visibility = View.GONE
            }
        }

        exoPip.setOnClickListener {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), packageName
                ) == AppOpsManager.MODE_ALLOWED
            } else false
            // API >= 26 check
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (status) {
                    this.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                    playerView.useController = false
                    pipStatus = false
                } else {
                    val intent = Intent(
                        "android.settings.PICTURE_IN_PICTURE_SETTINGS",
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
            } else {
                Toast.makeText(
                    this,
                    "Feature not supported on this device",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        when (isFullscreen) {
            0 -> {
                if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                } else {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                }
            }
            1 -> {
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        }




        scaleBtn.setOnClickListener {
            if (isFullscreen < 1) isFullscreen += 1 else isFullscreen = 0
            when (isFullscreen) {
                0 -> {
                    if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    } else {
                        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    }
                }
                1 -> {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                }
            }

            Snackbar.make(
                binding.exoPlayerView, (
                        when (isFullscreen) {
                            0 -> "Original"
                            1 -> "Stretch"
                            else -> "Original"
                        }
                        ), 1000
            ).show()
        }


        exoPlay.setOnClickListener {
            if (isInit) {
                if (model.player.isPlaying) pauseVideo()
                else playVideo()

            }
        }
        nextEpBtn.setOnClickListener {
            setNewEpisode(1)
        }
        prevEpBtn.setOnClickListener {
            setNewEpisode(-1)
        }


        // Back Button
        playerView.findViewById<ImageButton>(R.id.exo_back).apply {
            setOnClickListener {
                model.player.release()
                finish()
            }
        }



        handleController()

    }

    private fun checkNotch() {
        if (notchHeight != 0) {
            val orientation = resources.configuration.orientation
            playerView.findViewById<View>(R.id.exo_controller_cont)
                .updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        marginStart = notchHeight
                        marginEnd = notchHeight
                        topMargin = 0
                    } else {
                        topMargin = notchHeight
                        marginStart = 0
                        marginEnd = 0
                    }
                }
            playerView.findViewById<View>(androidx.media3.ui.R.id.exo_buffering).translationY =
                (if (orientation == Configuration.ORIENTATION_LANDSCAPE) 0 else (notchHeight + 8f.px)).dp
            exoBrightnessCont.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginEnd =
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) notchHeight else 0
            }
            exoVolumeCont.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginStart =
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) notchHeight else 0
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun handleController() {
        val overshoot = AnimationUtils.loadInterpolator(this, R.anim.over_shoot)
        val controllerDuration = (1f * 200).toLong()

        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) !isInPictureInPictureMode else true) {
            if (playerView.isControllerFullyVisible) {
                ObjectAnimator.ofFloat(
                    playerView.findViewById(R.id.exo_controller),
                    "alpha",
                    1f,
                    0f
                )
                    .setDuration(controllerDuration).start()
                ObjectAnimator.ofFloat(
                    playerView.findViewById(R.id.exo_bottom_controllers),
                    "translationY",
                    0f,
                    128f
                )
                    .apply { interpolator = overshoot;duration = controllerDuration;start() }
                ObjectAnimator.ofFloat(
                    playerView.findViewById(R.id.exo_progress),
                    "translationY",
                    0f,
                    128f
                )
                    .apply { interpolator = overshoot;duration = controllerDuration;start() }
                ObjectAnimator.ofFloat(
                    playerView.findViewById(R.id.exo_top_controllers),
                    "translationY",
                    0f,
                    -128f
                )
                    .apply { interpolator = overshoot;duration = controllerDuration;start() }
                playerView.postDelayed({ playerView.hideController() }, controllerDuration)


            } else {


                playerView.showController()
                ObjectAnimator.ofFloat(
                    playerView.findViewById(R.id.exo_controller),
                    "alpha",
                    0f,
                    1f
                )
                    .setDuration(controllerDuration).start()
                ObjectAnimator.ofFloat(
                    playerView.findViewById(R.id.exo_bottom_controllers),
                    "translationY",
                    128f,
                    0f
                )
                    .apply { interpolator = overshoot;duration = controllerDuration;start() }
                ObjectAnimator.ofFloat(
                    playerView.findViewById(R.id.exo_progress),
                    "translationY",
                    128f,
                    0f
                )
                    .apply { interpolator = overshoot;duration = controllerDuration;start() }
                ObjectAnimator.ofFloat(
                    playerView.findViewById(R.id.exo_top_controllers),
                    "translationY",
                    -128f,
                    0f
                )
                    .apply { interpolator = overshoot;duration = controllerDuration;start() }
            }
        }
    }

    private fun lockScreen(locked: Boolean) {
        if (locked) {
            exoTopControllers.visibility = View.INVISIBLE
            exoMiddleControllers.visibility = View.INVISIBLE
            exoBottomControllers.visibility = View.INVISIBLE
            exoProgress.setForceDisabled(true)
        } else {
            exoProgress.setForceDisabled(false)
            exoTopControllers.visibility = View.VISIBLE
            exoMiddleControllers.visibility = View.VISIBLE
            exoBottomControllers.visibility = View.VISIBLE
        }
    }


    private fun setNewEpisode(increment: Int = 1) {
        animePlayingDetails.animeEpisodeIndex =
            "${animePlayingDetails.animeEpisodeIndex.toInt() + increment}"
        println(animePlayingDetails.animeEpisodeIndex)
        if (animePlayingDetails.animeEpisodeIndex.toInt() > animePlayingDetails.animeTotalEpisode.toInt() || animePlayingDetails.animeEpisodeIndex.toInt() < 1)
        else {
            model.setAnimeLink(
                sourceType,
                animePlayingDetails.animeUrl,
                animePlayingDetails.animeEpisodeMap[animePlayingDetails.animeEpisodeIndex] as String,
                listOf(animePlayingDetails.epType),
                true
            )
            prevEpBtn.setImageViewEnabled(animePlayingDetails.animeEpisodeIndex.toInt() >= 2)
            nextEpBtn.setImageViewEnabled(animePlayingDetails.animeEpisodeIndex.toInt() != animePlayingDetails.animeTotalEpisode.toInt())
            model.player.stop()
            updateEpisodeName()

        }
    }


    @SuppressLint("StringFormatInvalid")
    private fun changeVideoSpeed(byInt: Float) {
        model.player.playbackParameters = PlaybackParameters(byInt)
    }

    private fun parseExtra() {
        animePlayingDetails =
            intent.getSerializableExtra("EXTRA_EPISODE_DATA") as AnimePlayingDetails
    }

    private fun onPiPChanged(isInPictureInPictureMode: Boolean) {
        playerView.useController = !isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            orientationListener?.disable()
        } else {
            orientationListener?.enable()
        }
        if (isInit) {
            hideSystemBars()
            model.player.play()
        }
    }


    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        onPiPChanged(isInPictureInPictureMode)
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
        onPiPChanged(isInPictureInPictureMode)
        super.onPictureInPictureUiStateChanged(pipState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        onPiPChanged(isInPictureInPictureMode)
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    override fun onStop() {
        model.player.pause()

        super.onStop()

    }


    @SuppressLint("ViewConstructor")
    class ExtendedTimeBar(
        context: Context,
        attrs: AttributeSet?
    ) : DefaultTimeBar(context, attrs) {
        private var enabled = false
        private var forceDisabled = false
        override fun setEnabled(enabled: Boolean) {
            this.enabled = enabled
            super.setEnabled(!forceDisabled && this.enabled)
        }

        fun setForceDisabled(forceDisabled: Boolean) {
            this.forceDisabled = forceDisabled
            isEnabled = enabled
        }

        private var previewBitmap: Bitmap? = null
        private val previewPaint = Paint()

        init {
            // Preview image paint settings
            previewPaint.isFilterBitmap = true
            // Load or generate the preview image
            // Replace R.drawable.preview_image with your actual image resource
            previewBitmap = BitmapFactory.decodeResource(resources, R.drawable.anime_img)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // Preview image display logic during seek
            val duration = 0L
            if (duration > 0) {
                val position = 3L
                val relativePos =
                    if (duration == 0L) 0f else (position.toFloat() / duration.toFloat())
                val width = width
                val previewWidth = previewBitmap?.width ?: 0
                val previewHeight = previewBitmap?.height ?: 0

                // Calculate the position to draw the preview image
                val previewX = (relativePos * width - previewWidth / 2).toInt()
                val previewY = height - previewHeight

                // Display the preview image
                previewBitmap?.let {
                    canvas.drawBitmap(it, previewX.toFloat(), previewY.toFloat(), previewPaint)
                }
            }
        }
    }

    companion object {
        var sourceType = ""
        var pipStatus: Boolean = false
        private var isLocked: Boolean = false
        fun newIntent(
            context: Context,
            episodeData: AnimePlayingDetails,
        ): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("EXTRA_EPISODE_DATA", episodeData)
            return intent
        }
    }

    private fun playVideo() {
        model.player.play()
    }

    private fun pauseVideo() {
        model.player.pause()
    }


    public override fun onResume() {
        super.onResume()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        playerView.useController = true
        model.player.prepare()
    }

    public override fun onPause() {
        super.onPause()
        if (pipStatus) pauseVideo()
    }

    override fun onDestroy() {
        super.onDestroy()

        pipStatus = false
        model.player.stop()
        model.player.release()
        finishAndRemoveTask()
    }

    private val keyMap: MutableMap<Int, (() -> Unit)?> = mutableMapOf(
        KeyEvent.KEYCODE_DPAD_RIGHT to null,
        KeyEvent.KEYCODE_DPAD_LEFT to null,
        KeyEvent.KEYCODE_SPACE to { exoPlay.performClick() },
        KeyEvent.KEYCODE_N to { nextEpBtn.performClick() },
        KeyEvent.KEYCODE_B to { prevEpBtn.performClick() }
    )

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (keyMap.containsKey(event.keyCode)) {
            (event.action == KeyEvent.ACTION_UP).also {
                if (isInit && it) keyMap[event.keyCode]?.invoke()
            }
        } else {
            super.dispatchKeyEvent(event)
        }
    }

    private fun ImageView.setImageViewEnabled(enabled: Boolean) = if (enabled) {
        drawable.clearColorFilter()
        isEnabled = true
        isFocusable = true
    } else {
        drawable.colorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        isEnabled = false
        isFocusable = false
    }



}