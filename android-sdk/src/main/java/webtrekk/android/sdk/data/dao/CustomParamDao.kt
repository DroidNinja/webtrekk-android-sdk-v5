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

package webtrekk.android.sdk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import webtrekk.android.sdk.data.entity.CustomParam

/**
 * Dao interface has [CustomParam] database interactions.
 */
@Dao
internal interface CustomParamDao {

    @Insert
    suspend fun setCustomParams(customParams: List<CustomParam>): List<Long>

    @Query("SELECT * FROM custom_params")
    suspend fun getCustomParams(): List<CustomParam>

    @Query("SELECT * FROM custom_params WHERE track_id = :trackId")
    suspend fun getCustomParamsByTrackId(trackId: Long): List<CustomParam>

    @Query("DELETE FROM custom_params")
    suspend fun deleteAllCustomParams()
}
