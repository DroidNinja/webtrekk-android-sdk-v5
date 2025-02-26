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

package webtrekk.android.sdk

import android.webkit.JavascriptInterface
import webtrekk.android.sdk.extension.jsonToMap
import webtrekk.android.sdk.util.webtrekkLogger

/**
 * Use analytics in a WebView.
 *
 * From App to Web, we have to pass the ever Id [Webtrekk.getEverId] to the [WebView] to keep track of the current user visit.
 *
 * To do so, there are 2 options:
 *
 * First: Use [WebtrekkWebInterface], and set it up in your [WebView],
 * @sample webView.addJavascriptInterface(WebtrekkWebInterface(Webtrekk.getInstance()), WebtrekkWebInterface.TAG)
 * On the web, Pixel Web SDK expects [WebtrekkWebInterface.TAG] alongside [WebtrekkWebInterface.getEverId]. They must be sent in this way.
 * Also, you can implement this class with extra JavaScript methods, but the JavaScript interface name must be [WebtrekkWebInterface.TAG].
 * [WebtrekkWebInterface.trackCustomEvent]   [WebtrekkWebInterface.trackCustomPage] [WebtrekkWebInterface.getUserAgent]
 * Second: Append "wt_eid" with ever Id [Webtrekk.getEverId] to the URL that will be loaded by the [WebView].
 * @sample webView.loadUrl("https://your_website_url.com/?wt_eid=the ever id")
 *
 * Note, you must enable JavaScript when uses this feature
 * @sample webView.settings.javaScriptEnabled = true
 *
 */
open class WebtrekkWebInterface(private val webtrekk: Webtrekk) {
    /**
     * Returns the ever Id, that will be used by Pixel Web SDK, to continue the current user visit.
     */
    @JavascriptInterface
    fun getEverId(): String {
        return webtrekk.getEverId()
    }

    /**
     * Track custom page coming from Smart Pixel Web SDK.
     *
     * @param pageName the page name in the web view.
     * @param params a json string has all the custom params coming from Smart Pixel.
     */
    @JavascriptInterface
    fun trackCustomPage(pageName: String, params: String) {
        try {
            webtrekk.trackCustomPage(pageName, params.jsonToMap())
        } catch (e: Exception) {
            webtrekkLogger.info(
                e.message
                    ?: "Unknown exception caught in WebView while tracking custom page"
            )
        }
    }

    /**
     * Track custom event coming from Smart Pixel Web SDK.
     *
     * @param eventName the event name happening in the web view.
     * @param params a json string has all the custom params coming from Smart Pixel.
     */
    @JavascriptInterface
    fun trackCustomEvent(eventName: String, params: String) {
        try {
            webtrekk.trackCustomEvent(eventName, params.jsonToMap())
        } catch (e: Exception) {
            webtrekkLogger.info(
                e.message
                    ?: "Unknown exception caught in WebView while tracking custom event"
            )
        }
    }

    /**
     * Returns Webtrekk specific UserAgent to Smart Pixel SDK.
     */
    @JavascriptInterface
    fun getUserAgent(): String {
        return webtrekk.getUserAgent()
    }

    companion object {
        /**
         *  The JavaScript interface name that Pixel Web SDK expects.
         */
        const val TAG = "WebtrekkAndroidWebViewCallback"
    }
}
