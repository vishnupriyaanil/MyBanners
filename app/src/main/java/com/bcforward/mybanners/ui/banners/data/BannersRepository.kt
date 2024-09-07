package com.bcforward.mybanners.ui.banners.data

import com.bcforward.mybanners.data.remote.BannersResponse
import com.bcforward.mybanners.data.service.BannerService
import java.io.IOException

open class BannersRepository(
    private val bannerService: BannerService
) {

    suspend fun getBanners(): Result<List<BannersResponse>> {
        return try {
            val response = bannerService.getBanners()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load banners"))
            }
        } catch (exception: IOException) {
            Result.failure(exception)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}