package com.example.webtrekk.androidsdk

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video.playerView
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import kotlin.math.max


class VideoActivity : AppCompatActivity(), View.OnClickListener,
    PlayerControlView.VisibilityListener {
    val TUNNELING_EXTRA = "tunneling"
    private val TAG = "VideoActivity"
    private var player: ExoPlayer? = null
    private var mediaSource: MediaSource? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private var startAutoPlay = false
    private var startWindow = 0
    private var startPosition: Long = 0
    private var muted = false
    private var isPlayingNow = true
    val trackingParams = TrackingParams()
    private val url =
        "https://firebasestorage.googleapis.com/v0/b/videostreaming-481cd.appspot.com/o/Mobile_Rich_Push.mp4?alt=media&token=3e9156fa-5af7-4344-9efb-7a8c882ab2dc";


    private val eventListener: Player.Listener = object : Player.Listener {
        fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            Log.i(TAG, "onTimelineChanged")
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            Log.i(TAG, "onTracksChanged")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Log.i(TAG, "onLoadingChanged")
        }

        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            Log.i(
                TAG,
                "onPlayerStateChanged: playWhenReady = $playWhenReady playbackState = $playbackState"
            )
            when (playbackState) {
                ExoPlayer.STATE_ENDED -> {
                    Log.i(TAG, "Playback ended!")
                    //Stop playback and return to start position

                    trackingParams.putAll(
                        mapOf(
                            MediaParam.MEDIA_POSITION to (player!!.currentPosition / 1000).toString(),
                            MediaParam.MEDIA_ACTION to "eof"
                        )
                    )
                    Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
                    clearStartPosition()
                }
                ExoPlayer.STATE_READY -> {

                }
                ExoPlayer.STATE_BUFFERING -> Log.i(TAG, "Playback buffering!")
                ExoPlayer.STATE_IDLE -> Log.i(TAG, "ExoPlayer idle!")
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            isPlayingNow = isPlaying
            trackingParams.putAll(
                mapOf(
                    MediaParam.MEDIA_DURATION to (player!!.duration / 1000).toString(),
                    MediaParam.MEDIA_POSITION to (player!!.currentPosition / 1000).toString(),
                    MediaParam.MEDIA_ACTION to if (isPlaying) "play" else "pause"
                )
            )
            Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
        }

        override fun onVolumeChanged(volume: Float) {
            super.onVolumeChanged(volume)
            trackingParams.putAll(
                mapOf(
                    MediaParam.MEDIA_POSITION to (player!!.currentPosition / 1000).toString(),
                    MediaParam.VOLUME to volume.toInt().toString(),
                    MediaParam.MUTE to if (volume.toInt() == 0) "1" else "0"
                )

            )
            Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        playerView.setControllerVisibilityListener(this)
        playerView.requestFocus()

        val builder = ParametersBuilder( /* context= */this)
        val tunneling = intent.getBooleanExtra(
            TUNNELING_EXTRA,
            false
        )
        if (Util.SDK_INT >= 21 && tunneling) {
//            builder.setTunnelingAudioSessionId(C.generateAudioSessionIdV21( /* context= */this))
        }
        trackSelectorParameters = builder.build()
        clearStartPosition()
    }

    private fun buildMediaSource(): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this, null)
        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        return mediaSourceFactory.createMediaSource(mediaItem)
    }

    override fun onClick(v: View?) {

    }

    override fun onVisibilityChange(visibility: Int) {

    }

    private fun updateStartPosition() {
        player?.let {
            startAutoPlay = it.playWhenReady
            startWindow = it.currentMediaItemIndex
            startPosition = max(0, it.currentPosition)
        }
    }

    private fun updateTrackSelectorParameters() {
        trackSelector.let {
            trackSelectorParameters = it?.parameters
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    private val analyticsListener = object : AnalyticsListener {
        override fun onSeekProcessed(eventTime: AnalyticsListener.EventTime) {
        }

        override fun onSeekStarted(eventTime: AnalyticsListener.EventTime) {
            trackingParams.putAll(
                mapOf(
                    MediaParam.MEDIA_POSITION to (eventTime.currentPlaybackPositionMs / 1000).toString(),
                    MediaParam.MEDIA_ACTION to "seek"
                )
            )
            Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
        }
    }

    private val eventLoggerAnalyticsListener = EventLogger(trackSelector)

    // Internal methods
    private fun initializePlayer() {
        if (player == null) {
            val trackSelectionFactory = AdaptiveTrackSelection.Factory()
            trackSelector = DefaultTrackSelector( /* context= */this, trackSelectionFactory).apply {
                parameters = trackSelectorParameters!!
            }

            mediaSource = buildMediaSource()
            if (mediaSource == null) {
                return
            }

            player = ExoPlayer.Builder( /* context= */this)
                .setTrackSelector(trackSelector!!)
                .build().apply {
                    lastSeenTrackGroupArray = null

                    addListener(eventListener)
                    setAudioAttributes(
                        AudioAttributes.DEFAULT,  /* handleAudioFocus= */
                        true
                    )
                    playWhenReady = startAutoPlay

                }
        }

        player?.let {
            it.addAnalyticsListener(eventLoggerAnalyticsListener)
            it.addAnalyticsListener(analyticsListener)
            playerView.player = player
            it.setMediaSource(mediaSource!!)
            it.prepare()
        }

        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (player!!.currentPosition / 1000).toString()
            )
        )
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            playerView?.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            playerView?.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (player!!.currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "stop"
            )
        )
        Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
        if (Util.SDK_INT <= 23) {
            playerView?.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            playerView?.onPause()
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        Log.d(TAG, "releasePlayer")
        trackingParams.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (player!!.currentPosition / 1000).toString(),
                MediaParam.MEDIA_ACTION to "eof"
            )
        )
        Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
        updateTrackSelectorParameters()
        updateStartPosition()
        player?.let {
            it.removeAnalyticsListener(eventLoggerAnalyticsListener)
            it.removeAnalyticsListener(analyticsListener)
            it.stop()
            it.clearMediaItems()
            it.clearVideoSurface()
            it.release()
            //mediaSource = null
            //trackSelector = null
        }.also {
            player = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateTrackSelectorParameters()
        updateStartPosition()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MUTE || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            trackingParams.putAll(
                mapOf(
                    MediaParam.MEDIA_POSITION to (player!!.currentPosition / 1000).toString(),
                    MediaParam.MUTE to (if (muted) "1" else "0")
                )
            )
            muted = !muted
            Webtrekk.getInstance().trackMedia("VideoActivity", "video name", trackingParams)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}