package com.example.webtrekk.androidsdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.webtrekk.androidsdk.databinding.SettingsActivityBinding

import webtrekk.android.sdk.Webtrekk

class SettingsExample : AppCompatActivity() {
    lateinit var binding:SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= SettingsActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.button.setOnClickListener {
            val stringIds = BuildConfig.TRACK_IDS
            val domain = BuildConfig.DOMEIN
            val elements: List<String> = stringIds.split(",")
            Webtrekk.getInstance().setIdsAndDomain(elements, domain)
        }

        binding.button2.setOnClickListener {
            Webtrekk.getInstance().setIdsAndDomain(
                listOf("826582930668809"),
                "http://vdestellaaccount01.wt-eu02.net"
            )

        }

        binding.button3.setOnClickListener {
            val stringIds = BuildConfig.TRACK_IDS
            val domain = BuildConfig.DOMEIN
            var elements: MutableList<String> = stringIds.split(",").toMutableList()
            elements.add("826582930668809")
            Webtrekk.getInstance().setIdsAndDomain(elements, domain)

        }

        binding.enableAnonymous.setOnClickListener {
            Webtrekk.getInstance().anonymousTracking(true, setOf("la", "cs804", "cs821"), generateNewEverId = false)
        }
        binding.disableAnonymous.setOnClickListener {
            Webtrekk.getInstance().anonymousTracking(false, generateNewEverId = false)
        }

        binding.swOptout.isChecked=Webtrekk.getInstance().hasOptOut()
        binding.swOptout.setOnCheckedChangeListener { buttonView, isChecked ->
            Webtrekk.getInstance().optOut(isChecked)
        }
    }
}