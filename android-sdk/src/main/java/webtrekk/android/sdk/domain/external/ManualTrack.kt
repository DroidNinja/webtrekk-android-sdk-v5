/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package webtrekk.android.sdk.domain.external

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import webtrekk.android.sdk.Param
import webtrekk.android.sdk.core.Sessions
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequest
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.module.AppModule
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * The manual track use case. NOTE that manual track won't work if auto track is enabled because manual track works if [Context] is provided, and intentionally it's used to track activity/fragment, while [TrackCustomPage] could be used to track any custom page because it's not dependent on [Context].
 */
internal class ManualTrack(
    coroutineContext: CoroutineContext,
    private val sessions: Sessions,
    private val cacheTrackRequest: CacheTrackRequest,
    private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams
) : ExternalInteractor<ManualTrack.Params> {

    private val _job = Job()
    override val scope =
        CoroutineScope(_job + coroutineContext) // Starting a new job with context of the parent.

    /**
     * [logger] the injected logger from Webtrekk.
     */
    private val logger by lazy { AppModule.logger }

    override operator fun invoke(invokeParams: Params, coroutineDispatchers: CoroutineDispatchers) {
        // If opt out is active, then return
        if (invokeParams.isOptOut) return

        // If auto track is enabled, then return
//        if (invokeParams.autoTrack) {
//            logger.warn("Auto track is enabled, call 'disableAutoTrack()' in the configurations to disable the auto tracking")
//
//            return
//        }

        scope.launch(
            coroutineDispatchers.ioDispatcher + coroutineExceptionHandler(
                logger
            )
        ) {

            val params = invokeParams.trackingParams.toMutableMap()
            if (!params.containsKey(Param.MEDIA_CODE))
                params.putAll(sessions.getUrlKey())
            // If there are no custom param, then cache as a single track request.
            if (params.isEmpty()) {
                cacheTrackRequest(CacheTrackRequest.Params(invokeParams.trackRequest))
                    .onSuccess { logger.debug("Cached the track request: $it") }
                    .onFailure { logger.warn("Error while caching the request: $it") }
            } else { // If has custom params, then cache as a track request with custom params.
                cacheTrackRequestWithCustomParams(
                    CacheTrackRequestWithCustomParams.Params(
                        invokeParams.trackRequest,
                        params
                    )
                )
                    .onSuccess { logger.debug("Cached the data track request: $it") }
                    .onFailure { logger.warn("Error while caching the request: $it") }
            }
        }
    }

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param trackRequest the track request that is created and will be cached in the data base.
     * @param trackingParams the custom params associated with the [trackRequest].
     * @param autoTrack the auto track config value.
     * @param isOptOut the opt out value.
     */
    data class Params(
        val trackRequest: TrackRequest,
        val trackingParams: Map<String, String>,
        val autoTrack: Boolean,
        val isOptOut: Boolean,
        val context: Context? = null
    )
}
