package webtrekk.android.sdk

import androidx.work.Constraints
import webtrekk.android.sdk.extension.nullOrEmptyIsError
import webtrekk.android.sdk.extension.validateList
import java.util.concurrent.TimeUnit

class WebtrekkConfiguration private constructor(
    override val trackIds: List<String>,
    override val trackDomain: String,
    override val logLevel: Logger.Level,
    override val sendDelay: Long,
    override val autoTracking: Boolean,
    override val workManagerConstraints: Constraints
) : Config {

    class Builder(private val trackIds: List<String>, private val trackDomain: String) {
        private var logLevel = DefaultConfiguration.logLevel
        private var sendDelay = DefaultConfiguration.timeUnit.toMillis(DefaultConfiguration.sendDelay)
        private var autoTracking = DefaultConfiguration.enabledAutoTrack
        private var constraints = DefaultConfiguration.workManagerConstraints

        @JvmName("setLogLevel")
        fun logLevel(logLevel: Logger.Level) = apply { this.logLevel = logLevel }

        @JvmName("setSendDelay")
        fun sendDelay(timeUnit: TimeUnit = TimeUnit.MINUTES, sendDelay: Long) =
            apply { this.sendDelay = timeUnit.toMillis(sendDelay) }

        fun disableAutoTracking() = apply { this.autoTracking = false }

        @JvmName("setWorkManagerConstraints")
        fun workManagerConstraints(constraints: Constraints) =
            apply { this.constraints = constraints }

        fun build() = WebtrekkConfiguration(
            trackIds.validateList("trackId"),
            trackDomain.nullOrEmptyIsError("trackDomain"),
            logLevel,
            sendDelay,
            autoTracking,
            constraints
        )
    }
}
