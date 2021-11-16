package com.example.webtrekk.androidsdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.webtrekk.androidsdk.databinding.ActivityMediaExampleBinding

class MediaExample : AppCompatActivity() {
    lateinit var binding:ActivityMediaExampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMediaExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.button.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
            val intent = Intent(this, StandardVideoActivity::class.java)
            startActivity(intent)
        }

        binding.button3.setOnClickListener {
            val intent = Intent(this, MediaActivityExample::class.java)
            startActivity(intent)
        }
    }
}