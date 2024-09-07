package com.bcforward.mybanners.ui.banners.data

import com.bcforward.mybanners.data.remote.BannersResponse
import retrofit2.Response

class FakeBannersRepository(
    private val bannerService: FakeBannerService
) : BannersRepository(bannerService) {

    suspend fun getBannerList(): Response<List<BannersResponse>> {
        return bannerService.getBanners()
    }

    fun setBannersResponse(bannersResponse: List<BannersResponse>) {
        bannerService.setBannersResponse(bannersResponse)
    }

    fun setFetchBannersException(exception: Exception) {
        bannerService.setFetchBannersException(exception)
    }
}
