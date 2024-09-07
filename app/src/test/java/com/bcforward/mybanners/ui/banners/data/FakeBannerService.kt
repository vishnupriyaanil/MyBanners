package com.bcforward.mybanners.ui.banners.data

import com.bcforward.mybanners.data.remote.BannersResponse
import com.bcforward.mybanners.data.service.BannerService
import com.bcforward.mybanners.util.parseJson
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeBannerService(
    private var shouldReturnError: Boolean = false
) : BannerService {
    private var bannersResponse: List<BannersResponse> = parseJson("banners.json")
    private var fetchBannersException: Exception? = null

    override suspend fun getBanners(): Response<List<BannersResponse>> {
        fetchBannersException?.let { throw it }

        return if (shouldReturnError) {
            Response.error(500, "Server Error".toResponseBody(null))
        } else {
            Response.success(bannersResponse)
        }
    }

    fun setBannersResponse(banners: List<BannersResponse>) {
        this.bannersResponse = banners
        this.shouldReturnError = false
        this.fetchBannersException = null
    }

    fun setFetchBannersException(exception: Exception) {
        this.shouldReturnError = true
        this.fetchBannersException = exception
    }
}
