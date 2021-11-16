package com.example.webtrekk.androidsdk


import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.webtrekk.androidsdk.databinding.ActivityStandardVideoBinding
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.Webtrekk


class StandardVideoActivity : AppCompatActivity() {

    lateinit var binding:ActivityStandardVideoBinding
    private var myVideoView: TrackedVideoView? = null
    private var position = 0
    private var mediaControls: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStandardVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myVideoView = binding.videoView
        mediaControls = MediaController(this)
        try {
            myVideoView!!.setMediaController(mediaControls)
            mediaControls!!.setAnchorView(myVideoView)
            myVideoView!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.wt))
        } catch (e: Exception) {
        }

        myVideoView!!.requestFocus()
        myVideoView!!.setOnPreparedListener { mp ->
            mediaControls!!.show()
            myVideoView!!.seekTo(position)
            if (position == 0) {
                mp.start()
            } else {
                mp.pause()
            }
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("Position", binding.videoView.currentPosition)
        binding.videoView.pause()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        position = savedInstanceState.getInt("Position")
        binding.videoView.seekTo(position)
    }

    override fun onStop() {
        myVideoView?.trackingParams?.putAll(
            mapOf(
                MediaParam.MEDIA_POSITION to (myVideoView!!.trackingParams[MediaParam.MEDIA_DURATION]!!.toInt() / 1000).toString(),
                MediaParam.MEDIA_ACTION to "eof"
            )
        )
        Webtrekk.getInstance().trackMedia("video name", myVideoView?.trackingParams!!)
        super.onStop()
    }
}