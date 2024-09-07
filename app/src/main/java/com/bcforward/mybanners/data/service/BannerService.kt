package com.bcforward.mybanners.data.service

import com.bcforward.mybanners.data.remote.BannersResponse
import retrofit2.Response
import retrofit2.http.GET


interface BannerService {
    @GET("b/5BEJ")
    suspend fun getBanners(
    ): Response<List<BannersResponse>>
}
