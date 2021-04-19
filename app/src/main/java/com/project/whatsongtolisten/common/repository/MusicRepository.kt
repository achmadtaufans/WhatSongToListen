package com.project.whatsongtolisten.common.repository

import com.project.whatsongtolisten.common.api.UseCaseResult
import com.project.whatsongtolisten.common.data.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicService {
    @GET("search")
    fun getSongs(
        @Query("term") artist_name: String,
        @Query("entity") entity_type: String
    ): Deferred<ApiResponse>
}

class MusicRepository(private val musicApi: MusicService) {
    suspend fun getSongs(name: String): UseCaseResult<ApiResponse> {
        return try {
            val result = musicApi.getSongs(name, "musicVideo").await()
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}